package kz.kff.domain.datasource.service.file

import kz.kff.core.config.StorageConfig
import kz.kff.domain.dto.file.FileCDTO
import kz.kff.domain.dto.file.FileRDTO
import java.io.InputStream

interface FileService {
    suspend fun store(
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
        skipIfNotValidate: Boolean,
        maxSizeMb: Long? = null,
        folder:String? = null,
        allowedExtensions: Set<String>? = null,
    ): FileCDTO

    suspend fun store(
        fileName: String,
        mimeType: String,
        inputStream: InputStream,
        skipIfNotValidate: Boolean,
        maxSizeMb: Long? = null,
        folder: String? = null,
        allowedExtensions: Set<String>? = null,
    ): FileCDTO

    suspend fun load(uniqueFileName: String): ByteArray
    suspend fun loadAsStream(uniqueFileName: String): InputStream
    suspend fun delete(uniqueFileName: String): Boolean
    suspend fun exists(uniqueFileName: String): Boolean
    suspend fun getFileInfo(uniqueFileName: String): FileRDTO?
    fun getStorageDir(): String
    fun getConfig(): StorageConfig



}