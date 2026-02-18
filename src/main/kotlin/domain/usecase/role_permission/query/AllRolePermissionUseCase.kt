package kz.kff.domain.usecase.role_permission.query

import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.domain.dto.role_permission.RolePermissionWithDetailsRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRolePermissionWithDetailsRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.role_permission.RolePermissionFilter

class AllRolePermissionUseCase(
    private val rolePermissionDatasource: RolePermissionDatasource
): UseCaseTransaction() {

     suspend operator fun invoke(filter: RolePermissionFilter): List<RolePermissionWithDetailsRDTO>{
        return tx {
            rolePermissionDatasource.findAllWithFilter(
                filter,
                mapper = eachRow { it.toRolePermissionWithDetailsRDTO() }
            )
        }
    }
}