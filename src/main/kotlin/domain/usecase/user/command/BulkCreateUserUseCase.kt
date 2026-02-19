package kz.kff.domain.usecase.user.command

import jakarta.validation.Validator
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.user.UserCDTO
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.mapper.toRoleRDTOList
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.user.UserFilter

class BulkCreateUserUseCase(
    private val userDatasource: UserDatasource,
    private val roleDatasource: RoleDatasource,
    private val validator: Validator,
) : UseCaseTransaction() {

    suspend operator fun invoke(dtos: List<UserCDTO>): List<UserRDTO> {
        if (dtos.isEmpty()) return emptyList()
        return tx(
            before = { dtos.forEach { validateDTO(validator, it) } }
        ) {
            // Validate all distinct roleIds exist
            dtos.map { it.roleId }.distinct().forEach { roleId ->
                roleDatasource.findByLongId(roleId) { rows -> rows.toRoleRDTOList() }
                    ?: throw ApiNotFoundException(LocalizedMessageConstraints.RoleNotFoundMessage)
            }

            val emails = dtos.map { it.email.lowercase() }
            val usernames = dtos.map { it.username.lowercase() }
            val phones = dtos.mapNotNull { it.phone }.map { it.lowercase() }

            // Fetch all conflicting users in 3 batch queries
            val existingEmails = userDatasource.findAllWithFilter(
                filter = UserFilter(table = UserTable, emails = emails),
                mapper = eachRow { it.toUserRDTO() }
            ).map { it.email.lowercase() }.toSet()

            val existingUsernames = userDatasource.findAllWithFilter(
                filter = UserFilter(table = UserTable, usernames = usernames),
                mapper = eachRow { it.toUserRDTO() }
            ).map { it.username.lowercase() }.toSet()

            val existingPhones = if (phones.isNotEmpty()) {
                userDatasource.findAllWithFilter(
                    filter = UserFilter(table = UserTable, phones = phones),
                    mapper = eachRow { it.toUserRDTO() }
                ).mapNotNull { it.phone?.lowercase() }.toSet()
            } else emptySet()

            // Skip DTOs that conflict on any unique field
            val dtosToCreate = dtos.filterNot { dto ->
                dto.email.lowercase() in existingEmails ||
                dto.username.lowercase() in existingUsernames ||
                (dto.phone != null && dto.phone.lowercase() in existingPhones)
            }

            userDatasource.bulkCreate(
                items = dtosToCreate,
                insertBlock = { dto -> applyMap(dto.bindMap(UserTable)) },
                mapper = { it.toUserRDTO() }
            )
        }
    }
}
