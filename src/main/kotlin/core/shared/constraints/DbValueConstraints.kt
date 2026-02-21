package kz.kff.core.shared.constraints

class DbValueConstraints {
    companion object{
        const val ADMIN_ROLE_VALUE = "ADMIN"
        const val MODERATOR_ROLE_VALUE = "MODERATOR"
        const val MANAGER_ROLE_VALUE = "MANAGER"

        //Role Permissions
        const val ROLE_CREATE_PERMISSION_VALUE = "role_create"
        const val ROLE_EDIT_PERMISSION_VALUE = "role_edit"
        const val ROLE_DELETE_PERMISSION_VALUE = "role_delete"
        const val ROLE_INDEX_PERMISSION_VALUE = "role_index"
        //Permission
        const val PERMISSION_CREATE_PERMISSION_VALUE = "permission_create"
        const val PERMISSION_EDIT_PERMISSION_VALUE = "permission_edit"
        const val PERMISSION_DELETE_PERMISSION_VALUE = "permission_delete"
        const val PERMISSION_INDEX_PERMISSION_VALUE = "permission_index"
        //Role Permission
        const val ROLE_PERMISSION_CREATE_PERMISSION_VALUE = "role_permission_create"
        const val ROLE_PERMISSION_EDIT_PERMISSION_VALUE = "role_permission_edit"
        const val ROLE_PERMISSION_DELETE_PERMISSION_VALUE = "role_permission_delete"
        const val ROLE_PERMISSION_INDEX_PERMISSION_VALUE = "role_permission_index"
        //User Permissions
        const val USER_CREATE_PERMISSION_VALUE = "user_create"
        const val USER_EDIT_PERMISSION_VALUE = "user_edit"
        const val USER_DELETE_PERMISSION_VALUE = "user_delete"
        const val USER_INDEX_PERMISSION_VALUE = "user_index"
        //File
        const val FILE_CREATE_PERMISSION_VALUE = "file_create"
        const val FILE_EDIT_PERMISSION_VALUE = "file_edit"
        const val FILE_DELETE_PERMISSION_VALUE = "file_delete"
        const val FILE_INDEX_PERMISSION_VALUE = "file_index"
    }
}