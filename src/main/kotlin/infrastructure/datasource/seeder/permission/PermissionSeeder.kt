package kz.kff.infrastructure.datasource.seeder.permission

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.shared.constraints.DbValueConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.permission.PermissionCDTO
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.infrastructure.datasource.seeder.BaseSeeder

class PermissionSeeder
    (private val permissionDatasource: PermissionDatasource)
    : BaseSeeder<PermissionTable, PermissionCDTO> {
    override suspend fun seedProdData() {
        val count = permissionDatasource.count()
        val data = getProdData()
        if(count == 0L && data.isNotEmpty()) {
            permissionDatasource.bulkCreate(
                items = data,
                insertBlock = { dto -> applyMap(dto.bindMap(PermissionTable)) },
                mapper = { it.toPermissionRDTO() }
            )
        }
    }

    override suspend fun seedDevData() {
        val count = permissionDatasource.count()
        val data = getDevData()
        if(count == 0L && data.isNotEmpty()) {
            permissionDatasource.bulkCreate(
                items = data,
                insertBlock = { dto -> applyMap(dto.bindMap(PermissionTable)) },
                mapper = { it.toPermissionRDTO() }
            )
        }
    }

    override suspend fun seedStageData() {
        val count = permissionDatasource.count()
        val data = getStageData()
        if(count == 0L && data.isNotEmpty()) {
            permissionDatasource.bulkCreate(
                items = data,
                insertBlock = { dto -> applyMap(dto.bindMap(PermissionTable)) },
                mapper = { it.toPermissionRDTO() }
            )
        }
    }

    override suspend fun getDevData(): List<PermissionCDTO> {
        return getCommonDTO()
    }

    override suspend fun getProdData(): List<PermissionCDTO> {
        return getCommonDTO()
    }

    override suspend fun getStageData(): List<PermissionCDTO> {
        return getCommonDTO()
    }

    private fun getCommonDTO(): List<PermissionCDTO> {
        val permissions = listOf(
            // ===== ROLE =====
            PermissionCDTO(
                titleRu = "Создание роли",
                titleKk = "Рөл құру",
                titleEn = "Create Role",
                value = DbValueConstraints.ROLE_CREATE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Редактирование роли",
                titleKk = "Рөлді өңдеу",
                titleEn = "Edit Role",
                value = DbValueConstraints.ROLE_EDIT_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Удаление роли",
                titleKk = "Рөлді жою",
                titleEn = "Delete Role",
                value = DbValueConstraints.ROLE_DELETE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Просмотр ролей",
                titleKk = "Рөлдерді көру",
                titleEn = "View Roles",
                value = DbValueConstraints.ROLE_INDEX_PERMISSION_VALUE
            ),

            // ===== PERMISSION =====
            PermissionCDTO(
                titleRu = "Создание разрешения",
                titleKk = "Рұқсат құру",
                titleEn = "Create Permission",
                value = DbValueConstraints.PERMISSION_CREATE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Редактирование разрешения",
                titleKk = "Рұқсатты өңдеу",
                titleEn = "Edit Permission",
                value = DbValueConstraints.PERMISSION_EDIT_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Удаление разрешения",
                titleKk = "Рұқсатты жою",
                titleEn = "Delete Permission",
                value = DbValueConstraints.PERMISSION_DELETE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Просмотр разрешений",
                titleKk = "Рұқсаттарды көру",
                titleEn = "View Permissions",
                value = DbValueConstraints.PERMISSION_INDEX_PERMISSION_VALUE
            ),

            // ===== ROLE_PERMISSION =====
            PermissionCDTO(
                titleRu = "Назначение разрешений роли",
                titleKk = "Рөлге рұқсат беру",
                titleEn = "Assign Permission to Role",
                value = DbValueConstraints.ROLE_PERMISSION_CREATE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Редактирование разрешений роли",
                titleKk = "Рөл рұқсаттарын өңдеу",
                titleEn = "Edit Role Permissions",
                value = DbValueConstraints.ROLE_PERMISSION_EDIT_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Удаление разрешений роли",
                titleKk = "Рөл рұқсаттарын жою",
                titleEn = "Remove Role Permission",
                value = DbValueConstraints.ROLE_PERMISSION_DELETE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Просмотр разрешений роли",
                titleKk = "Рөл рұқсаттарын көру",
                titleEn = "View Role Permissions",
                value = DbValueConstraints.ROLE_PERMISSION_INDEX_PERMISSION_VALUE
            ),

            // ===== USER =====
            PermissionCDTO(
                titleRu = "Создание пользователя",
                titleKk = "Пайдаланушы құру",
                titleEn = "Create User",
                value = DbValueConstraints.USER_CREATE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Редактирование пользователя",
                titleKk = "Пайдаланушыны өңдеу",
                titleEn = "Edit User",
                value = DbValueConstraints.USER_EDIT_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Удаление пользователя",
                titleKk = "Пайдаланушыны жою",
                titleEn = "Delete User",
                value = DbValueConstraints.USER_DELETE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Просмотр пользователей",
                titleKk = "Пайдаланушыларды көру",
                titleEn = "View Users",
                value = DbValueConstraints.USER_INDEX_PERMISSION_VALUE
            ),

            // ===== FILE =====
            PermissionCDTO(
                titleRu = "Загрузка файла",
                titleKk = "Файл жүктеу",
                titleEn = "Upload File",
                value = DbValueConstraints.FILE_CREATE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Редактирование файла",
                titleKk = "Файлды өңдеу",
                titleEn = "Edit File",
                value = DbValueConstraints.FILE_EDIT_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Удаление файла",
                titleKk = "Файлды жою",
                titleEn = "Delete File",
                value = DbValueConstraints.FILE_DELETE_PERMISSION_VALUE
            ),
            PermissionCDTO(
                titleRu = "Просмотр файлов",
                titleKk = "Файлдарды көру",
                titleEn = "View Files",
                value = DbValueConstraints.FILE_INDEX_PERMISSION_VALUE
            )
        )
        return permissions
    }

}

