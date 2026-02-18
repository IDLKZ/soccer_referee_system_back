package kz.kff.presentation.http.file

import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.ContentType
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.shared.constraints.ApiRouteConstraints
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.fromFilter
import kz.kff.domain.datasource.filter.internalError
import kz.kff.domain.datasource.filter.okPaginationWrapped
import kz.kff.domain.datasource.filter.okWrapped
import kz.kff.domain.datasource.filter.oneFileUpload
import kz.kff.domain.datasource.filter.uploadFile
import kz.kff.domain.datasource.filter.withLocaleHeader
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.usecase.file.command.CreateFileUseCase
import kz.kff.domain.usecase.file.query.PaginateFileUseCase
import kz.kff.infrastructure.datasource.filter.file.FileFilter
import kz.kff.infrastructure.datasource.filter.file.FileParameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class FileController : KoinComponent {

    private val paginateFileUseCase by inject<PaginateFileUseCase>()
    private val createFileUseCase by inject<CreateFileUseCase>()

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
        }
    }
}
