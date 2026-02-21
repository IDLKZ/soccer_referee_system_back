package kz.kff.domain.usecase.auth.command

import jakarta.validation.Validator
import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.exception_handlers.api.ApiValidationException
import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.DbValueConstraints
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.dto.auth.RegisterDTO
import kz.kff.domain.dto.auth.TokenDTO
import kz.kff.domain.dto.meta_data.MetaDataDTO
import kz.kff.domain.dto.user.UserCDTO
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.domain.mapper.toUserWithRelationsDTOGrouped
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter
import kz.kff.infrastructure.datasource.filter.user.UserFilter

class RegisterUseCase(
    private val userDatasource: UserDatasource,
    private val roleDatasource: RoleDatasource,
    private val authSessionDatasource: AuthSessionDatasource,
    private val jwtService: AppJwtService,
    private val validator: Validator,
) : UseCaseTransaction() {

    suspend operator fun invoke(
        dto: RegisterDTO,
        metaDataDTO: MetaDataDTO,
    ): TokenDTO = tx(
        before = { validateDTO(validator, dto) }
    ) {
        validateUniqueness(dto)

        val defaultRole = roleDatasource.findOneByFilter(
            filter = RoleFilter.byValue(DbValueConstraints.MANAGER_ROLE_VALUE),
            mapper = { it.toRoleRDTO() }
        ) ?: throw ApiBadRequestException(LocalizedMessageConstraints.RoleNotFoundMessage)

        val userCDTO = UserCDTO(
            roleId = defaultRole.id,
            username = dto.username,
            email = dto.email,
            passwordHash = jwtService.hashPassword(dto.password),
            firstName = dto.firstName,
            lastName = dto.lastName,
            patronymic = dto.patronymic,
            phone = dto.phone,
            birthDate = dto.birthDate,
            gender = dto.gender,
        )

        val createdUser = userDatasource.create(
            insertBlock = userCDTO.createEntity(UserTable),
            mapper = { it.toUserRDTO() }
        ) ?: throw ApiInternalException(LocalizedMessageConstraints.ErrorInternalMessage)

        val user = userDatasource.findByLongId(
            id = createdUser.id,
            includeJoin = true,
        ) { rows -> rows.toUserWithRelationsDTOGrouped() }
            ?: throw ApiInternalException(LocalizedMessageConstraints.ErrorInternalMessage)

        val authSessionCDTO = metaDataDTO.toAuthSessionCDTO(user.id, jwtService.refreshExpiresAt())

        val authSession = authSessionDatasource.create(
            insertBlock = authSessionCDTO.createEntity(AuthSessionTable),
            mapper = { it.toAuthSessionRDTO() }
        ) ?: throw ApiInternalException(LocalizedMessageConstraints.ErrorInternalMessage)

        val accessToken = jwtService.generateAccessToken(user, authSession.id)
        val refreshToken = jwtService.generateRefreshToken(user.id, authSession.id)
        TokenDTO(accessToken, refreshToken, jwtService.refreshAccessExpiresAt())
    }

    private suspend fun validateUniqueness(dto: RegisterDTO) {
        val existEmailCount = userDatasource.countWithFilter(
            filter = UserFilter(table = UserTable, email = dto.email)
        )
        if (existEmailCount > 0) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniqueEmailExistMessage,
                errors = mapOf("email" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniqueEmailExistMessage))
            )
        }

        val existUsernameCount = userDatasource.countWithFilter(
            filter = UserFilter(table = UserTable, username = dto.username)
        )
        if (existUsernameCount > 0) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniqueUsernameExistMessage,
                errors = mapOf("username" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniqueUsernameExistMessage))
            )
        }

        dto.phone?.let {
            val existPhoneCount = userDatasource.countWithFilter(
                filter = UserFilter(table = UserTable, phone = it)
            )
            if (existPhoneCount > 0) {
                throw ApiValidationException(
                    LocalizedMessageConstraints.ValidationUniquePhoneExistMessage,
                    errors = mapOf("phone" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniquePhoneExistMessage))
                )
            }
        }
    }
}
