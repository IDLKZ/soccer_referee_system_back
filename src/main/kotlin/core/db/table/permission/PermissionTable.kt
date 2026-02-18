package kz.kff.core.db.table.permission

import kz.kff.core.db.table.BasicLongTable
import kz.kff.core.db.table.SoftDeleteAtTable
import kz.kff.core.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.Column

object PermissionTable : BasicLongTable("permissions") {
    val titleRu: Column<String> = varchar("title_ru", DataConstraints.StandardVarcharLength)
    val titleKk: Column<String?> = varchar("title_kk", DataConstraints.StandardVarcharLength).nullable()
    val titleEn: Column<String?> = varchar("title_en", DataConstraints.StandardVarcharLength).nullable()
    val value: Column<String> = varchar("value", DataConstraints.StandardUniqueValueLength).uniqueIndex("idx_permissions_value")
}