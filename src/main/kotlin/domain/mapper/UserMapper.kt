package kz.kff.domain.mapper

import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.dto.user.UserSDTO
import kz.kff.domain.dto.user.UserWithRelationsDTO
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toUserRDTO(): UserRDTO {
    return UserRDTO(
        id = this[UserTable.id].value,
        roleId = this[UserTable.roleId]?.value,
        imageId = this[UserTable.imageId]?.value,
        username = this[UserTable.username],
        phone = this[UserTable.phone],
        email = this[UserTable.email],
        firstName = this[UserTable.firstName],
        lastName = this[UserTable.lastName],
        patronymic = this[UserTable.patronymic],
        birthDate = this[UserTable.birthDate],
        gender = this[UserTable.gender],
        isActive = this[UserTable.isActive],
        isVerified = this[UserTable.isVerified],
        createdAt = this[UserTable.createdAt],
        updatedAt = this[UserTable.updatedAt],
        deletedAt = this[UserTable.deletedAt],
    )
}

fun ResultRow.toUserSDTO(): UserSDTO {
    return UserSDTO(
        id = this[UserTable.id].value,
        username = this[UserTable.username],
        firstName = this[UserTable.firstName],
        lastName = this[UserTable.lastName],
        email = this[UserTable.email],
        phone = this[UserTable.phone],
    )
}

fun ResultRow.toUserWithRelationsDTO(): UserWithRelationsDTO {
    return UserWithRelationsDTO(
        id = this[UserTable.id].value,
        username = this[UserTable.username],
        phone = this[UserTable.phone],
        email = this[UserTable.email],
        firstName = this[UserTable.firstName],
        lastName = this[UserTable.lastName],
        patronymic = this[UserTable.patronymic],
        birthDate = this[UserTable.birthDate],
        gender = this[UserTable.gender],
        isActive = this[UserTable.isActive],
        isVerified = this[UserTable.isVerified],
        role = this.getOrNull(RoleTable.id)?.let { this.toRoleSDTO() },
        image = this.getOrNull(FileTable.id)?.let { this.toFileRDTO() },
        createdAt = this[UserTable.createdAt],
        updatedAt = this[UserTable.updatedAt],
        deletedAt = this[UserTable.deletedAt],
    )
}

fun Iterable<ResultRow>.toUserRDTOList() = map { it.toUserRDTO() }

fun Iterable<ResultRow>.toUserSDTOList() = map { it.toUserSDTO() }

fun Iterable<ResultRow>.toUserWithRelationsDTOList() = map { it.toUserWithRelationsDTO() }
