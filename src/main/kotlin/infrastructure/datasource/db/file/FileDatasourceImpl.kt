package kz.kff.infrastructure.datasource.db.file

import kz.kff.core.db.table.file.FileTable
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.infrastructure.datasource.db.BaseDbDatasourceImpl
import org.jetbrains.exposed.v1.jdbc.Query

class FileDatasourceImpl (
    table : FileTable,
) : BaseDbDatasourceImpl<FileTable>(table), FileDatasource {

    override fun baseJoinQuery(): Query {
        return super.baseQuery()
    }

}