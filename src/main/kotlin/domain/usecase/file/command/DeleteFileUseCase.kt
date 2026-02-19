package kz.kff.domain.usecase.file.command

import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeleteFileUseCase(
    private val fileService: FileService,
    private val fileDatasource: FileDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(id: Long, deleteOldFile: Boolean): Boolean {
        return tx {
            val existingFile = fileDatasource.findByLongId(id = id, mapper = eachRow { it.toFileRDTO() })
                ?: throw ApiBadRequestException(LocalizedMessageConstraints.ErrorNotFoundMessage)
            if (deleteOldFile) {
                fileService.delete(existingFile.uniqueFileName)
            }
            fileDatasource.delete(id)
        }
    }
}
