package kz.kff.presentation.http.permission

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okListWrapped
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.onlyIdPath
import kz.kff.domain.datasource.filter.toDeleteParams
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.permission.PermissionCDTO
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.dto.permission.PermissionUDTO
import kz.kff.domain.usecase.permission.command.BulkCreatePermissionUseCase
import kz.kff.domain.usecase.permission.command.BulkDeletePermissionUseCase
import kz.kff.domain.usecase.permission.command.BulkUpdatePermissionUseCase
import kz.kff.domain.usecase.permission.command.CreatePermissionUseCase
import kz.kff.domain.usecase.permission.command.DeletePermissionByIdUseCase
import kz.kff.domain.usecase.permission.command.UpdatePermissionByIdUseCase
import kz.kff.domain.usecase.permission.query.GetAllPermissionsUseCase
import kz.kff.domain.usecase.permission.query.GetPermissionByIdUseCase
import kz.kff.domain.usecase.permission.query.GetPermissionByValueUseCase
import kz.kff.domain.usecase.permission.query.PaginatePermissionUseCase
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter
import kz.kff.infrastructure.datasource.filter.permission.PermissionQueryParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PermissionController : KoinComponent {
    //Query
    private val paginatePermissionUseCase by inject<PaginatePermissionUseCase>()
    private val getAllPermissionsUseCase by inject<GetAllPermissionsUseCase>()
    private val getPermissionByIdUseCase by inject<GetPermissionByIdUseCase>()
    private val getPermissionByValueUseCase by inject<GetPermissionByValueUseCase>()
    //Command
    private val createPermissionUseCase by inject<CreatePermissionUseCase>()
    private val updatePermissionUseCase by inject<UpdatePermissionByIdUseCase>()
    private val deletePermissionUseCase by inject<DeletePermissionByIdUseCase>()
    //Bulk Command
    private val bulkCreatePermissionUseCase by inject<BulkCreatePermissionUseCase>()
    private val bulkUpdatePermissionUseCase by inject<BulkUpdatePermissionUseCase>()
    private val bulkDeletePermissionUseCase by inject<BulkDeletePermissionUseCase>()


    fun register(route: Route, routeName: String = ApiRouteConstraints.API_PERMISSIONS) {
        route.route(routeName) {
            //Пагинация
            get(path = ApiRouteConstraints.PAGINATE_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Пагинация прав"
                description = "Получить пагинированный список прав"
                request {
                    withLocaleHeader()
                    fromFilter(PermissionFilter::class)
                }
                response {
                    okPaginationWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val filter = PermissionQueryParameter(filterClass = PermissionFilter::class)
                    .fromParameter(call.parameters, table = PermissionTable)
                val pagination = paginatePermissionUseCase(filter = filter)
                call.success(data = pagination)
            }

            //Получить все
            get(path = ApiRouteConstraints.ALL_API, {
                tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Получить все права"
                description = "Получить список всех прав с фильтрацией"
                request {
                    withLocaleHeader()
                    fromFilter(PermissionFilter::class)
                }
                response {
                    okListWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val filter = PermissionQueryParameter(filterClass = PermissionFilter::class)
                    .fromParameter(call.parameters, table = PermissionTable)
                val all = getAllPermissionsUseCase(filter = filter)
                call.success(data = all)
            }

            //Получить по ID
            get(path = ApiRouteConstraints.GET_BY_ID_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Получить право по ID"
                description = "Получить право по его ID"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                }
                response {
                    okWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val result = getPermissionByIdUseCase(id = id)
                call.success(data = result)
            }

            //Получить по значению
            get(path = "/get/value/{value}", {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Получить право по значению"
                description = "Получить право по его строковому значению"
                request {
                    withLocaleHeader()
                    pathParameter<String>("value") { description = "Значение права" }
                }
                response {
                    okWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val value = call.parameters["value"]
                    ?: throw ApiInternalException(detail = "Value parameter is required")
                val result = getPermissionByValueUseCase(value = value)
                call.success(data = result)
            }

            //Создать
            post(path = ApiRouteConstraints.CREATE_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Создать право"
                description = "Создать новое право"
                request {
                    withLocaleHeader()
                    body<PermissionCDTO>()
                }
                response {
                    okWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val dto = call.receive<PermissionCDTO>()
                val result = createPermissionUseCase(dto)
                call.success(data = result)
            }

            //Обновить по ID
            put(path = ApiRouteConstraints.UPDATE_BY_ID_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Обновить право по ID"
                description = "Обновить существующее право по ID"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                    body<PermissionCDTO>()
                }
                response {
                    okWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val dto = call.receive<PermissionCDTO>()
                val result = updatePermissionUseCase(id = id, dto = dto)
                call.success(data = result)
            }

            //Удалить по ID
            delete(path = ApiRouteConstraints.DELETE_BY_ID_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Удалить право по ID"
                description = "Удалить право по его ID"
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
                val result = deletePermissionUseCase(id = id)
                call.success(data = result)
            }

            //Массовое создание
            post(path = ApiRouteConstraints.CREATE_BULK_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Массовое создание прав"
                description = "Создать несколько прав за один запрос"
                request {
                    withLocaleHeader()
                    body<List<PermissionCDTO>>()
                }
                response {
                    okListWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val dtos = call.receive<List<PermissionCDTO>>()
                val result = bulkCreatePermissionUseCase(dtos = dtos)
                call.success(data = result)
            }

            //Массовое обновление
            put(path = ApiRouteConstraints.UPDATE_BULK_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Массовое обновление прав"
                description = "Обновить несколько прав за один запрос"
                request {
                    withLocaleHeader()
                    body<List<PermissionUDTO>>()
                }
                response {
                    okListWrapped<PermissionRDTO>()
                    internalError()
                }
            }) {
                val dtos = call.receive<List<PermissionUDTO>>()
                val result = bulkUpdatePermissionUseCase(dtos = dtos)
                call.success(data = result)
            }

            //Массовое удаление
            delete(path = ApiRouteConstraints.DELETE_BULK_API, {
                 tags(ApiRouteConstraints.PERMISSION_TAG)
                summary = "Массовое удаление прав"
                description = "Удалить несколько прав за один запрос"
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
                val result = bulkDeletePermissionUseCase(ids = ids)
                call.success(data = result)
            }
        }
    }
}
