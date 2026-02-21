package kz.kff.domain.usecase.user.command

import jakarta.validation.Validator
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.exception_handlers.api.ApiValidationException
import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.dto.user.UserCDTO
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.mapper.toRoleRDTOList
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.user.UserFilter

class UpdateUserUseCase(
    private val userDatasource: UserDatasource,
    private val roleDatasource: RoleDatasource,
    private val validator: Validator,
    private val jwtService: AppJwtService,
) : UseCaseTransaction() {

    suspend operator fun invoke(id: Long, dto: UserCDTO): UserRDTO = tx(
        before = { validateDTO(validator, dto) }
    ) {
        userDatasource.findByLongId(id = id, mapper = eachRow { it.toUserRDTO() })
            ?: throw ApiNotFoundException(LocalizedMessageConstraints.UserNotFoundMessage)

        roleDatasource.findByLongId(dto.roleId) { rows -> rows.toRoleRDTOList() }
            ?: throw ApiNotFoundException(LocalizedMessageConstraints.RoleNotFoundMessage)

        val byEmail = userDatasource.findOneByFilter(
            filter = UserFilter(table = UserTable, email = dto.email),
            mapper = { it.toUserRDTO() }
        )
        if (byEmail != null && byEmail.id != id) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniqueEmailExistMessage,
                errors = mapOf("email" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniqueEmailExistMessage))
            )
        }

        val byUsername = userDatasource.findOneByFilter(
            filter = UserFilter(table = UserTable, username = dto.username),
            mapper = { it.toUserRDTO() }
        )
        if (byUsername != null && byUsername.id != id) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniqueUsernameExistMessage,
                errors = mapOf("username" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniqueUsernameExistMessage))
            )
        }

        if (dto.phone != null) {
            val byPhone = userDatasource.findOneByFilter(
                filter = UserFilter(table = UserTable, phone = dto.phone),
                mapper = { it.toUserRDTO() }
            )
            if (byPhone != null && byPhone.id != id) {
                throw ApiValidationException(
                    LocalizedMessageConstraints.ValidationUniquePhoneExistMessage,
                    errors = mapOf("phone" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniquePhoneExistMessage))
                )
            }
        }
        dto.passwordHash = jwtService.hashPassword(dto.passwordHash)
        userDatasource.update(
            id = id,
            updateBlock = { dto.applyUpdate(UserTable, this) },
            mapper = { it.toUserRDTO() }
        ) ?: throw ApiInternalException(messageKey = LocalizedMessageConstraints.ErrorInternalMessage)
    }
}
