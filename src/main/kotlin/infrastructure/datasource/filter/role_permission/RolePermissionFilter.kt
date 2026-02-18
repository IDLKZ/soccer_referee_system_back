package kz.kff.infrastructure.datasource.filter.role_permission

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.inList

class RolePermissionFilter(
    table: RolePermissionTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = true,
    //
    override val perPage: Int = 20,
    override val page: Int = 1,
    val roleIds:List<Long>? = null,
    val permissionIds: List<Long>? = null,
): BasePaginationFilter<RolePermissionTable>(table) {
    override fun getSearchColumns(): List<Column<*>> {
        return emptyList()
    }

    override fun getJoinSearchColumns(): List<Column<*>> {
        return listOf(
            PermissionTable.titleRu,
            PermissionTable.titleKk,
            PermissionTable.titleEn,
            PermissionTable.value,
            RoleTable.titleRu,
            RoleTable.titleKk,
            RoleTable.titleEn,
            RoleTable.value,
        )
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        roleIds?.let {
            conditions.add(table.roleId.inList(it))
        }
        permissionIds?.let {
            conditions.add(table.permissionId.inList(it))
        }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }

}