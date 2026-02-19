package kz.kff.core.shared.utils.file_utils

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.dto.file.FileCDTO


suspend fun uploadOneDocument(
    multipart: MultiPartData,
    fileService: FileService,
    partFileName: String = "file",
    skipIfNotValidate: Boolean = true,
    folder: String? = null,
    extensions: Set<String>? = null,
    maxSizeMb: Long? = null,
): FileCDTO?{
    var fileDTO: FileCDTO? = null
    multipart.forEachPart { part ->
        if (part is PartData.FileItem && part.name == partFileName){
            val fileName = part.originalFileName
                ?: throw ApiBadRequestException(LocalizedMessageConstraints.FileNameRequiredFileUpload)

            val mimeType = part.contentType?.toString()
                ?: "application/octet-stream"


            part.streamProvider().use { inputStream ->
                fileDTO = fileService.store(
                    fileName, mimeType, inputStream,
                    skipIfNotValidate = skipIfNotValidate,
                    maxSizeMb = maxSizeMb,
                    folder = folder,
                    allowedExtensions = extensions
                )
            }
        }
        part.dispose()
    }
    return fileDTO
}

suspend fun uploadDocuments(
    multipart: MultiPartData,
    fileService: FileService,
    partFileName: String = "files",
    skipIfNotValidate: Boolean = true,
    folder: String? = null,
    extensions: Set<String>? = null,
    maxSizeMb: Long? = null,
): List<FileCDTO> {

    val uploadedFiles = mutableListOf<FileCDTO>()

    multipart.forEachPart { part ->
        try {
            if (part is PartData.FileItem && part.name == partFileName) {

                val originalFileName = part.originalFileName
                    ?: throw ApiBadRequestException(
                        LocalizedMessageConstraints.FileNameRequiredFileUpload
                    )

                val mimeType = part.contentType?.toString()
                    ?: "application/octet-stream"

                val storedFile = part.streamProvider().use { inputStream ->
                    fileService.store(
                        originalFileName,
                        mimeType,
                        inputStream,
                        skipIfNotValidate = skipIfNotValidate,
                        maxSizeMb = maxSizeMb,
                        folder = folder,
                        allowedExtensions = extensions
                    )
                }

                uploadedFiles.add(storedFile)
            }
        } finally {
            part.dispose()
        }
    }
    return uploadedFiles
}
