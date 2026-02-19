package kz.kff.domain.usecase.file.command

import io.ktor.http.content.MultiPartData
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.core.shared.utils.file_utils.uploadDocuments
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.file.FileCDTO
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class BulkCreateFileUseCase (
    private val fileService: FileService,
    private val fileDatasource: FileDatasource
): UseCaseTransaction() {

    suspend operator fun invoke(skipIfNotValidate: Boolean, folder: String?, multipart: MultiPartData): List<FileRDTO> {
        var filesDTO: List<FileCDTO>? = uploadDocuments(
            multipart = multipart,
            fileService = fileService,
            skipIfNotValidate = skipIfNotValidate,
            folder = folder,
        )
        return tx {
            if (filesDTO == null) {
                throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
            }
            if(filesDTO.isEmpty()){
                throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
            }
            fileDatasource.bulkCreate(
                items = filesDTO,
                insertBlock = { dto -> applyMap(dto.bindMap(FileTable)) },
                mapper = { it.toFileRDTO() }
            )
        }
    }
}