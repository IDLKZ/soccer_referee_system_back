package kz.kff.presentation.http.role_permission

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okListWrapped
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.onlyIdPath
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.role_permission.RolePermissionCDTO
import kz.kff.domain.dto.role_permission.RolePermissionRDTO
import kz.kff.domain.dto.role_permission.RolePermissionWithDetailsRDTO
import kz.kff.domain.usecase.role_permission.command.CreateRolePermissionUseCase
import kz.kff.domain.usecase.role_permission.command.DeleteRolePermissionUseCase
import kz.kff.domain.usecase.role_permission.query.AllRolePermissionUseCase
import kz.kff.domain.usecase.role_permission.query.PaginateAllRolePermissionUseCase
import kz.kff.infrastructure.datasource.filter.role_permission.RolePermissionFilter
import kz.kff.infrastructure.datasource.filter.role_permission.RolePermissionParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RolePermissionController : KoinComponent {
    //Query
    private val paginateAllRolePermissionUseCase by inject<PaginateAllRolePermissionUseCase>()
    private val allRolePermissionUseCase by inject<AllRolePermissionUseCase>()
    //Command
    private val createRolePermissionUseCase by inject<CreateRolePermissionUseCase>()
    private val deleteRolePermissionUseCase by inject<DeleteRolePermissionUseCase>()


    fun register(route: Route, routeName: String = ApiRouteConstraints.API_ROLE_PERMISSIONS) {
        route.route(routeName) {
            //Пагинация
            get(path = ApiRouteConstraints.PAGINATE_API, {
                tags(ApiRouteConstraints.ROLE_PERMISSION_TAG)
                summary = "Пагинация связей роль-право"
                description = "Получить пагинированный список связей между ролями и правами"
                request {
                    withLocaleHeader()
                    fromFilter(RolePermissionFilter::class)
                }
                response {
                    okPaginationWrapped<RolePermissionWithDetailsRDTO>()
                    internalError()
                }
            }) {
                val filter = RolePermissionParameter(filterClass = RolePermissionFilter::class)
                    .fromParameter(call.parameters, table = RolePermissionTable)
                val result = paginateAllRolePermissionUseCase(filter = filter)
                call.success(data = result)
            }

            //Получить все
            get(path = ApiRouteConstraints.ALL_API, {
                tags(ApiRouteConstraints.ROLE_PERMISSION_TAG)
                summary = "Получить все связи роль-право"
                description = "Получить список всех связей между ролями и правами"
                request {
                    withLocaleHeader()
                    fromFilter(RolePermissionFilter::class)
                }
                response {
                    okListWrapped<RolePermissionWithDetailsRDTO>()
                    internalError()
                }
            }) {
                val filter = RolePermissionParameter(filterClass = RolePermissionFilter::class)
                    .fromParameter(call.parameters, table = RolePermissionTable)
                val result = allRolePermissionUseCase(filter = filter)
                call.success(data = result)
            }

            //Создать
            post(path = ApiRouteConstraints.CREATE_API, {
                tags(ApiRouteConstraints.ROLE_PERMISSION_TAG)
                summary = "Привязать право к роли"
                description = "Создать связь между ролью и правом"
                request {
                    withLocaleHeader()
                    body<RolePermissionCDTO>()
                }
                response {
                    okWrapped<RolePermissionRDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<RolePermissionCDTO>()
                val result = createRolePermissionUseCase(dto)
                call.success(data = result)
            }

            //Удалить по ID
            delete(path = ApiRouteConstraints.DELETE_BY_ID_API, {
                tags(ApiRouteConstraints.ROLE_PERMISSION_TAG)
                summary = "Удалить связь роль-право по ID"
                description = "Удалить связь между ролью и правом по ID записи"
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
                val result = deleteRolePermissionUseCase(id = id)
                call.success(data = result)
            }
        }
    }

    companion object {
        fun register(route: Route) {
            RolePermissionController().register(route)
        }
    }
}
