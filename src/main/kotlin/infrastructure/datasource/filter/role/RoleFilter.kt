package kz.kff.infrastructure.datasource.db.filter.role

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList

open class RoleFilter (
    table: RoleTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    //
    override val perPage: Int = 20,
    override val page: Int = 1,
    val isSystem:Boolean? = null,
    val isAdministrative:Boolean? = null,
    val roleIds:List<Long>? = null,
    val value: String? = null,
    val values:List<String>? = null
) : BasePaginationFilter<RoleTable>(table) {


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
                PermissionTable.titleRu,
                PermissionTable.titleEn,
                PermissionTable.titleKk,
                PermissionTable.value,
            )
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        value?.let {
            conditions.add(
                table.value.eq(value)
            )
        }
        roleIds?.let {
            conditions.add(table.id.inList(it))
        }
        isSystem?.let { conditions.add(table.isSystem eq it) }
        isAdministrative?.let { conditions.add(table.isAdministrative eq it) }
        values?.let {
            conditions.add(table.value.inList(values))
        }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }

    companion object {
        fun byValue(value: String, table: RoleTable = RoleTable): RoleFilter {
            return RoleFilter(table, value=value)
        }
    }
}