package kz.kff.infrastructure.datasource.service.file

import kz.kff.core.config.StorageConfig
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.shared.constraints.LocalFileExtensions
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.dto.file.FileCDTO
import kz.kff.domain.dto.file.FileRDTO
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

class FileServiceImpl(private val config: StorageConfig) : FileService {

    private val storageDir: Path = Paths.get(config.uploadDir).toAbsolutePath().normalize()

    init {
        Files.createDirectories(storageDir)
    }

    override suspend fun store(
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
        skipIfNotValidate: Boolean,
        maxSizeMb: Long?,
        folder: String?,
        allowedExtensions: Set<String>?,
    ): FileCDTO {
        if (!skipIfNotValidate) {
            validateFile(fileName, bytes.size.toLong(), maxSizeMb, allowedExtensions)
        }

        val extension = extractExtension(fileName)
        val uniqueName = buildString {
            append(UUID.randomUUID())
            if (extension.isNotEmpty()) append(".$extension")
        }

        val targetDir: Path = if (folder != null) {
            storageDir.resolve(folder).normalize()
        } else {
            storageDir
        }

        if (!targetDir.startsWith(storageDir)) {
            throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
        }

        Files.createDirectories(targetDir)

        val targetPath = targetDir.resolve(uniqueName)

        try {
            Files.write(targetPath, bytes)
        } catch (e: Exception) {
            throw ApiInternalException(
                messageKey = LocalizedMessageConstraints.ErrorFileUpload,
                detail = e.message
            )
        }

        val accessPath = buildString {
            append(config.accessDir.trimEnd('/'))
            if (folder != null) append("/${folder.replace('\\', '/')}")
            append("/$uniqueName")
        }

        return FileCDTO(
            originalName = fileName,
            uniqueFileName = uniqueName,
            fullPath = accessPath,
            directory = folder,
            extension = if (extension.isNotEmpty()) extension else null,
            mimeType = mimeType,
            storageLocal = true,
            fileSizeByte = bytes.size.toLong(),
        )
    }

    override suspend fun store(
        fileName: String,
        mimeType: String,
        inputStream: InputStream,
        skipIfNotValidate: Boolean,
        maxSizeMb: Long?,
        folder: String?,
        allowedExtensions: Set<String>?,
    ): FileCDTO = store(
        fileName = fileName,
        mimeType = mimeType,
        bytes = inputStream.use { it.readBytes() },
        skipIfNotValidate = skipIfNotValidate,
        maxSizeMb = maxSizeMb,
        folder = folder,
        allowedExtensions = allowedExtensions,
    )

    override suspend fun load(uniqueFileName: String): ByteArray {
        val file = resolveFile(uniqueFileName)
        return try {
            Files.readAllBytes(file)
        } catch (e: Exception) {
            throw ApiInternalException(
                messageKey = LocalizedMessageConstraints.ErrorFileUpload,
                detail = e.message
            )
        }
    }

    override suspend fun loadAsStream(uniqueFileName: String): InputStream {
        val file = resolveFile(uniqueFileName)
        return try {
            Files.newInputStream(file)
        } catch (e: Exception) {
            throw ApiInternalException(
                messageKey = LocalizedMessageConstraints.ErrorFileUpload,
                detail = e.message
            )
        }
    }

    override suspend fun delete(uniqueFileName: String): Boolean {
        return try {
            val file = resolveFile(uniqueFileName)
            Files.deleteIfExists(file)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun exists(uniqueFileName: String): Boolean {
        return try {
            val file = resolveFile(uniqueFileName)
            Files.exists(file)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getFileInfo(uniqueFileName: String): FileRDTO? {
        return try {
            val file = resolveFile(uniqueFileName)
            if (!Files.exists(file)) null else null // metadata lives in DB repository
        } catch (e: Exception) {
            null
        }
    }

    override fun getStorageDir(): String = storageDir.toString()

    override fun getConfig(): StorageConfig = config


    // === Private Methods ===

    private fun validateFile(
        fileName: String,
        size: Long,
        maxSizeMb: Long? = null,
        allowedExtensions: Set<String>? = null,
    ) {
        val maxBytes = if (maxSizeMb != null) maxSizeMb * 1024 * 1024 else config.maxFileSizeBytes
        if (size > maxBytes) {
            throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
        }

        val extension = extractExtension(fileName).lowercase()
        if (config.forbiddenExtensions.contains(extension)) {
            throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
        }

        validateExtensions(fileName, allowedExtensions)
    }

    private fun validateExtensions(fileName: String, allowedExtensions: Set<String>? = null) {
        if (allowedExtensions != null) {
            val extension = extractExtension(fileName).lowercase()
            val allowed = allowedExtensions
                .flatMap { identifier -> LocalFileExtensions.getExtensions(identifier) }
                .map { it.lowercase().trimStart('.') }
                .toSet()
            if (extension.isEmpty() || extension !in allowed) {
                throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
            }
        }
    }

    private fun extractExtension(fileName: String): String {
        return fileName.substringAfterLast(".", "")
    }

    private fun resolveFile(uniqueFileName: String): Path {
        val direct = storageDir.resolve(uniqueFileName).normalize()
        if (direct.startsWith(storageDir) && Files.exists(direct)) {
            return direct
        }
        Files.walk(storageDir).use { stream ->
            return stream
                .filter { Files.isRegularFile(it) && it.fileName.toString() == uniqueFileName }
                .findFirst()
                .orElseThrow {
                    ApiInternalException(messageKey = LocalizedMessageConstraints.ErrorFileUpload)
                }
        }
    }
}
