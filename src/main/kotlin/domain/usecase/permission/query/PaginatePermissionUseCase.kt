package kz.kff.domain.usecase.permission.query

import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter

class PaginatePermissionUseCase(
    private val permissionDatasource: PermissionDatasource
) : UseCaseTransaction() {
    suspend operator fun invoke(filter: PermissionFilter): PaginationMeta<PermissionRDTO> = tx {
        permissionDatasource.paginate(filter, eachRow { it.toPermissionRDTO() })
    }
}
