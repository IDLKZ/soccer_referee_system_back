package kz.kff.infrastructure.datasource.filter.auth_session

import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq

class AuthSessionFilter(
    table: AuthSessionTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    override val perPage: Int = 20,
    override val page: Int = 1,
    val userId: Long? = null,
    val revoked: Boolean? = null,
) : BasePaginationFilter<AuthSessionTable>(table) {

    override fun getSearchColumns(): List<Column<*>> = listOf(
        table.deviceName,
        table.ipAddress,
        table.browser,
        table.country,
        table.city,
    )

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        userId?.let { conditions.add(table.userId eq it) }
        revoked?.let { conditions.add(table.revoked eq it) }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }
}
