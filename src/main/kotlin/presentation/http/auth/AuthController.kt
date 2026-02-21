package kz.kff.presentation.http.auth

import io.ktor.server.routing.Route
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.request.receive
import io.ktor.server.routing.route
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.core.shared.utils.meta_data.MetaDataHelper
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.auth.RefreshDTO
import kz.kff.domain.dto.auth.RegisterDTO
import kz.kff.domain.dto.auth.SignInDTO
import kz.kff.domain.dto.auth.TokenDTO
import kz.kff.domain.usecase.auth.command.RefreshTokenUseCase
import kz.kff.domain.usecase.auth.command.RegisterUseCase
import kz.kff.domain.usecase.auth.command.SignInUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthController : KoinComponent {
    private val signInUseCase by inject<SignInUseCase>()
    private val registerUseCase by inject<RegisterUseCase>()
    private val refreshTokenUseCase by inject<RefreshTokenUseCase>()

    fun register(route: Route, routeName: String = ApiRouteConstraints.API_AUTH) {
        route.route(routeName) {

            post(path = ApiRouteConstraints.SIGN_IN_API, builder = {
                tags(ApiRouteConstraints.AUTH_TAG)
                summary = "Вход"
                description = "Аутентификация по логину и паролю"
                request {
                    withLocaleHeader()
                    body<SignInDTO>()
                }
                response {
                    okWrapped<TokenDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<SignInDTO>()
                val metaData = MetaDataHelper.getMetaDataDTO(call.request)
                val result = signInUseCase(dto, metaData)
                call.success(result)
            }

            post(path = ApiRouteConstraints.SIGN_UP_API, builder = {
                tags(ApiRouteConstraints.AUTH_TAG)
                summary = "Регистрация"
                description = "Создать аккаунт и получить токены"
                request {
                    withLocaleHeader()
                    body<RegisterDTO>()
                }
                response {
                    okWrapped<TokenDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<RegisterDTO>()
                val metaData = MetaDataHelper.getMetaDataDTO(call.request)
                val result = registerUseCase(dto, metaData)
                call.success(result)
            }

            post(path = ApiRouteConstraints.REFRESH_API, builder = {
                tags(ApiRouteConstraints.AUTH_TAG)
                summary = "Обновить токен"
                description = "Получить новый access token по refresh token"
                request {
                    withLocaleHeader()
                    body<RefreshDTO>()
                }
                response {
                    okWrapped<TokenDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<RefreshDTO>()
                val result = refreshTokenUseCase(dto)
                call.success(result)
            }
        }
    }
}