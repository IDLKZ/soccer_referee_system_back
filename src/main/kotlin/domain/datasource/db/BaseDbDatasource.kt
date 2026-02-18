package kz.kff.domain.datasource.db

import kz.kff.domain.datasource.db.filter.BaseFilter
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import kz.kff.domain.dto.PaginationMeta
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

/**
 * Оборачивает построчный маппер в групповой.
 * Использование: eachRow { it.toSomeDTO() }
 */
fun <T> eachRow(mapper: (ResultRow) -> T): (List<ResultRow>) -> List<T> = { rows -> rows.map(mapper) }

interface BaseDbDatasource<T: LongIdTable> {
    suspend fun <DTO> findByLongId(
        id: Long,
        includeJoin: Boolean? = false,
        showDeleted: Boolean? = false,
        mapper: (List<ResultRow>) -> List<DTO>
    ): DTO?

    suspend fun <DTO> findOneByFilter(
        filter: BaseFilter<T>,
        mapper: (ResultRow) -> DTO
    ): DTO?

    suspend fun <DTO> findAllWithFilter(
        filter: BaseFilter<T>,
        mapper: (List<ResultRow>) -> List<DTO>
    ): List<DTO>

    suspend fun <DTO> all(
        includeJoin: Boolean? = false,
        showDeleted: Boolean? = false,
        mapper: (List<ResultRow>) -> List<DTO>
    ): List<DTO>

    suspend fun count(): Long
    suspend fun countWithFilter(filter: BaseFilter<T>): Long

    suspend fun <DTO> paginate(
        filter: BasePaginationFilter<T>,
        mapper: (List<ResultRow>) -> List<DTO>
    ): PaginationMeta<DTO>

    suspend fun <DTO> create(
        insertBlock: InsertStatement<Number>.() -> Unit,
        mapper: (ResultRow) -> DTO
    ): DTO?

    suspend fun <DTO> update(
        id: Long,
        updateBlock: UpdateStatement.() -> Unit,
        mapper: (ResultRow) -> DTO
    ): DTO?

    suspend fun delete(
        id: Long,
        hardDelete: Boolean = false,
    ): Boolean

    suspend fun restore(
        id: Long,
    ): Boolean

    suspend fun <DTO, ITEM> bulkCreate(
        items: List<ITEM>,
        insertBlock: BatchInsertStatement.(ITEM) -> Unit,
        mapper: (ResultRow) -> DTO
    ): List<DTO>

    suspend fun <DTO> bulkUpdate(
        updates: List<Pair<Long, UpdateStatement.() -> Unit>>,
        mapper: (ResultRow) -> DTO
    ): List<DTO>

    suspend fun bulkDelete(
        ids: List<Long>,
        hardDelete: Boolean = false
    ): Int

    suspend fun bulkRestore(
        ids: List<Long>
    ): Int
}
