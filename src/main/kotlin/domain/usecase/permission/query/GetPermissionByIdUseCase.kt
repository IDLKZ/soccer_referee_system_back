package kz.kff.domain.usecase.permission.query

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class GetPermissionByIdUseCase(
    private val permissionDatasource: PermissionDatasource
) : UseCaseTransaction() {
    suspend operator fun invoke(id: Long): PermissionRDTO = tx {
        permissionDatasource.findByLongId(id, mapper = eachRow { it.toPermissionRDTO() })
            ?: throw ApiNotFoundException(LocalizedMessageConstraints.PermissionNotFoundMessage)
    }
}
