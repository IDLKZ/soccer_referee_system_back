package kz.kff.core.shared.constraints

class ValidationConstraints {
    companion object{
        const val KZ_MOBILE_REGEX = "^77\\d{9}$"
        const val LOGIN_REGEX = "^[a-zA-Z0-9._@-]{3,255}$"
        const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        const val PHONE_PARTIAL_REGEX = "^7[0-9]*$"
        const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_\\-+=]).{8,}$"
        const val IIN_REGES = "^\\d{12}$"
    }
}