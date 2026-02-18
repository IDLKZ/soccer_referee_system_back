package kz.kff.infrastructure.datasource.db.role_permission

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.infrastructure.datasource.db.BaseDbDatasourceImpl
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll

class RolePermissionDatasourceImpl(
    table: RolePermissionTable,
) : BaseDbDatasourceImpl<RolePermissionTable>(table), RolePermissionDatasource {

    override fun baseJoinQuery(): Query {
        return table
            .leftJoin(
                PermissionTable,
            )
            .leftJoin(
                RoleTable,
            )
            .selectAll()
    }
}