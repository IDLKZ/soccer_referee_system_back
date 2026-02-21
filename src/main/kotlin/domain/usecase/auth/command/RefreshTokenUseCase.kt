package kz.kff.domain.usecase.auth.command

import jakarta.validation.Validator
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.exception_handlers.api.ApiUnauthorizedException
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.dto.auth.RefreshDTO
import kz.kff.domain.dto.auth.TokenDTO
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.mapper.toUserWithRelationsDTOGrouped
import kz.kff.domain.usecase.shared.UseCaseTransaction

class RefreshTokenUseCase(
    private val userDatasource: UserDatasource,
    private val authSessionDatasource: AuthSessionDatasource,
    private val jwtService: AppJwtService,
    private val validator: Validator,
) : UseCaseTransaction() {

    suspend operator fun invoke(dto: RefreshDTO): TokenDTO = tx(
        before = { validateDTO(validator, dto) }
    ) {
        val parsed = jwtService.parseRefreshToken(dto.refreshToken)

        val session = authSessionDatasource.findByLongId(
            id = parsed.sessionId,
        ) { rows -> rows.map { it.toAuthSessionRDTO() } }
            ?: throw ApiUnauthorizedException()

        if (session.revoked) throw ApiUnauthorizedException()

        val user = userDatasource.findByLongId(
            id = parsed.userId,
            includeJoin = true,
        ) { rows -> rows.toUserWithRelationsDTOGrouped() }
            ?: throw ApiUnauthorizedException()

        val accessToken = jwtService.generateAccessToken(user, session.id)
        TokenDTO(
            access_token = accessToken,
            refresh_token = dto.refreshToken,
            expires_in_ms = jwtService.refreshAccessExpiresAt(),
        )
    }
}
