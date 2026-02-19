package kz.kff.presentation.http.file

import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.http.ContentType
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.ApiParams
import kz.kff.domain.datasource.filter.deleteFileParams
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.listFileUpload
import kz.kff.domain.datasource.filter.okListWrapped
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.oneFileUpload
import kz.kff.domain.datasource.filter.updateFileParams
import kz.kff.domain.datasource.filter.uploadFile
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.datasource.filter.onlyIdPath
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.usecase.file.command.BulkCreateFileUseCase
import kz.kff.domain.usecase.file.command.CreateFileUseCase
import kz.kff.domain.usecase.file.command.DeleteBulkFileUseCase
import kz.kff.domain.usecase.file.command.DeleteFileUseCase
import kz.kff.domain.usecase.file.command.UpdateFileUseCase
import kz.kff.domain.usecase.file.query.GetFileByIdUseCase
import kz.kff.domain.usecase.file.query.PaginateFileUseCase
import kz.kff.infrastructure.datasource.filter.file.FileFilter
import kz.kff.infrastructure.datasource.filter.file.FileParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FileController : KoinComponent {

    private val paginateFileUseCase by inject<PaginateFileUseCase>()
    private val getFileByIdUseCase by inject<GetFileByIdUseCase>()
    private val createFileUseCase by inject<CreateFileUseCase>()
    private val bulkCreateFileUseCase by inject<BulkCreateFileUseCase>()
    private val updateFileUseCase by inject<UpdateFileUseCase>()
    private val deleteFileUseCase by inject<DeleteFileUseCase>()

    private val deleteBulkFileUseCase by inject<DeleteBulkFileUseCase>()

    fun register(route: Route, routeName: String = ApiRouteConstraints.API_FILE) {

        route.route(routeName) {
            get(
                path = ApiRouteConstraints.PAGINATE_API,
                builder = {
                    summary = "Все загруженные файлы с пагинацией"
                    description = "Загруженные файлы с пагинацией"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        fromFilter(FileFilter::class)
                    }
                    response {
                        okPaginationWrapped<FileRDTO>()
                        internalError()
                    }
                }
            ) {
                val response = paginateFileUseCase(
                    filter = FileParameter(filterClass = FileFilter::class).fromParameter(
                        call.parameters,
                        table = FileTable
                    )
                )
                call.success(response)
            }

            get(
                path = ApiRouteConstraints.GET_BY_ID_API,
                builder = {
                    summary = "Получить файл по ID"
                    description = "Получить файл по ID"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        onlyIdPath()
                    }
                    response {
                        okWrapped<FileRDTO>()
                        internalError()
                    }
                }
            ) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val result = getFileByIdUseCase(id)
                call.success(result)
            }

            post(
                path = ApiRouteConstraints.CREATE_API,
                builder = {
                    summary = "Загрузка одного файла"
                    description = "Загрузка файла"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        uploadFile()
                        oneFileUpload()
                    }
                    response {
                        okWrapped<FileRDTO>()
                        internalError()
                    }
                }
            ) {
                val skipIfNotValidate = ApiRouteConstraints.getSkipIfNotValidateParameter(call.parameters)
                val folder = ApiRouteConstraints.getFolderParameter(call.parameters)
                val multiPart = call.receiveMultipart()
                val result = createFileUseCase(skipIfNotValidate, folder, multiPart)
                call.success(result)
            }

            post(
                path = ApiRouteConstraints.CREATE_BULK_API,
                builder = {
                    summary = "Загрузка нескольких файлов"
                    description = "Загрузка нескольких файлов"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        uploadFile()
                        listFileUpload()
                    }
                    response {
                        okListWrapped<FileRDTO>()
                        internalError()
                    }
                }
            ) {
                val skipIfNotValidate = ApiRouteConstraints.getSkipIfNotValidateParameter(call.parameters)
                val folder = ApiRouteConstraints.getFolderParameter(call.parameters)
                val multiPart = call.receiveMultipart()
                val result = bulkCreateFileUseCase(skipIfNotValidate, folder, multiPart)
                call.success(result)
            }

            put(
                path = ApiRouteConstraints.UPDATE_BY_ID_API,
                builder = {
                    summary = "Обновить файл по ID"
                    description = "Обновить файл по ID"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        updateFileParams()
                        oneFileUpload()
                    }
                    response {
                        okWrapped<FileRDTO>()
                        internalError()
                    }
                }
            ) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val skipIfNotValidate = ApiRouteConstraints.getSkipIfNotValidateParameter(call.parameters)
                val deleteOldFile = ApiRouteConstraints.getDeleteOldFileParameter(call.parameters)
                val folder = ApiRouteConstraints.getFolderParameter(call.parameters)
                val multiPart = call.receiveMultipart()
                val result = updateFileUseCase(id, multiPart, deleteOldFile, skipIfNotValidate, folder)
                call.success(result)
            }

            delete(
                path = ApiRouteConstraints.DELETE_BY_ID_API,
                builder = {
                    summary = "Удалить файл по ID"
                    description = "Удалить файл по ID"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        deleteFileParams()
                    }
                    response {
                        okWrapped<Boolean>()
                        internalError()
                    }
                }
            ) {
                val id = ApiRouteConstraints.getIdFromParameter(call.parameters)
                val deleteOldFile = ApiRouteConstraints.getDeleteOldFileParameter(call.parameters)
                val result = deleteFileUseCase(id, deleteOldFile)
                call.success(result)
            }

            delete(
                path = ApiRouteConstraints.DELETE_BULK_API,
                builder = {
                    summary = "Массовое удаление файлов"
                    description = "Массовое удаление файлов по списку ID"
                    tags(ApiRouteConstraints.FILE_TAG)
                    request {
                        withLocaleHeader()
                        queryParameter<Boolean>(ApiParams.DELETE_OLD_FILE) { description = "Delete old file"; required = false }
                        body<List<Long>>()
                    }
                    response {
                        okWrapped<Int>()
                        internalError()
                    }
                }
            ) {
                val ids = call.receive<List<Long>>()
                val deleteOldFile = ApiRouteConstraints.getDeleteOldFileParameter(call.parameters)
                val result = deleteBulkFileUseCase(ids, deleteOldFile)
                call.success(result)
            }
        }
    }
}
