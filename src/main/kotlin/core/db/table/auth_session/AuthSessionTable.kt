package kz.kff.core.db.table.auth_session

import kz.kff.core.db.table.BasicLongTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.ReferenceOption

object AuthSessionTable : BasicLongTable("auth_sessions") {

    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)
    // Device info
    val deviceName = varchar("device_name", DataConstraints.StandardVarcharLength).nullable()
    val deviceOs = varchar("device_os", DataConstraints.StandardVarcharLength).nullable()
    val deviceType = varchar("device_type", DataConstraints.StandardVarcharLength).nullable()
    val browser = varchar("browser", DataConstraints.StandardVarcharLength).nullable()
    val ipAddress = varchar("ip_address", DataConstraints.StandardVarcharLength).nullable()
    val userAgent = text("user_agent",).nullable()
    val country = varchar("country", DataConstraints.StandardVarcharLength).nullable()
    val city = varchar("city", DataConstraints.StandardVarcharLength).nullable()
    val expiresAt = long("expires_at")
    val revoked = bool("revoked").default(false)
    val revokedAt = long("revoked_at").nullable()

}