package kz.kff.domain.mapper

import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.domain.dto.role.RoleWithPermissionsDTO
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.dto.user.UserSDTO
import kz.kff.domain.dto.user.UserSecretRDTO
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

fun ResultRow.toUserSecretRDTO(): UserSecretRDTO {
    return UserSecretRDTO(
        id = this[UserTable.id].value,
        username = this[UserTable.username],
        phone = this[UserTable.phone],
        email = this[UserTable.email],
        firstName = this[UserTable.firstName],
        lastName = this[UserTable.lastName],
        patronymic = this[UserTable.patronymic],
        passwordHash = this[UserTable.passwordHash],
        birthDate = this[UserTable.birthDate],
        gender = this[UserTable.gender],
        isActive = this[UserTable.isActive],
        isVerified = this[UserTable.isVerified],
        role = this.getOrNull(RoleTable.id)?.let { this.toRoleWithPermissionsDTO() },
        image = this.getOrNull(FileTable.id)?.let { this.toFileRDTO() },
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
        role = this.getOrNull(RoleTable.id)?.let { this.toRoleWithPermissionsDTO() },
        image = this.getOrNull(FileTable.id)?.let { this.toFileRDTO() },
        createdAt = this[UserTable.createdAt],
        updatedAt = this[UserTable.updatedAt],
        deletedAt = this[UserTable.deletedAt],
    )
}

fun Iterable<ResultRow>.toUserRDTOList() = map { it.toUserRDTO() }

fun Iterable<ResultRow>.toUserSecretRDTOList() = map { it.toUserSecretRDTO() }

fun Iterable<ResultRow>.toUserSDTOList() = map { it.toUserSDTO() }

fun Iterable<ResultRow>.toUserWithRelationsDTOList() = map { it.toUserWithRelationsDTO() }

fun Iterable<ResultRow>.toUserSecretRDTOGrouped(): List<UserSecretRDTO> {
    return groupBy { it[UserTable.id].value }.map { (_, rows) ->
        val first = rows.first()
        val role = first.getOrNull(RoleTable.id)?.let {
            RoleWithPermissionsDTO(
                id = first[RoleTable.id].value,
                titleRu = first[RoleTable.titleRu],
                titleKk = first[RoleTable.titleKk],
                titleEn = first[RoleTable.titleEn],
                value = first[RoleTable.value],
                isSystem = first[RoleTable.isSystem],
                isAdministrative = first[RoleTable.isAdministrative],
                permissions = rows
                    .filter { it.getOrNull(PermissionTable.id) != null }
                    .map { it.toPermissionRDTO() },
                createdAt = first[RoleTable.createdAt],
                updatedAt = first[RoleTable.updatedAt],
                deletedAt = first[RoleTable.deletedAt],
            )
        }
        UserSecretRDTO(
            id = first[UserTable.id].value,
            username = first[UserTable.username],
            phone = first[UserTable.phone],
            email = first[UserTable.email],
            firstName = first[UserTable.firstName],
            lastName = first[UserTable.lastName],
            patronymic = first[UserTable.patronymic],
            passwordHash = first[UserTable.passwordHash],
            birthDate = first[UserTable.birthDate],
            gender = first[UserTable.gender],
            isActive = first[UserTable.isActive],
            isVerified = first[UserTable.isVerified],
            role = role,
            image = first.getOrNull(FileTable.id)?.let { first.toFileRDTO() },
            createdAt = first[UserTable.createdAt],
            updatedAt = first[UserTable.updatedAt],
            deletedAt = first[UserTable.deletedAt],
        )
    }
}

fun Iterable<ResultRow>.toUserWithRelationsDTOGrouped(): List<UserWithRelationsDTO> {
    return groupBy { it[UserTable.id].value }.map { (_, rows) ->
        val first = rows.first()
        val role = first.getOrNull(RoleTable.id)?.let {
            RoleWithPermissionsDTO(
                id = first[RoleTable.id].value,
                titleRu = first[RoleTable.titleRu],
                titleKk = first[RoleTable.titleKk],
                titleEn = first[RoleTable.titleEn],
                value = first[RoleTable.value],
                isSystem = first[RoleTable.isSystem],
                isAdministrative = first[RoleTable.isAdministrative],
                permissions = rows
                    .filter { it.getOrNull(PermissionTable.id) != null }
                    .map { it.toPermissionRDTO() },
                createdAt = first[RoleTable.createdAt],
                updatedAt = first[RoleTable.updatedAt],
                deletedAt = first[RoleTable.deletedAt],
            )
        }
        UserWithRelationsDTO(
            id = first[UserTable.id].value,
            username = first[UserTable.username],
            phone = first[UserTable.phone],
            email = first[UserTable.email],
            firstName = first[UserTable.firstName],
            lastName = first[UserTable.lastName],
            patronymic = first[UserTable.patronymic],
            birthDate = first[UserTable.birthDate],
            gender = first[UserTable.gender],
            isActive = first[UserTable.isActive],
            isVerified = first[UserTable.isVerified],
            role = role,
            image = first.getOrNull(FileTable.id)?.let { first.toFileRDTO() },
            createdAt = first[UserTable.createdAt],
            updatedAt = first[UserTable.updatedAt],
            deletedAt = first[UserTable.deletedAt],
        )
    }
}
