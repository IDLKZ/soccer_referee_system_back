package kz.kff.infrastructure.datasource.filter.file

import kz.kff.core.db.table.file.FileTable
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq

class FileFilter(
    table : FileTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    //
    override val perPage: Int = 20,
    override val page: Int = 1,
    val isLocalStored: Boolean? = null,
    val moreThanInByte:Long? = null,
    val lessThanInByte:Long? = null,

) : BasePaginationFilter<FileTable>(table) {

    override fun getSearchColumns(): List<Column<*>> {
        return listOf(
            table.extension,
            table.mimeType,
            table.fullPath,
            table.directory,
            table.originalName,
            table.uniqueFileName,
        )
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        isLocalStored?.let {
            conditions.add(table.storedLocal.eq(isLocalStored))
        }
        moreThanInByte?.let {
            conditions.add(table.fileSizeByte.greaterEq(moreThanInByte))
        }
        lessThanInByte?.let {
            conditions.add(table.fileSizeByte.lessEq(lessThanInByte))
        }

        return conditions.reduceOrNull { acc, op -> acc and op }
    }


}