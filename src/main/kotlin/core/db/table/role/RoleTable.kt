package kz.kff.core.db.table.role
import kz.kff.core.db.table.SoftDeleteAtTable
import kz.kff.core.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.Column

object RoleTable : SoftDeleteAtTable("roles") {
    val titleRu: Column<String> = varchar("title_ru", DataConstraints.StandardVarcharLength)
    val titleKk: Column<String?> = varchar("title_kk", DataConstraints.StandardVarcharLength).nullable()
    val titleEn: Column<String?> = varchar("title_en", DataConstraints.StandardVarcharLength).nullable()
    val descriptionRu: Column<String?> = text("description_ru").nullable()
    val descriptionKk: Column<String?> = text("description_kk").nullable()
    val descriptionEn: Column<String?> = text("description_en").nullable()
    val value: Column<String> = varchar("value", DataConstraints.StandardUniqueValueLength).uniqueIndex("idx_roles_value")
    val isSystem: Column<Boolean> = bool("isSystem").default(false).index("idx_roles_system")
    val isAdministrative: Column<Boolean> = bool("isAdministrative").default(false).index("idx_roles_administrative")
}