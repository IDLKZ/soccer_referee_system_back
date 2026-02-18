package kz.kff.core.shared.constraints

sealed class ApiInnerStatusCode(
    val innerStatusCode: Int,
) {
    //If Not Verifier Number
    object UserNumberIsNotVerifier:ApiInnerStatusCode(
        innerStatusCode = 1401
    )
}
