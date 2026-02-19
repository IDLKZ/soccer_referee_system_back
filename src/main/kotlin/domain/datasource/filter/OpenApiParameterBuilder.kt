package kz.kff.domain.datasource.filter

import io.github.smiley4.ktoropenapi.config.MultipartBodyConfig
import io.github.smiley4.ktoropenapi.config.MultipartPartConfig
import io.github.smiley4.ktoropenapi.config.RequestConfig
import io.github.smiley4.ktoropenapi.config.ResponsesConfig
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kz.kff.core.shared.utils.api_response.ApiCommonResponse
import kz.kff.core.shared.utils.api_response.ApiResponseError
import kz.kff.domain.dto.PaginationMeta
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object ApiParams {
    const val ID = "id"
    const val IDS = "ids"
    const val SHOW_DELETED = "showDeleted"
    const val INCLUDE_JOIN = "includeJoin"
    const val HARD_DELETE = "hardDelete"
    const val SKIP_IF_FILE_NOT_VALIDATE = "skipIfNotValidate"
    const val FOLDER = "folder"
    const val DELETE_OLD_FILE = "deleteOldFile"
}

fun <F : Any> RequestConfig.fromFilter(filterClass: KClass<F>) {

    val ctor = filterClass.primaryConstructor
        ?: error("Filter must have primary constructor")

    ctor.parameters.forEach { param ->

        val name = param.name ?: return@forEach

        // не документируем table
        if (name == "table") return@forEach

        val type = param.type
        val classifier = type.classifier
        val required = !param.isOptional && !type.isMarkedNullable

        when (classifier) {

            String::class -> queryParameter<String>(name) {
                this.required = required
            }

            Int::class -> queryParameter<Int>(name) {
                this.required = required
            }

            Long::class -> queryParameter<Long>(name) {
                this.required = required
            }

            Double::class -> queryParameter<Double>(name) {
                this.required = required
            }

            Float::class -> queryParameter<Float>(name) {
                this.required = required
            }

            Boolean::class -> queryParameter<Boolean>(name) {
                this.required = required
            }

            List::class -> {
                val argType = type.arguments.firstOrNull()?.type?.classifier

                when (argType) {

                    String::class -> queryParameter<List<String>>(name) {
                        this.required = false
                    }

                    Int::class -> queryParameter<List<Int>>(name) {
                        this.required = false
                    }

                    Long::class -> queryParameter<List<Long>>(name) {
                        this.required = false
                    }
                }
            }

            else -> {
                val kClass = classifier as? KClass<*>

                if (kClass?.java?.isEnum == true) {
                    queryParameter<String>(name) {
                        this.required = required
                        description =
                            "Allowed values: ${kClass.java.enumConstants.joinToString()}"
                    }
                }
            }
        }
    }
}

fun RequestConfig.withIdAndFlags(useSoftDelete: Boolean = false) {
    pathParameter<Long>(ApiParams.ID) {
        description = "ID"
    }
    if (useSoftDelete) {
        queryParameter<Boolean>(ApiParams.SHOW_DELETED) {
            description = "Show deleted"
            required = false
        }
    }
    queryParameter<Boolean>(ApiParams.INCLUDE_JOIN) {
        description = "Include linked"
        required = false
    }
}

fun RequestConfig.onlyIdPath() {
    pathParameter<Long>(ApiParams.ID) {
        description = "ID"
    }
}

fun RequestConfig.toDeleteParams() {
    pathParameter<Long>(ApiParams.ID) {
        description = "ID"
    }
    queryParameter<Boolean>(ApiParams.HARD_DELETE) { description = "Hard delete flag"; required = false }
}

fun RequestConfig.onlyHardDeleteParams() {
    queryParameter<Boolean>(ApiParams.HARD_DELETE) { description = "Hard delete flag"; required = false }
}


inline fun <reified T> ResponsesConfig.okWrapped() {
    HttpStatusCode.OK to { body<ApiCommonResponse<T>>() }
}

inline fun <reified T> ResponsesConfig.okPaginationWrapped() {
    HttpStatusCode.OK to { body<ApiCommonResponse<PaginationMeta<T>>>() }
}

inline fun <reified T> ResponsesConfig.okListWrapped() {
    HttpStatusCode.OK to { body<ApiCommonResponse<List<T>>>() }
}

fun ResponsesConfig.internalError() {
    HttpStatusCode.InternalServerError to {
        body<ApiCommonResponse<ApiResponseError>>()
    }
}

fun RequestConfig.withLocaleHeader() {
    headerParameter<String>("Accept-Language") {
        description = "Preferred response language (ru, en, kk)"
        required = false
    }
}

fun <D : Any> RequestConfig.fromFormField(
    dtoClass: KClass<D>,
    block: MultipartBodyConfig.() -> Unit = {}
) {
    multipartBody {
        mediaTypes(ContentType.MultiPart.FormData)

        val ctor = dtoClass.primaryConstructor
            ?: error("DTO must have primary constructor")
        ctor.parameters.forEach { param ->
            val name = param.name ?: return@forEach
            val type = param.type
            val classifier = type.classifier
            val required = !param.isOptional && !type.isMarkedNullable
            when (classifier) {
                String::class  -> part<String>(name)  { this.required = required }
                Int::class     -> part<Int>(name)     { this.required = required }
                Long::class    -> part<Long>(name)    { this.required = required }
                Double::class  -> part<Double>(name)  { this.required = required }
                Float::class   -> part<Float>(name)   { this.required = required }
                Boolean::class -> part<Boolean>(name) { this.required = required }
                List::class -> {
                    val argType = type.arguments.firstOrNull()?.type?.classifier
                    when (argType) {
                        String::class -> part<List<String>>(name) { this.required = false }
                        Int::class    -> part<List<Int>>(name)    { this.required = false }
                        Long::class   -> part<List<Long>>(name)   { this.required = false }
                    }
                }
                else -> {
                    val kClass = classifier as? KClass<*>
                    if (kClass?.java?.isEnum == true) {
                        part<String>(name) { this.required = required }
                    }
                }
            }
        }

        block()
    }
}

fun RequestConfig.uploadFile() {
    queryParameter<Boolean>(ApiParams.SKIP_IF_FILE_NOT_VALIDATE) { description = "Skip if file not valid"; required = false }
    queryParameter<String?>(ApiParams.FOLDER) { description = "Upload folder"; required = false }
}

fun RequestConfig.updateFileParams() {
    pathParameter<Long>(ApiParams.ID) {
        description = "ID"
    }
    queryParameter<Boolean>(ApiParams.SKIP_IF_FILE_NOT_VALIDATE) { description = "Skip if file not valid"; required = false }
    queryParameter<Boolean>(ApiParams.DELETE_OLD_FILE) { description = "Delete old file"; required = false }
    queryParameter<String?>(ApiParams.FOLDER) { description = "Upload folder"; required = false }
}

fun RequestConfig.deleteFileParams() {
    pathParameter<Long>(ApiParams.ID) {
        description = "ID"
    }
    queryParameter<Boolean>(ApiParams.DELETE_OLD_FILE) { description = "Delete old file"; required = false }
}

fun MultipartBodyConfig.uploadOneFilePart(name: String = "file") {
    part<File>(name) {
        mediaTypes(ContentType.Application.OctetStream)
    }
}

fun MultipartBodyConfig.uploadMultipleFilesPart(name: String = "files") {
    part<List<File>>(name) {
        mediaTypes(ContentType.Application.OctetStream)
    }
}

fun RequestConfig.oneFileUpload() {
    multipartBody {
        mediaTypes(ContentType.MultiPart.FormData)
        uploadOneFilePart()
    }
}

fun RequestConfig.listFileUpload() {
    multipartBody {
        mediaTypes(ContentType.MultiPart.FormData)
        uploadMultipleFilesPart()
    }
}

