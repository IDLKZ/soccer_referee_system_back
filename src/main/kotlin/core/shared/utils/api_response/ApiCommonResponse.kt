package kz.kff.core.shared.utils.api_response

import kotlinx.serialization.Serializable
import kz.kff.core.config.AppEnvironment

@Serializable
data class ApiCommonResponse<T>(
    val message: String?,
    val data: T,
    val code: Int?,
    val error:ApiResponseError?
)

@Serializable data class ApiResponseError(
    val innerCode: Int?,
    val message: String?,
    val detail: String?,
    val validationError: Map<String, String>?,
    val errorTrace: String?,
)

@Serializable
data class EmptyData(val empty: Boolean = true)

fun isShowError(env: AppEnvironment): Boolean {
    return when (env) {
        AppEnvironment.DEV,
        AppEnvironment.TEST -> true
        AppEnvironment.PROD -> false
    }
}