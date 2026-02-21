package kz.kff.core.shared.constraints

import io.ktor.http.Parameters
import io.ktor.http.parameters
import io.ktor.server.plugins.BadRequestException
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toLongOrNull

class ApiRouteConstraints {
    companion object{
        const val API = "api"
        const val PAGINATE_API = "/paginate"
        const val ALL_API = "/all"
        const val CREATE_API = "/create"
        const val UPDATE_BY_ID_API = "/update/{id}"
        const val GET_BY_ID_API = "/get/{id}"
        const val DELETE_BY_ID_API = "/delete/{id}"
        const val RESTORE_BY_ID_API = "/restore/{id}"

        const val CREATE_BULK_API = "/bulk/create"
        const val UPDATE_BULK_API = "/bulk/update"
        const val DELETE_BULK_API = "/bulk/delete"
        const val RESTORE_BULK_API = "/bulk/restore"

        const val SIGN_IN_API = "/sign-in"
        const val SIGN_UP_API = "/sign-up"
        const val REFRESH_API = "/refresh"
        const val GET_ME_API = "/me"

        const val API_ROLES = "/roles"
        const val API_PERMISSIONS = "/permissions"
        const val API_ROLE_PERMISSIONS = "/role-permissions"
        const val API_FILE = "/file"
        const val API_USERS = "/users"
        const val API_AUTH_SESSIONS = "/auth-sessions"
        const val API_AUTH = "/auth"

        const val ROLE_TAG = "Roles-Роли"
        const val PERMISSION_TAG = "Permission-Разрешения"
        const val ROLE_PERMISSION_TAG = "Role-Permission-Роли-Разрешения"
        const val FILE_TAG = "File-Файловый сервис"
        const val USER_TAG = "Users-Пользователи"
        const val AUTH_SESSION_TAG = "Auth-Sessions-Сессии"
        const val AUTH_TAG = "Auth-Аутентификация"


        fun getIdFromParameter(parameters: Parameters,idName:String = "id"):Long{
            return parameters[idName]?.toLongOrNull()
                ?: throw BadRequestException(LocalizedMessageConstraints.RequestInvalidIdErrorMessage)
        }

        fun getIDSFromQueryParameter(parameters: Parameters,idsName:String = "ids"):List<Long>{
            return parameters["ids"]
                ?.split(",")
                ?.map { it.toLong() }
                ?: throw BadRequestException(LocalizedMessageConstraints.RequestInvalidIdsErrorMessage)
        }

        fun getHardDeleteParameter(parameters: Parameters,parameterName:String = "hardDelete"):Boolean{
            return parameters[parameterName]?.toBooleanStrictOrNull() ?: false
        }

        fun getIncludeJoinParameter(parameters: Parameters,parameterName:String = "includeJoin"):Boolean{
            return parameters[parameterName]?.toBooleanStrictOrNull() ?: false
        }

        fun getShowDeletedParameter(parameters: Parameters,parameterName:String = "showDeleted"):Boolean?{
            return  parameters[parameterName]?.toBooleanStrictOrNull()
        }

        fun getSkipIfNotValidateParameter(parameters: Parameters,parameterName:String = "skipIfNotValidate"):Boolean{
            return  parameters[parameterName]?.toBooleanStrictOrNull() ?: true
        }

        fun getFolderParameter(parameters: Parameters,parameterName:String = "folder"): String?{
            return  parameters[parameterName]?.toString()
        }

        fun getDeleteOldFileParameter(parameters: Parameters, parameterName: String = "deleteOldFile"): Boolean {
            return parameters[parameterName]?.toBooleanStrictOrNull() ?: false
        }

    }
}