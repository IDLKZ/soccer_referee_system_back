package kz.kff.domain.usecase.shared

import jakarta.validation.Validator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kz.kff.core.exception_handlers.api.ApiValidationException
import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

abstract class UseCaseTransaction {
    protected suspend fun <T> tx(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        before: (suspend () -> Unit)? = null,
        afterCommit: (suspend () -> Unit)? = null,
        afterRollback: (suspend (Throwable) -> Unit)? = null,
        block: suspend () -> T
    ): T {
        try {
            before?.invoke()

            val result = newSuspendedTransaction(dispatcher) {
                block()
            }

            afterCommit?.invoke()
            return result
        } catch (e: Throwable) {
            afterRollback?.invoke(e)
            throw e
        }
    }

    companion object {
        fun validateDTO(validator: Validator, dto: Any) {
            val violations = validator.validate(dto)
            if (violations.isNotEmpty()) {
                val errors = violations.associate {
                    it.propertyPath.toString() to LocalizedMessage(it.message)
                }
                throw ApiValidationException(
                    LocalizedMessageConstraints.ErrorValidationdMessage,
                    errors
                )
            }
        }
    }
}