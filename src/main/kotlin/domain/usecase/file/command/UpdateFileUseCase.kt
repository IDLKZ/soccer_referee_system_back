package kz.kff.domain.usecase.file.command

import io.ktor.http.content.MultiPartData
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.core.shared.utils.file_utils.uploadOneDocument
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.dto.file.FileCDTO
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class UpdateFileUseCase(
    private val fileService: FileService,
    private val fileDatasource: FileDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(
        id: Long,
        multipart: MultiPartData,
        deleteOldFile: Boolean,
        skipIfNotValidate: Boolean,
        folder: String?,
    ): FileRDTO {
        var fileDTO: FileCDTO? = uploadOneDocument(
            multipart = multipart,
            fileService = fileService,
            skipIfNotValidate = skipIfNotValidate,
            folder = folder,
        )
        if (fileDTO == null) {
            throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
        }
        return tx {
            val existingFile: FileRDTO? = fileDatasource.findByLongId(id=id, mapper = eachRow { it.toFileRDTO() })
            if(existingFile == null) {
                throw ApiBadRequestException(LocalizedMessageConstraints.ErrorNotFoundMessage)
            }
            if(deleteOldFile){
                fileService.delete(existingFile.uniqueFileName)
            }
            fileDatasource.update(
                id=id,
                updateBlock = { fileDTO.applyUpdate(FileTable, this) },
                mapper = {it->it.toFileRDTO()}
            )?: throw ApiInternalException()

        }

    }
}