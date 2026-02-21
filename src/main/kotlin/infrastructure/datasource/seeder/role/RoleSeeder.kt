package kz.kff.infrastructure.datasource.seeder.role

import kz.kff.core.db.table.role.RoleTable
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.role.RoleCDTO
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.infrastructure.datasource.seeder.BaseSeeder

class RoleSeeder(
    private val roleDatasource: RoleDatasource
) : BaseSeeder<RoleTable, RoleCDTO> {
    override suspend fun seedProdData() {
        if (roleDatasource.count() == 0L) {
            val data: List<RoleCDTO> = getProdData()
            if (data.isNotEmpty()) {
                roleDatasource.bulkCreate(
                    items = data,
                    insertBlock = { dto -> applyMap(dto.bindMap(RoleTable)) },
                    mapper = { it.toRoleRDTO() }
                )
            }
        }
    }

    override suspend fun seedDevData() {
        if (roleDatasource.count() == 0L) {
            val data: List<RoleCDTO> = getDevData()
            if (data.isNotEmpty()) {
                roleDatasource.bulkCreate(
                    items = data,
                    insertBlock = { dto -> applyMap(dto.bindMap(RoleTable)) },
                    mapper = { it.toRoleRDTO() }
                )
            }
        }
    }

    override suspend fun seedStageData() {
        if (roleDatasource.count() == 0L) {
            val data: List<RoleCDTO> = getStageData()
            if (data.isNotEmpty()) {
                roleDatasource.bulkCreate(
                    items = data,
                    insertBlock = { dto -> applyMap(dto.bindMap(RoleTable)) },
                    mapper = { it.toRoleRDTO() }
                )
            }
        }
    }


    override suspend fun getDevData(): List<RoleCDTO> {
        return listOf(
            RoleCDTO(
                titleRu = "Администратор",
                titleKk = "Әкімші",
                titleEn = "Administrator",
                value = "ADMIN",
                isSystem = false,
                isAdministrative = true
            ),

            RoleCDTO(
                titleRu = "Модератор",
                titleKk = "Модератор",
                titleEn = "Moderator",
                value = "MODERATOR",
                isSystem = false,
                isAdministrative = true
            ),

            RoleCDTO(
                titleRu = "Менеджер",
                titleKk = "Менеджер",
                titleEn = "Manager",
                value = "MANAGER",
                isSystem = false,
                isAdministrative = false
            ),
        )
    }

    override suspend fun getProdData(): List<RoleCDTO> {
        return listOf(
            RoleCDTO(
                titleRu = "Администратор",
                titleKk = "Әкімші",
                titleEn = "Administrator",
                value = "ADMIN",
                isSystem = false,
                isAdministrative = true
            ),

            RoleCDTO(
                titleRu = "Модератор",
                titleKk = "Модератор",
                titleEn = "Moderator",
                value = "MODERATOR",
                isSystem = false,
                isAdministrative = true
            ),

            RoleCDTO(
                titleRu = "Менеджер",
                titleKk = "Менеджер",
                titleEn = "Manager",
                value = "MANAGER",
                isSystem = false,
                isAdministrative = false
            ),
        )
    }

    override suspend fun getStageData(): List<RoleCDTO> {
        return listOf(
            RoleCDTO(
                titleRu = "Администратор",
                titleKk = "Әкімші",
                titleEn = "Administrator",
                value = "ADMIN",
                isSystem = false,
                isAdministrative = true
            ),

            RoleCDTO(
                titleRu = "Модератор",
                titleKk = "Модератор",
                titleEn = "Moderator",
                value = "MODERATOR",
                isSystem = false,
                isAdministrative = true
            ),

            RoleCDTO(
                titleRu = "Менеджер",
                titleKk = "Менеджер",
                titleEn = "Manager",
                value = "MANAGER",
                isSystem = false,
                isAdministrative = false
            ),
        )
    }
}