package kz.kff.core.db.table.file

import kz.kff.core.db.table.BasicLongTable
import kz.kff.core.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.Column

object FileTable : BasicLongTable("files") {
    val originalName: Column<String> = text("original_name")
    val uniqueFileName:Column<String> = text("unique_name")
    val fullPath:Column<String> = text("full_path")
    val directory:Column<String?> = text("directory").nullable()
    val extension:Column<String?> = text("extension").nullable()
    val mimeType:Column<String?> = text("mimeType").nullable()
    val storedLocal:Column<Boolean> = bool("stored_local").default(true)
    val fileSizeByte: Column<Long> = long("file_size_byte")
}