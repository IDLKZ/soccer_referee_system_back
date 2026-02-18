package kz.kff.core.shared.constraints

sealed class LocalFileExtensions(
    val identifier: String,
    val extensions: Set<String>,
) {

    object Image : LocalFileExtensions(
        identifier = "image",
        setOf(
            ".jpg", ".jpeg", ".png", ".gif",
            ".webp", ".avif", ".svg", ".bmp",
            ".tiff", ".ico", ".heic"
        )
    )

    object Document : LocalFileExtensions(
        identifier = "document",
        setOf(
            ".txt", ".pdf", ".doc", ".docx",
            ".xls", ".xlsx", ".ppt", ".pptx",
            ".csv", ".md", ".rtf"
        )
    )

    object Archive : LocalFileExtensions(
        identifier = "archive",
        setOf(
            ".zip", ".rar", ".7z",
            ".tar", ".gz", ".tar.gz", ".bz2"
        )
    )

    object Audio : LocalFileExtensions(
        identifier = "audio",
        setOf(
            ".mp3", ".wav", ".ogg",
            ".flac", ".aac", ".m4a"
        )
    )

    object Video : LocalFileExtensions(
        identifier = "video",
        setOf(
            ".mp4", ".webm", ".mkv",
            ".avi", ".mov", ".wmv"
        )
    )

    object Web : LocalFileExtensions(
        identifier = "web",
        setOf(
            ".html", ".css", ".js",
            ".json", ".xml", ".yaml", ".yml"
        )
    )
    companion object {
        private val all = listOf(Image, Document, Archive, Audio, Video, Web)

        fun fromIdentifier(identifier: String): LocalFileExtensions? {
            return all.find { it.identifier == identifier }
        }

        fun getExtensions(identifier: String): List<String> {
            return fromIdentifier(identifier)?.extensions?.toList() ?: emptyList()
        }
    }
}


