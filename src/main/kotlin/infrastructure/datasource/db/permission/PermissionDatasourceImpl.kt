package kz.kff.infrastructure.datasource.db.permission

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.infrastructure.datasource.db.BaseDbDatasourceImpl
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll

class PermissionDatasourceImpl(
    table: PermissionTable
) : BaseDbDatasourceImpl<PermissionTable>(table), PermissionDatasource {

    override fun baseJoinQuery(): Query {
        return table.leftJoin(RolePermissionTable)
            .leftJoin(RoleTable)
            .selectAll()
    }
}
