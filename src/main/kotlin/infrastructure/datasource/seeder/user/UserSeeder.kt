package kz.kff.infrastructure.datasource.seeder.user

import kotlinx.datetime.LocalDate
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.shared.constraints.DbValueConstraints
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.user.UserCDTO
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter
import kz.kff.infrastructure.datasource.seeder.BaseSeeder

class UserSeeder
    (
    private val roleDatasource: RoleDatasource,
    private val userDatasource: UserDatasource,
    private val jwtService: AppJwtService
) : BaseSeeder<UserTable, UserCDTO> {
    override suspend fun seedProdData() {
        val count = userDatasource.count()
        val data = getProdData()
        if (count == 0L && data.isNotEmpty()) {
            userDatasource.bulkCreate(
                items = data,
                insertBlock = { dto-> applyMap(dto.bindMap(UserTable))},
                mapper = {it.toUserRDTO()}
            )
        }
    }

    override suspend fun seedDevData() {
        val count = userDatasource.count()
        val data = getDevData()
        if (count == 0L && data.isNotEmpty()) {
            userDatasource.bulkCreate(
                items = data,
                insertBlock = { dto-> applyMap(dto.bindMap(UserTable))},
                mapper = {it.toUserRDTO()}
            )
        }
    }

    override suspend fun seedStageData() {
        val count = userDatasource.count()
        val data = getStageData()
        if (count == 0L && data.isNotEmpty()) {
            userDatasource.bulkCreate(
                items = data,
                insertBlock = { dto-> applyMap(dto.bindMap(UserTable))},
                mapper = {it.toUserRDTO()}
            )
        }
    }

    override suspend fun getDevData(): List<UserCDTO> {
        return getCommonData()
    }

    override suspend fun getProdData(): List<UserCDTO> {
        return getCommonData()
    }

    override suspend fun getStageData(): List<UserCDTO> {
        return getCommonData()
    }

    suspend private fun getCommonData(): List<UserCDTO> {
        var admin = roleDatasource.findOneByFilter(
            filter = RoleFilter(RoleTable, value = DbValueConstraints.ADMIN_ROLE_VALUE),
            mapper = { it.toRoleRDTO() })
        var moderator = roleDatasource.findOneByFilter(
            filter = RoleFilter(
                RoleTable,
                value = DbValueConstraints.MODERATOR_ROLE_VALUE
            ), mapper = { it.toRoleRDTO() })
        var manager = roleDatasource.findOneByFilter(
            filter = RoleFilter(
                RoleTable,
                value = DbValueConstraints.MANAGER_ROLE_VALUE
            ), mapper = { it.toRoleRDTO() })
        val users = mutableListOf<UserCDTO>()

        if (admin != null) {
            users.addAll(
                listOf(
                    UserCDTO(
                        roleId = admin.id,
                        imageId = null,
                        username = "system_admin",
                        phone = "+77010000001",
                        email = "sysadmin@kff.kz",
                        firstName = "Системный",
                        lastName = "Администратор",
                        patronymic = null,
                        passwordHash = jwtService.hashPassword("admin123"),
                        birthDate = LocalDate.parse("1990-01-01"),
                        gender = 1,
                        isActive = true,
                        isVerified = true
                    ),

                    UserCDTO(
                        roleId = admin.id,
                        username = "admin",
                        phone = "+77010000002",
                        email = "admin@kff.kz",
                        firstName = "Главный",
                        lastName = "Админ",
                        patronymic = "Сергеевич",
                        passwordHash = jwtService.hashPassword("admin123"),
                        birthDate = LocalDate.parse("1992-05-12"),
                        gender = 1,
                        isActive = true,
                        isVerified = true
                    ),
                )
            )
        }
        if (moderator != null) {
            users.addAll(
                listOf(
                    UserCDTO(
                        roleId = moderator.id,
                        username = "moderator",
                        phone = "+77010000003",
                        email = "moderator@kff.kz",
                        firstName = "Айдос",
                        lastName = "Жумабеков",
                        patronymic = null,
                        passwordHash = jwtService.hashPassword("admin123"),
                        birthDate = LocalDate.parse("1995-03-20"),
                        gender = 1,
                        isActive = true,
                        isVerified = true
                    ),
                )
            )
        }
        if(manager != null){
            users.addAll(
                listOf(
                    UserCDTO(
                        roleId = manager.id,
                        username = "manager",
                        phone = "+77010000004",
                        email = "manager@kff.kz",
                        firstName = "Алия",
                        lastName = "Касымова",
                        patronymic = "Ерлановна",
                        passwordHash = jwtService.hashPassword("admin123"),
                        birthDate = LocalDate.parse("1994-07-11"),
                        gender = 2,
                        isActive = true,
                        isVerified = true
                    ),

                    UserCDTO(
                        roleId = manager.id,
                        username = "user_test",
                        phone = "+77010000005",
                        email = "user@kff.kz",
                        firstName = "Нурсултан",
                        lastName = "Абдрахманов",
                        patronymic = null,
                        passwordHash = jwtService.hashPassword("admin123"),
                        birthDate = LocalDate.parse("2000-09-09"),
                        gender = 1,
                        isActive = true,
                        isVerified = false
                    )
                )
            )
        }
        return users
    }
}