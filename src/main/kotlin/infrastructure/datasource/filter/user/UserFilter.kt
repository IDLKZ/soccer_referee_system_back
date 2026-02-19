package kz.kff.infrastructure.datasource.filter.user

import kz.kff.core.db.table.user.UserTable
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.lowerCase

class UserFilter(
    table: UserTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    override val perPage: Int = 20,
    override val page: Int = 1,
    val isActive: Boolean? = null,
    val isVerified: Boolean? = null,
    val roleId: Long? = null,
    val gender: Int? = null,
    val email: String? = null,
    val username: String? = null,
    val phone: String? = null,
    val emails: List<String>? = null,
    val usernames: List<String>? = null,
    val phones: List<String>? = null,
) : BasePaginationFilter<UserTable>(table) {

    override fun getSearchColumns(): List<Column<*>> {
        return listOf(
            table.username,
            table.email,
            table.firstName,
            table.lastName,
            table.phone,
        )
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        isActive?.let { conditions.add(table.isActive eq it) }
        isVerified?.let { conditions.add(table.isVerified eq it) }
        gender?.let { conditions.add(table.gender eq it) }
        roleId?.let { conditions.add(table.roleId eq it) }
        email?.let { conditions.add(table.email.lowerCase() eq it.lowercase()) }
        username?.let { conditions.add(table.username.lowerCase() eq it.lowercase()) }
        phone?.let { conditions.add(table.phone.lowerCase() eq it.lowercase()) }
        emails?.takeIf { it.isNotEmpty() }?.let { conditions.add(table.email.lowerCase() inList it.map { e -> e.lowercase() }) }
        usernames?.takeIf { it.isNotEmpty() }?.let { conditions.add(table.username.lowerCase() inList it.map { u -> u.lowercase() }) }
        phones?.takeIf { it.isNotEmpty() }?.let { conditions.add(table.phone.lowerCase() inList it.map { p -> p.lowercase() }) }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }
}
