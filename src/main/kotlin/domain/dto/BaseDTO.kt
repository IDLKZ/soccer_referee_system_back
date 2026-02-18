package kz.kff.domain.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

interface BaseCreateDTO<T : LongIdTable> {

    fun bindMap(
        table: T
    ): Map<Column<*>, Any?> {

        val dtoProps = this::class.memberProperties

        // Map each Column to its Kotlin property name in the table
        val columnToPropertyName: Map<Column<*>, String> = table::class.memberProperties
            .mapNotNull { prop ->
                prop.isAccessible = true
                val value = prop.getter.call(table)
                if (value is Column<*>) value to prop.name else null
            }
            .toMap()

        return table.columns.associateWith { column ->
            val propertyName = columnToPropertyName[column]

            dtoProps
                .firstOrNull { it.name == propertyName }
                ?.apply { isAccessible = true }
                ?.getter
                ?.call(this)
        }
    }
}
interface BaseUpdateDTO<T : LongIdTable> :
    BaseCreateDTO<T> {

    fun applyUpdate(
        table: T,
        stmt: UpdateStatement,
        includeNulls: Boolean = false
    ) {

        stmt.applyMap(
            bindMap(table),
            includeNulls
        )
    }
}


interface BaseOneCreateDTO<T : LongIdTable> :
    BaseCreateDTO<T> {

    fun createEntity(
        table: T
    ): InsertStatement<Number>.() -> Unit = {

        this.applyMap(
            bindMap(table)
        )
    }
}
/**
 * Оборачивает Long в EntityID, если колонка является reference (foreign key).
 * Exposed reference-колонки имеют тип Column<EntityID<Long>>, а DTO передаёт Long.
 */
@Suppress("UNCHECKED_CAST")
private fun wrapEntityIdIfNeeded(col: Column<*>, value: Any?): Any? {
    if (value == null) return null
    if (value is Long && col.referee != null) {
        val refTable = col.referee!!.table
        if (refTable is IdTable<*>) {
            return EntityID(value, refTable as IdTable<Long>)
        }
    }
    return value
}

@Suppress("UNCHECKED_CAST")
fun UpdateStatement.applyMap(
    map: Map<Column<*>, Any?>,
    includeNulls: Boolean = false
) {
    map.forEach { (col, value) ->
        val wrapped = wrapEntityIdIfNeeded(col, value)
        if (wrapped != null || includeNulls) {
            this[col as Column<Any?>] = wrapped
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun InsertStatement<Number>.applyMap(
    map: Map<Column<*>, Any?>
) {
    map.forEach { (col, value) ->
        val wrapped = wrapEntityIdIfNeeded(col, value)
        if (wrapped != null) {
            this[col as Column<Any?>] = wrapped
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun BatchInsertStatement.applyMap(
    map: Map<Column<*>, Any?>
) {
    map.forEach { (col, value) ->
        val wrapped = wrapEntityIdIfNeeded(col, value)
        if (wrapped != null) {
            this[col as Column<Any?>] = wrapped
        }
    }
}

object LocalDateTimeSerializer :
    KSerializer<LocalDateTime> {

    override val descriptor = PrimitiveSerialDescriptor(
        "LocalDateTime",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: LocalDateTime
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(
        decoder: Decoder
    ): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

