package kz.kff.domain.usecase.auth.command

import jakarta.validation.Validator
import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.dto.auth.SignInDTO
import kz.kff.domain.dto.auth.TokenDTO
import kz.kff.domain.dto.meta_data.MetaDataDTO
import kz.kff.domain.dto.user.UserSecretRDTO
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.mapper.toUserSecretRDTOGrouped
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.user.UserFilter

class SignInUseCase(
    private val userDatasource: UserDatasource,
    private val authSessionDatasource: AuthSessionDatasource,
    private val jwtService: AppJwtService,
    private val validator: Validator
): UseCaseTransaction() {

    suspend operator fun invoke(
        dto: SignInDTO,
        metaDataDTO: MetaDataDTO
    ): TokenDTO {

        return tx(
            before = { validateDTO(validator,dto) }
        ) {
            var user: UserSecretRDTO? = findUserByLogin(login = dto.login)
            if(user == null){
                throw ApiBadRequestException(
                    messageKey = LocalizedMessageConstraints.LoginUserNotFoundMessage
                )
            }
            val verified = jwtService.verifyPassword(dto.password,user.passwordHash?:"-")
            if(!verified){
                throw ApiBadRequestException(
                    messageKey = LocalizedMessageConstraints.PasswordNotVerifiedMessage
                )
            }
            var userData = user.toUserWithRelationsDTO()

            val authSessionCDTO = metaDataDTO.toAuthSessionCDTO(user.id, jwtService.refreshExpiresAt())

            val authSession = authSessionDatasource.create(
                insertBlock = authSessionCDTO.createEntity(AuthSessionTable),
                mapper = {it.toAuthSessionRDTO()}
            )?:throw ApiInternalException()

            val accessToken = jwtService.generateAccessToken(userData,authSession.id)
            val refreshToken = jwtService.generateRefreshToken(userData.id,authSession.id)
            TokenDTO(accessToken,refreshToken, jwtService.refreshAccessExpiresAt())
        }

    }

    private suspend fun findUserByLogin(login: String): UserSecretRDTO? {
        return findByFilter(UserFilter(UserTable, username = login, includeJoin = true))
            ?: findByFilter(UserFilter(UserTable, email = login, includeJoin = true))
            ?: findByFilter(UserFilter(UserTable, phone = login, includeJoin = true))
    }

    private suspend fun findByFilter(filter: UserFilter): UserSecretRDTO? {
        return userDatasource.findAllWithFilter(filter) { rows -> rows.toUserSecretRDTOGrouped() }.firstOrNull()
    }


}