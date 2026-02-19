package kz.kff.infrastructure.datasource.db.user

import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.infrastructure.datasource.db.BaseDbDatasourceImpl
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll

class UserDatasourceImpl(
    table: UserTable
) : BaseDbDatasourceImpl<UserTable>(table), UserDatasource {

    override fun baseJoinQuery(): Query {
        return table
            .leftJoin(RoleTable)
            .leftJoin(FileTable)
            .selectAll()
    }
}
