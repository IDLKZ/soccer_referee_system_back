package kz.kff.presentation.http.role

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.patch
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okListWrapped
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.onlyHardDeleteParams
import kz.kff.domain.datasource.filter.onlyIdPath
import kz.kff.domain.datasource.filter.toDeleteParams
import kz.kff.domain.datasource.filter.withIdAndFlags
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.role.RoleCDTO
import kz.kff.domain.dto.role.RoleRDTO
import kz.kff.domain.dto.role.RoleUDTO
import kz.kff.domain.dto.role.RoleWithPermissionsDTO
import kz.kff.domain.usecase.role.command.BulkCreateRoleUseCase
import kz.kff.domain.usecase.role.command.BulkDeleteRoleUseCase
import kz.kff.domain.usecase.role.command.BulkRestoreRoleUseCase
import kz.kff.domain.usecase.role.command.BulkUpdateRoleUseCase
import kz.kff.domain.usecase.role.command.CreateRoleUseCase
import kz.kff.domain.usecase.role.command.DeleteRoleByIdCase
import kz.kff.domain.usecase.role.command.RestoreRoleByIdCase
import kz.kff.domain.usecase.role.command.UpdateRoleByIdCase
import kz.kff.domain.usecase.role.query.GetAllRolesUseCase
import kz.kff.domain.usecase.role.query.GetRoleByIdUseCase
import kz.kff.domain.usecase.role.query.PaginateRoleUseCase
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter
import kz.kff.infrastructure.datasource.filter.role.RoleQueryParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RoleController : KoinComponent {
    //Query
    private val paginateRoleUseCase by inject<PaginateRoleUseCase>()
    private val getAllRoleUseCase by inject<GetAllRolesUseCase>()
    private val getRoleByIdUseCase by inject<GetRoleByIdUseCase>()
    //Command
    private val createRoleUseCase by inject<CreateRoleUseCase>()
    private val updateRoleUseCase by inject<UpdateRoleByIdCase>()
    private val deleteRoleUseCase by inject<DeleteRoleByIdCase>()
    private val restoreRoleUseCase by inject<RestoreRoleByIdCase>()
    //Bulk Command
    private val bulkCreateRoleUseCase by inject<BulkCreateRoleUseCase>()
    private val bulkUpdateRoleUseCase by inject<BulkUpdateRoleUseCase>()
    private val bulkRestoreRoleUseCase by inject<BulkRestoreRoleUseCase>()
    private val bulkDeleteRoleUseCase by inject<BulkDeleteRoleUseCase>()


    fun register(route: Route, routeName: String = ApiRouteConstraints.API_ROLES) {
        route.route(routeName) {
            //Пагинация
            get(path = ApiRouteConstraints.PAGINATE_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Пагинация ролей"
                description = "Получить пагинированный список ролей с правами"
                request {
                    withLocaleHeader()
                    fromFilter(RoleFilter::class)
                }
                response {
                    okPaginationWrapped<RoleWithPermissionsDTO>()
                    internalError()
                }
            }) {
                val filter = RoleQueryParameter(filterClass = RoleFilter::class)
                    .fromParameter(call.parameters, table = RoleTable)
                val pagination = paginateRoleUseCase(filter = filter)
                call.success(data = pagination)
            }

            //Получить все
            get(path = ApiRouteConstraints.ALL_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Получить все роли"
                description = "Получить список всех ролей с фильтрацией"
                request {
                    withLocaleHeader()
                    fromFilter(RoleFilter::class)
                }
                response {
                    okListWrapped<RoleWithPermissionsDTO>()
                    internalError()
                }
            }) {
                val filter = RoleQueryParameter(filterClass = RoleFilter::class)
                    .fromParameter(call.parameters, table = RoleTable)
                val all = getAllRoleUseCase(filter = filter)
                call.success(data = all)
            }

            //Получить по ID
            get(path = ApiRouteConstraints.GET_BY_ID_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Получить роль по ID"
                description = "Получить роль по ID с возможностью включения прав"
                request {
                    withLocaleHeader()
                    withIdAndFlags(true)
                }
                response {
                    okWrapped<RoleWithPermissionsDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val showDeleted = ApiRouteConstraints.getShowDeletedParameter(call.parameters)
                val includeJoin = ApiRouteConstraints.getIncludeJoinParameter(call.parameters)
                val result = getRoleByIdUseCase(id = id, showDeleted = showDeleted, includeJoin = includeJoin)
                call.success(data = result)
            }

            //Создать
            post(path = ApiRouteConstraints.CREATE_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Создать роль"
                description = "Создать новую роль"
                request {
                    withLocaleHeader()
                    body<RoleCDTO>()
                }
                response {
                    okWrapped<RoleWithPermissionsDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<RoleCDTO>()
                val result = createRoleUseCase(dto)
                call.success(data = result)
            }

            //Обновить по ID
            put(path = ApiRouteConstraints.UPDATE_BY_ID_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Обновить роль по ID"
                description = "Обновить существующую роль по ID"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                    body<RoleCDTO>()
                }
                response {
                    okWrapped<RoleWithPermissionsDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val dto = call.receive<RoleCDTO>()
                val result = updateRoleUseCase(id = id, dto = dto)
                call.success(data = result)
            }

            //Удалить по ID
            delete(path = ApiRouteConstraints.DELETE_BY_ID_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Удалить роль по ID"
                description = "Удалить роль по ID (мягкое или жёсткое удаление)"
                request {
                    withLocaleHeader()
                    toDeleteParams()
                }
                response {
                    okWrapped<Boolean>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val hardDelete = ApiRouteConstraints.getHardDeleteParameter(call.parameters)
                val result = deleteRoleUseCase(id = id, hardDelete = hardDelete)
                call.success(data = result)
            }

            //Восстановить по ID
            patch(path = ApiRouteConstraints.RESTORE_BY_ID_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Восстановить роль по ID"
                description = "Восстановить мягко удалённую роль по ID"
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
                val result = restoreRoleUseCase(id = id)
                call.success(data = result)
            }

            //Массовое создание
            post(path = ApiRouteConstraints.CREATE_BULK_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Массовое создание ролей"
                description = "Создать несколько ролей за один запрос"
                request {
                    withLocaleHeader()
                    body<List<RoleCDTO>>()
                }
                response {
                    okListWrapped<RoleRDTO>()
                    internalError()
                }
            }) {
                val dtos = call.receive<List<RoleCDTO>>()
                val result = bulkCreateRoleUseCase(dtos = dtos)
                call.success(data = result)
            }

            //Массовое обновление
            put(path = ApiRouteConstraints.UPDATE_BULK_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Массовое обновление ролей"
                description = "Обновить несколько ролей за один запрос"
                request {
                    withLocaleHeader()
                    body<List<RoleUDTO>>()
                }
                response {
                    okListWrapped<RoleRDTO>()
                    internalError()
                }
            }) {
                val dtos = call.receive<List<RoleUDTO>>()
                val result = bulkUpdateRoleUseCase(dtos = dtos)
                call.success(data = result)
            }

            //Массовое удаление
            delete(path = ApiRouteConstraints.DELETE_BULK_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Массовое удаление ролей"
                description = "Удалить несколько ролей за один запрос"
                request {
                    withLocaleHeader()
                    body<List<Long>>()
                    onlyHardDeleteParams()
                }
                response {
                    okWrapped<Int>()
                    internalError()
                }
            }) {
                val ids = call.receive<List<Long>>()
                val hardDelete = ApiRouteConstraints.getHardDeleteParameter(call.parameters)
                val result = bulkDeleteRoleUseCase(ids = ids, hardDelete = hardDelete)
                call.success(data = result)
            }

            //Массовое восстановление
            put(path = ApiRouteConstraints.RESTORE_BULK_API, {
                tags(ApiRouteConstraints.ROLE_TAG)
                summary = "Массовое восстановление ролей"
                description = "Восстановить несколько мягко удалённых ролей"
                request {
                    withLocaleHeader()
                    body<List<Long>>()
                }
                response {
                    okWrapped<Int>()
                    internalError()
                }
            }) {
                val ids = call.receive<List<Long>>()
                val result = bulkRestoreRoleUseCase(ids = ids)
                call.success(data = result)
            }
        }
    }
}
