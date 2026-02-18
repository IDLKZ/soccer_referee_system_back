package kz.kff.core.config

import io.ktor.server.config.ApplicationConfig

class StorageConfig(
    val useStorage: Boolean,
    val uploadDir: String,
    val accessDir: String,
    val maxFileSizeBytes: Long,
    val allowedExtensions: Set<String>,
    val forbiddenExtensions: Set<String>
) {
    companion object {
        fun configure(config: ApplicationConfig): StorageConfig {
            val storage = config.config("storage")
            val maxSizeMb = storage.propertyOrNull("maxFileSizeMb")?.getString()?.toLongOrNull() ?: 10
            return StorageConfig(
                useStorage = config.propertyOrNull("useStorage")?.getString()?.toBoolean() ?: false,
                uploadDir = storage.propertyOrNull("uploadDir")?.getString() ?: "uploads",
                accessDir = storage.propertyOrNull("accessDir")?.getString() ?: "/uploads",
                maxFileSizeBytes = maxSizeMb * 1024 * 1024,
                allowedExtensions = storage.propertyOrNull("allowedExtensions")
                    ?.getList()
                    ?.map { it.lowercase() }
                    ?.toSet() ?: emptySet(),
                forbiddenExtensions = storage.propertyOrNull("forbiddenExtensions")
                    ?.getList()
                    ?.map { it.lowercase() }
                    ?.toSet() ?: setOf("exe", "bat", "sh", "cmd", "ps1", "jar")
            )
        }
    }
}