package kz.kff.infrastructure.datasource.db.filter.permission

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList

class PermissionFilter(
    table: PermissionTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    //
    override val perPage: Int = 20,
    override val page: Int = 1,
    val permissionIds: List<Long>? = null,
    val value: String? = null,
    val values: List<String>? = null
) : BasePaginationFilter<PermissionTable>(table) {

    override fun getSearchColumns(): List<Column<*>> {
        return listOf(
            table.titleRu,
            table.titleKk,
            table.titleEn,
            table.value,
        )
    }

    override fun getJoinSearchColumns(): List<Column<*>> {
        return listOf(
            RoleTable.titleRu,
            RoleTable.titleEn,
            RoleTable.titleKk,
            RoleTable.value,
        )
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        value?.let {
            conditions.add(
                table.value.eq(value)
            )
        }
        permissionIds?.let {
            conditions.add(table.id.inList(it))
        }
        values?.let {
            conditions.add(table.value.inList(values))
        }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }

    companion object {
        fun byValue(value: String, table: PermissionTable = PermissionTable): PermissionFilter {
            return PermissionFilter(table, value = value)
        }
    }
}
