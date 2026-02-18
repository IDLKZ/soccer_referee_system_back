package kz.kff.core.db.table.user

import kotlinx.datetime.LocalDate
import kz.kff.core.db.table.SoftDeleteAtTable
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.datetime.date

object UserTable : SoftDeleteAtTable("users") {

    val roleId = reference("role_id", RoleTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val imageId = reference("image_id", FileTable, onDelete = ReferenceOption.SET_NULL).nullable()

    val username: Column<String> = varchar("username", DataConstraints.StandardUniqueValueLength).uniqueIndex()
    val phone: Column<String?> = varchar("phone", DataConstraints.StandardVarcharLength).uniqueIndex().nullable()
    val email:Column<String> = varchar("email", DataConstraints.StandardUniqueValueLength).uniqueIndex("idx_users_email")

    val firstName:Column<String> = varchar("first_name", DataConstraints.StandardVarcharLength)
    val lastName:Column<String> = varchar("last_name", DataConstraints.StandardVarcharLength)
    val patronymic:Column<String?> = varchar("patronymic", DataConstraints.StandardVarcharLength).nullable()

    val passwordHash: Column<String?> = text("password_hash").nullable()
    val birthDate: Column<LocalDate?> = date("birth_date").nullable()
    val gender:Column<Int?> = integer("gender").nullable()

    val isActive:Column<Boolean> = bool("is_active").default(true)
    val isVerified:Column<Boolean> = bool("is_verified").default(false)

}