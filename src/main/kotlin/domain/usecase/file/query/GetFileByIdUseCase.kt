package kz.kff.domain.usecase.file.query

import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class GetFileByIdUseCase(
    private val fileDatasource: FileDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(id: Long): FileRDTO {
        return tx {
            fileDatasource.findByLongId(id = id, mapper = eachRow { it.toFileRDTO() })
                ?: throw ApiBadRequestException(LocalizedMessageConstraints.ErrorNotFoundMessage)
        }
    }
}
