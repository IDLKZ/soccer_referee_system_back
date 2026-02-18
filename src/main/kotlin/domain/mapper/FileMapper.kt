package kz.kff.domain.mapper
import kz.kff.core.db.table.file.FileTable
import kz.kff.domain.dto.file.FileRDTO
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toFileRDTO(): FileRDTO {
    return FileRDTO(
        id = this[FileTable.id].value,
        originalName = this[FileTable.originalName],
        uniqueFileName = this[FileTable.uniqueFileName],
        fullPath = this[FileTable.fullPath],
        directory = this[FileTable.directory],
        extension = this[FileTable.extension],
        mimeType = this[FileTable.mimeType],
        storageLocal = this[FileTable.storedLocal],
        fileSizeByte = this[FileTable.fileSizeByte],
        createdAt = this[FileTable.createdAt],
        updatedAt = this[FileTable.updatedAt],
    )
}