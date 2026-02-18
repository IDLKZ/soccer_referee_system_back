package kz.kff.domain.datasource.db.filter
import io.ktor.http.Parameters
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor

abstract class BaseQueryParameter<
        T : LongIdTable,
        F : BaseFilter<T>
        >(
    private val filterClass: KClass<F>
) {

    open fun fromParameter(
        parameters: Parameters,
        table: T
    ): F {

        val ctor = filterClass.primaryConstructor
            ?: error("Filter must have primary constructor")

        val args = mutableMapOf<KParameter, Any?>()

        ctor.parameters.forEach { param ->

            val name = param.name ?: return@forEach

            when (name) {
                "table" -> args[param] = table
                else -> {
                    val rawValue = parameters[name]
                    val converted = convertValue(rawValue, param.type)
                    if (converted != null || !param.isOptional) {
                        args[param] = converted
                    }
                }
            }
        }

        return ctor.callBy(args)
    }
}
private fun convertValue(
    value: String?,
    type: KType
): Any? {

    if (value == null) return null

    val classifier = type.classifier

    return when (classifier) {

        String::class -> value

        Int::class -> value.toIntOrNull()

        Long::class -> value.toLongOrNull()

        Double::class -> value.toDoubleOrNull()

        Float::class -> value.toFloatOrNull()

        Boolean::class -> value.toBooleanStrictOrNull()

        List::class -> parseList(value, type)

        else -> parseEnum(value, classifier)
    }
}

private fun parseList(
    value: String,
    type: KType
): List<Any?> {

    val argType = type.arguments.first().type?.classifier

    return value.split(",").mapNotNull {

        when (argType) {
            Long::class -> it.toLongOrNull()
            Int::class -> it.toIntOrNull()
            String::class -> it
            else -> null
        }
    }
}


private fun parseEnum(
    value: String,
    classifier: Any?
): Any? {

    if (classifier !is KClass<*>) return value

    if (!classifier.java.isEnum) return value

    return classifier.java.enumConstants
        .firstOrNull { (it as Enum<*>).name == value }
}