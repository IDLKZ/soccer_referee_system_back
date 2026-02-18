package kz.kff.infrastructure.datasource.db

import kz.kff.core.db.table.SoftDeleteAtTable
import kz.kff.core.db.table.SoftIsDeleteTable
import kz.kff.domain.datasource.db.BaseDbDatasource
import kz.kff.domain.datasource.db.filter.BaseFilter
import kz.kff.domain.datasource.db.filter.BasePaginationFilter
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.buildPaginationMeta
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory

abstract class BaseDbDatasourceImpl<T : LongIdTable>(
    protected val table: T
) : BaseDbDatasource<T> {
    private val logger = LoggerFactory.getLogger(BaseDbDatasourceImpl::class.java)

    // Без join
    protected open fun baseQuery(): Query = table.selectAll()

    // С join — наследники переопределяют
    protected open fun baseJoinQuery(): Query = table.selectAll()

    private fun chooseQuery(includeJoin: Boolean?): Query {
        if (includeJoin != null && includeJoin) {
            return baseJoinQuery()
        }
        return baseQuery()
    }

    override suspend fun <DTO> findByLongId(
        id: Long,
        includeJoin: Boolean?,
        showDeleted: Boolean?,
        mapper: (List<ResultRow>) -> List<DTO>
    ): DTO? {
        val deletedCondition = useShowDeleted(showDeleted)
        val query = chooseQuery(includeJoin)
        val rows = query
            .where { table.id eq id and (deletedCondition ?: Op.TRUE) }
            .toList()
        return mapper(rows).firstOrNull()
    }

    override suspend fun <DTO> findOneByFilter(
        filter: BaseFilter<T>,
        mapper: (ResultRow) -> DTO
    ): DTO? {
        val query = if (filter.includeJoin) baseJoinQuery() else baseQuery()
        filter.buildConditions()?.let { query.where { it } }
        return query
            .orderBy(filter.getOrderColumn() to filter.getOrderDirection())
            .singleOrNull()
            ?.let(mapper)
    }

    override suspend fun <DTO> findAllWithFilter(
        filter: BaseFilter<T>,
        mapper: (List<ResultRow>) -> List<DTO>
    ): List<DTO> {
        val base = if (filter.includeJoin) baseJoinQuery() else baseQuery()
        val query = filter.buildConditions()
            ?.let { base.where { it } }
            ?: base
        val rows = query
            .orderBy(filter.getOrderColumn() to filter.getOrderDirection())
            .toList()
        return mapper(rows)
    }

    override suspend fun <DTO> all(
        includeJoin: Boolean?,
        showDeleted: Boolean?,
        mapper: (List<ResultRow>) -> List<DTO>
    ): List<DTO> {
        val base = chooseQuery(includeJoin)
        val query = useShowDeleted(showDeleted)
            ?.let { base.where { it } }
            ?: base
        return mapper(query.toList())
    }

    override suspend fun count(): Long {
        return table.selectAll().count()
    }

    override suspend fun countWithFilter(filter: BaseFilter<T>): Long {
        val base = if (filter.includeJoin) baseJoinQuery() else baseQuery()
        val query = filter.buildConditions()
            ?.let { base.where { it } }
            ?: base
        return query.count()
    }

    override suspend fun <DTO> paginate(
        filter: BasePaginationFilter<T>,
        mapper: (List<ResultRow>) -> List<DTO>
    ): PaginationMeta<DTO> {
        val page = filter.validPage
        val perPage = filter.validPerPage

        if (filter.includeJoin) {
            // Двухэтапная пагинация: count и ID по основной таблице, JOIN только для выбранных ID
            val countBase = baseQuery()
            val countQuery = filter.buildConditions()
                ?.let { countBase.where { it } }
                ?: countBase
            val count = countQuery.count()

            val ids = countQuery
                .orderBy(filter.getOrderColumn() to filter.getOrderDirection())
                .limit(perPage.toInt())
                .offset(((page - 1) * perPage).toLong())
                .map { it[table.id].value }

            val joinRows = baseJoinQuery()
                .where { table.id inList ids }
                .orderBy(filter.getOrderColumn() to filter.getOrderDirection())
                .toList()

            return buildPaginationMeta(
                items = mapper(joinRows),
                currentPage = page,
                perPage = perPage,
                totalRecords = count
            )
        }

        // Простая пагинация без join
        val base = baseQuery()
        val query = filter.buildConditions()
            ?.let { base.where { it } }
            ?: base

        val orderedQuery = query.orderBy(filter.getOrderColumn() to filter.getOrderDirection())
        val count = orderedQuery.count()
        val rows = orderedQuery
            .limit(perPage.toInt())
            .offset(((page - 1) * perPage).toLong())
            .toList()

        return buildPaginationMeta(
            items = mapper(rows),
            currentPage = page,
            perPage = perPage,
            totalRecords = count
        )
    }

    override suspend fun <DTO> create(
        insertBlock: InsertStatement<Number>.() -> Unit,
        mapper: (ResultRow) -> DTO
    ): DTO? {
        return table.insert {
            it.insertBlock()
        }.resultedValues!!
            .single()
            .let(mapper)
    }

    override suspend fun <DTO> update(
        id: Long,
        updateBlock: UpdateStatement.() -> Unit,
        mapper: (ResultRow) -> DTO
    ): DTO? {
        val updated: Int = table.update({ table.id eq id }) {
            it.updateBlock()
        }
        return updated
            .takeIf { it > 0 }
            ?.let {
                baseQuery()
                    .where { table.id eq id }
                    .singleOrNull()
                    ?.let(mapper)
            }
    }

    override suspend fun delete(
        id: Long,
        hardDelete: Boolean
    ): Boolean {
        return when {
            !isUsingSoftDelete() -> hardDelete(id)
            hardDelete -> hardDelete(id)
            else -> softDelete(id)
        }
    }

    override suspend fun restore(
        id: Long
    ): Boolean {
        return when {
            !isUsingSoftDelete() -> false
            else -> restoreSoftDelete(id)
        }
    }

    override suspend fun <DTO, ITEM> bulkCreate(
        items: List<ITEM>,
        insertBlock: BatchInsertStatement.(ITEM) -> Unit,
        mapper: (ResultRow) -> DTO
    ): List<DTO> {
        if (items.isEmpty()) return emptyList()

        return table.batchInsert(items) { item ->
            insertBlock(item)
        }.map(mapper)
    }

    override suspend fun <DTO> bulkUpdate(
        updates: List<Pair<Long, UpdateStatement.() -> Unit>>,
        mapper: (ResultRow) -> DTO
    ): List<DTO> {
        return updates.mapNotNull { (id, updateBlock) ->
            val updated = table.update({ table.id eq id }) {
                it.updateBlock()
            }
            if (updated > 0) {
                baseQuery()
                    .where { table.id eq id }
                    .singleOrNull()
                    ?.let(mapper)
            } else null
        }
    }

    override suspend fun bulkDelete(
        ids: List<Long>,
        hardDelete: Boolean
    ): Int {
        return if (hardDelete || !isUsingSoftDelete()) {
            table.deleteWhere { table.id inList ids }
        } else {
            when (table) {
                is SoftDeleteAtTable ->
                    table.update(
                        where = { table.id inList ids }
                    ) {
                        it[deletedAt] = CurrentDateTime
                    }

                is SoftIsDeleteTable ->
                    table.update(
                        where = { table.id inList ids }
                    ) {
                        it[isDeleted] = true
                    }

                else -> 0
            }
        }
    }

    override suspend fun bulkRestore(ids: List<Long>): Int {
        return when (table) {
            is SoftDeleteAtTable ->
                table.update(
                    where = { table.id inList ids }
                ) {
                    it[deletedAt] = null
                }

            is SoftIsDeleteTable ->
                table.update(
                    where = { table.id inList ids }
                ) {
                    it[isDeleted] = false
                }

            else -> 0
        }
    }

    private fun useShowDeleted(showDeleted: Boolean?): Op<Boolean>? {
        return when {
            table is SoftDeleteAtTable && showDeleted != null -> {
                if (showDeleted) table.deletedAt.isNotNull() else table.deletedAt.isNull()
            }
            table is SoftIsDeleteTable && showDeleted != null -> {
                table.isDeleted eq showDeleted
            }
            else -> null
        }
    }

    private fun softDelete(id: Long): Boolean {
        return when (table) {
            is SoftDeleteAtTable ->
                table.update({ table.id eq id }) {
                    it[deletedAt] = CurrentDateTime
                } > 0

            is SoftIsDeleteTable ->
                table.update({ table.id eq id }) {
                    it[isDeleted] = true
                } > 0

            else -> false
        }
    }

    private fun restoreSoftDelete(id: Long): Boolean {
        return when (table) {
            is SoftDeleteAtTable ->
                table.update({ table.id eq id }) {
                    it[deletedAt] = null
                } > 0

            is SoftIsDeleteTable ->
                table.update({ table.id eq id }) {
                    it[isDeleted] = false
                } > 0

            else -> false
        }
    }

    private fun hardDelete(id: Long): Boolean {
        return table.deleteWhere { table.id eq id }.let { it > 0 }
    }

    private fun isUsingSoftDelete(): Boolean {
        return when (table) {
            is SoftDeleteAtTable -> true
            is SoftIsDeleteTable -> true
            else -> false
        }
    }
}
