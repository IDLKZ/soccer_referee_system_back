package kz.kff.core.shared.constraints

class LocalizedMessageConstraints {
    companion object{
        const val ErrorBadRequestMessage: String = "error.bad_request"
        const val ErrorConflictMessage: String = "error.conflict_exception"
        const val ErrorForbiddenMessage: String = "error.forbidden_exception"
        const val ErrorInternalMessage: String = "error.internal_exception"
        const val ErrorNotFoundMessage: String = "error.not_found_exception"
        const val ErrorUnauthorizedMessage: String = "error.unauthorized_exception"
        const val ErrorValidationdMessage: String = "error.validation_exception"

        const val ValidationFieldRequiredMessage:String = "validation.field_required"
        const val ValidationFieldMaxSizeMessage:String = "validation.max_size"
        const val ValidationUniqueValueExistMessage:String = "validation.value_exist"
        const val ValidationUniqueEmailExistMessage:String = "validation.email_exist"
        const val ValidationUniquePhoneExistMessage:String = "validation.phone_exist"
        const val ValidationUniqueUsernameExistMessage:String = "validation.username_exist"
        const val ValidationPasswordPatternMessage:String = "validation.password_pattern"
        const val ValidationLoginPatternMessage:String = "validation.login_pattern"

        const val RequestInvalidIdErrorMessage: String = "request.invalid_id_in_request"
        const val RequestInvalidIdsErrorMessage: String = "request.invalid_ids_in_request"

        //Role
        const val RoleNotFoundMessage: String = "role.role_not_found"

        //Permission
        const val PermissionNotFoundMessage: String = "permission.permission_not_found"

        //RolePermission
        const val RolePermissionNotFoundMessage: String = "permission.role_permission_not_found"

        //User
        const val UserNotFoundMessage: String = "user.user_not_found"

        //AuthSession
        const val AuthSessionNotFoundMessage: String = "auth_session.not_found"

        const val ValidationLongMaxSizeExceed:String = "max_size.exceeded"
        const val ErrorFileUpload:String = "error.file_upload"
        const val FileNameRequiredFileUpload:String = "error.file_name_required"

        //Auth
        const val LoginUserNotFoundMessage: String = "auth.user_not_found"
        const val PasswordNotVerifiedMessage: String = "auth.password_not_verified"
    }
}