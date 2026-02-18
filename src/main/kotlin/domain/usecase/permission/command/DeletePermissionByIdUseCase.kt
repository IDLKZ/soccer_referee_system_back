package kz.kff.domain.usecase.permission.command

import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toPermissionSDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeletePermissionByIdUseCase(
    private val permissionDatasource: PermissionDatasource,
) : UseCaseTransaction() {

    operator suspend fun invoke(id: Long): Boolean = tx {
        val existing = permissionDatasource.findByLongId(id = id, mapper = eachRow { it.toPermissionSDTO() })
        if (existing == null) {
            throw ApiBadRequestException(messageKey = LocalizedMessageConstraints.ErrorNotFoundMessage)
        }
        permissionDatasource.delete(id = id, hardDelete = true)
    }
}
