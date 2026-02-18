package kz.kff.core.db.table.user_verification_code

import kotlinx.datetime.LocalDateTime
import kz.kff.core.db.table.BasicLongTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.datetime.datetime

object UserVerificationCodeTable : BasicLongTable("user_verification_codes") {
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)

    val code: Column<String> = varchar("code", DataConstraints.StandardVarcharLength)
    val type: Column<String> = varchar("type", DataConstraints.StandardVarcharLength)

    val expiredAt: Column<LocalDateTime?> = datetime("expired_at").nullable()
    val usedAt: Column<LocalDateTime?> = datetime("usedAt").nullable()

    val isUsed: Column<Boolean> = bool("is_used").default(false)
    val isActive: Column<Boolean> = bool("is_active").default(true)
}