package kz.kff.presentation.http.auth_session

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.onlyIdPath
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.auth_session.AuthSessionCDTO
import kz.kff.domain.dto.auth_session.AuthSessionRDTO
import kz.kff.domain.usecase.auth_session.command.CreateAuthSessionUseCase
import kz.kff.domain.usecase.auth_session.command.DeleteAuthSessionByIdUseCase
import kz.kff.domain.usecase.auth_session.command.UpdateAuthSessionUseCase
import kz.kff.domain.usecase.auth_session.query.GetAuthSessionUseCase
import kz.kff.domain.usecase.auth_session.query.PaginateAuthSessionUseCase
import kz.kff.infrastructure.datasource.filter.auth_session.AuthSessionFilter
import kz.kff.infrastructure.datasource.filter.auth_session.AuthSessionParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthSessionController : KoinComponent {

    private val paginateAuthSessionUseCase by inject<PaginateAuthSessionUseCase>()
    private val getAuthSessionUseCase by inject<GetAuthSessionUseCase>()
    private val createAuthSessionUseCase by inject<CreateAuthSessionUseCase>()
    private val updateAuthSessionUseCase by inject<UpdateAuthSessionUseCase>()
    private val deleteAuthSessionByIdUseCase by inject<DeleteAuthSessionByIdUseCase>()

    fun register(route: Route, routeName: String = ApiRouteConstraints.API_AUTH_SESSIONS) {
        route.route(routeName) {
            // Paginate
            get(path = ApiRouteConstraints.PAGINATE_API, {
                tags(ApiRouteConstraints.AUTH_SESSION_TAG)
                summary = "Пагинация сессий"
                description = "Получить пагинированный список сессий аутентификации"
                request {
                    withLocaleHeader()
                    fromFilter(AuthSessionFilter::class)
                }
                response {
                    okPaginationWrapped<AuthSessionRDTO>()
                    internalError()
                }
            }) {
                val filter = AuthSessionParameter(filterClass = AuthSessionFilter::class)
                    .fromParameter(call.parameters, table = AuthSessionTable)
                val result = paginateAuthSessionUseCase(filter)
                call.success(result)
            }

            // Get By Id
            get(path = ApiRouteConstraints.GET_BY_ID_API, {
                tags(ApiRouteConstraints.AUTH_SESSION_TAG)
                summary = "Получить сессию по ID"
                description = "Получить сессию аутентификации по ID"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                }
                response {
                    okWrapped<AuthSessionRDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val result = getAuthSessionUseCase(id = id)
                call.success(result)
            }

            // Create
            post(path = ApiRouteConstraints.CREATE_API, {
                tags(ApiRouteConstraints.AUTH_SESSION_TAG)
                summary = "Создать сессию"
                description = "Создать новую сессию аутентификации"
                request {
                    withLocaleHeader()
                    body<AuthSessionCDTO>()
                }
                response {
                    okWrapped<AuthSessionRDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<AuthSessionCDTO>()
                val result = createAuthSessionUseCase(dto)
                call.success(result)
            }

            // Update
            put(path = ApiRouteConstraints.UPDATE_BY_ID_API, {
                tags(ApiRouteConstraints.AUTH_SESSION_TAG)
                summary = "Обновить сессию по ID"
                description = "Обновить данные сессии аутентификации"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                    body<AuthSessionCDTO>()
                }
                response {
                    okWrapped<AuthSessionRDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val dto = call.receive<AuthSessionCDTO>()
                val result = updateAuthSessionUseCase(id = id, dto = dto)
                call.success(result)
            }

            // Delete by ID
            delete(path = ApiRouteConstraints.DELETE_BY_ID_API, {
                tags(ApiRouteConstraints.AUTH_SESSION_TAG)
                summary = "Удалить сессию по ID"
                description = "Удалить сессию аутентификации по ID"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                }
                response {
                    okWrapped<Boolean>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val result = deleteAuthSessionByIdUseCase(id = id)
                call.success(result)
            }
        }
    }
}
