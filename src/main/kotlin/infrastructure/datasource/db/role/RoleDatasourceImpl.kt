package kz.kff.infrastructure.datasource.db.role

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.infrastructure.datasource.db.BaseDbDatasourceImpl
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll

class RoleDatasourceImpl(
    table: RoleTable
) : BaseDbDatasourceImpl<RoleTable>(table), RoleDatasource {

    override fun baseJoinQuery(): Query {
        return table.leftJoin(RolePermissionTable)
            .leftJoin(PermissionTable)
            .selectAll()
    }
}