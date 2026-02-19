package kz.kff.presentation.http.user

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.fromFormField
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okListWrapped
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.onlyHardDeleteParams
import kz.kff.domain.datasource.filter.onlyIdPath
import kz.kff.domain.datasource.filter.toDeleteParams
import kz.kff.domain.datasource.filter.uploadOneFilePart
import kz.kff.domain.datasource.filter.withIdAndFlags
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.user.UserCDTO
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.dto.user.UserWithRelationsDTO
import kz.kff.domain.usecase.user.command.BulkCreateUserUseCase
import kz.kff.domain.usecase.user.command.BulkDeleteUserByIdsUseCase
import kz.kff.domain.usecase.user.command.CreateUserUseCase
import kz.kff.domain.usecase.user.command.DeleteUserByIdUseCase
import kz.kff.domain.usecase.user.command.UpdateUserUseCase
import kz.kff.domain.usecase.user.query.GetUserByIdUseCase
import kz.kff.domain.usecase.user.query.PaginateUserUseCase
import kz.kff.infrastructure.datasource.filter.user.UserFilter
import kz.kff.infrastructure.datasource.filter.user.UserParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserController : KoinComponent {

    private val paginateUserUseCase by inject<PaginateUserUseCase>()
    private val getUserByIdUseCase by inject<GetUserByIdUseCase>()
    private val createUserUseCase by inject<CreateUserUseCase>()
    private val updateUserUseCase by inject<UpdateUserUseCase>()
    private val deleteUserByIdUseCase by inject<DeleteUserByIdUseCase>()
    private val bulkDeleteUserByIdsUseCase by inject<BulkDeleteUserByIdsUseCase>()
    private val bulkCreateUserUseCase by inject<BulkCreateUserUseCase>()

    fun register(route: Route, routeName: String = ApiRouteConstraints.API_USERS) {
        route.route(routeName) {
            //Paginate
            get(path = ApiRouteConstraints.PAGINATE_API, {
                tags(ApiRouteConstraints.USER_TAG)
                summary = "Пагинация пользователей"
                description = "Получить пагинированный список пользователей"
                request {
                    withLocaleHeader()
                    fromFilter(UserFilter::class)
                }
                response {
                    okPaginationWrapped<UserWithRelationsDTO>()
                    internalError()
                }
            }) {
                val filter = UserParameter(filterClass = UserFilter::class)
                    .fromParameter(call.parameters, table = UserTable)
                val result = paginateUserUseCase(filter)
                call.success(result)
            }
            //Get By Id
            get(path = ApiRouteConstraints.GET_BY_ID_API, {
                tags(ApiRouteConstraints.USER_TAG)
                summary = "Получить пользователя по ID"
                description = "Получить пользователя по ID с возможностью включения роли и аватара"
                request {
                    withLocaleHeader()
                    withIdAndFlags(useSoftDelete = true)
                }
                response {
                    okWrapped<UserWithRelationsDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val showDeleted = ApiRouteConstraints.getShowDeletedParameter(call.parameters)
                val includeJoin = ApiRouteConstraints.getIncludeJoinParameter(call.parameters)
                val result = getUserByIdUseCase(id = id, showDeleted = showDeleted, includeJoin = includeJoin)
                call.success(result)
            }
            //Create
            post(
                path = ApiRouteConstraints.CREATE_API,
                builder = {
                    tags(ApiRouteConstraints.USER_TAG)
                    summary = "Создать пользователя"
                    description = "Новый пользователь"
                    request {
                        withLocaleHeader()
                        fromFormField(
                            dtoClass = UserCDTO::class,
                            block = { uploadOneFilePart() }
                        )
                    }
                    response {
                        okWrapped<UserRDTO>()
                        internalError()
                    }
                }
            ) {
                val multipart = call.receiveMultipart()
                val result = createUserUseCase(multipart)
                call.success(result)
            }
            //Update
            put(path = ApiRouteConstraints.UPDATE_BY_ID_API, {
                tags(ApiRouteConstraints.USER_TAG)
                summary = "Обновить пользователя по ID"
                description = "Обновить существующего пользователя по ID"
                request {
                    withLocaleHeader()
                    onlyIdPath()
                    body<UserCDTO>()
                }
                response {
                    okWrapped<UserRDTO>()
                    internalError()
                }
            }) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val dto = call.receive<UserCDTO>()
                val result = updateUserUseCase(id = id, dto = dto)
                call.success(result)
            }
            //Delete by ID
            delete(path = ApiRouteConstraints.DELETE_BY_ID_API, {
                tags(ApiRouteConstraints.USER_TAG)
                summary = "Удалить пользователя по ID"
                description = "Удалить пользователя по ID (мягкое или жёсткое удаление)"
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
                val result = deleteUserByIdUseCase(id = id, hardDelete = hardDelete)
                call.success(result)
            }
            //Bulk Create
            post(path = ApiRouteConstraints.CREATE_BULK_API, {
                tags(ApiRouteConstraints.USER_TAG)
                summary = "Массовое создание пользователей"
                description = "Создать несколько пользователей за один запрос (дубликаты по email/username/phone пропускаются)"
                request {
                    withLocaleHeader()
                    body<List<UserCDTO>>()
                }
                response {
                    okListWrapped<UserRDTO>()
                    internalError()
                }
            }) {
                val dtos = call.receive<List<UserCDTO>>()
                val result = bulkCreateUserUseCase(dtos)
                call.success(result)
            }
            //Bulk Delete
            delete(path = ApiRouteConstraints.DELETE_BULK_API, {
                tags(ApiRouteConstraints.USER_TAG)
                summary = "Массовое удаление пользователей"
                description = "Удалить несколько пользователей за один запрос"
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
                val result = bulkDeleteUserByIdsUseCase(ids = ids, hardDelete = hardDelete)
                call.success(result)
            }
        }
    }
}
