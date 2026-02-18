package kz.kff.domain.usecase.permission.query

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter

class GetPermissionByValueUseCase(
    private val permissionDatasource: PermissionDatasource
) : UseCaseTransaction() {
    suspend operator fun invoke(value: String): PermissionRDTO = tx {
        permissionDatasource.findOneByFilter(
            filter = PermissionFilter.byValue(value),
            mapper = { it.toPermissionRDTO() }
        ) ?: throw ApiNotFoundException(LocalizedMessageConstraints.PermissionNotFoundMessage)
    }
}
