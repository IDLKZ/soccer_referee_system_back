package kz.kff.infrastructure.datasource.seeder.role_permission

import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.core.shared.constraints.DbValueConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.role_permission.RolePermissionCDTO
import kz.kff.domain.mapper.toPermissionRDTOList
import kz.kff.domain.mapper.toRolePermissionRDTO
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter
import kz.kff.infrastructure.datasource.seeder.BaseSeeder

class AdminRolePermissionSeeder(
    private val roleDatasource: RoleDatasource,
    private val permissionDatasource: PermissionDatasource,
    private val rolePermissionDatasource: RolePermissionDatasource,
) : BaseSeeder<RolePermissionTable, RolePermissionCDTO> {



    override suspend fun seedProdData() {
        val data = getProdData()
        val result = rolePermissionDatasource.count()
        if(!data.isNullOrEmpty() && result == 0L){
            rolePermissionDatasource.bulkCreate(
                items = data,
                insertBlock = { dto -> applyMap(dto.bindMap(RolePermissionTable)) },
                mapper = {it.toRolePermissionRDTO()}
            )
        }
    }

    override suspend fun seedDevData() {
        val data = getDevData()
        val result = rolePermissionDatasource.count()
        if(!data.isNullOrEmpty() && result == 0L){
            rolePermissionDatasource.bulkCreate(
                items = data,
                insertBlock = { dto -> applyMap(dto.bindMap(RolePermissionTable)) },
                mapper = {it.toRolePermissionRDTO()}
            )
        }
    }

    override suspend fun seedStageData() {
        val data = getStageData()
        val result = rolePermissionDatasource.count()
        if(!data.isNullOrEmpty() && result == 0L){
            rolePermissionDatasource.bulkCreate(
                items = data,
                insertBlock = { dto -> applyMap(dto.bindMap(RolePermissionTable)) },
                mapper = {it.toRolePermissionRDTO()}
            )
        }
    }

    override suspend fun getDevData(): List<RolePermissionCDTO> {
        var data = listOf<RolePermissionCDTO>()
        val filter = RoleFilter(table = RoleTable, value = DbValueConstraints.ADMIN_ROLE_VALUE)
        val adminRole = roleDatasource.findOneByFilter(filter, mapper = {it.toRoleRDTO()})
        val permissions = permissionDatasource.all(mapper = {it.toPermissionRDTOList()})
        if(adminRole != null && !permissions.isNullOrEmpty()) {
            data = permissions.map {it->
                RolePermissionCDTO(
                    roleId = adminRole.id,
                    permissionId = it.id,
                )
            }
        }
        return data
    }

    override suspend fun getProdData(): List<RolePermissionCDTO> {
        var data = listOf<RolePermissionCDTO>()
        val filter = RoleFilter(table = RoleTable, value = DbValueConstraints.ADMIN_ROLE_VALUE)
        val adminRole = roleDatasource.findOneByFilter(filter, mapper = {it.toRoleRDTO()})
        val permissions = permissionDatasource.all(mapper = {it.toPermissionRDTOList()})
        if(adminRole != null && !permissions.isNullOrEmpty()) {
            data = permissions.map {it->
                RolePermissionCDTO(
                    roleId = adminRole.id,
                    permissionId = it.id,
                )
            }
        }
        return data
    }

    override suspend fun getStageData(): List<RolePermissionCDTO> {
        var data = listOf<RolePermissionCDTO>()
        val filter = RoleFilter(table = RoleTable, value = DbValueConstraints.ADMIN_ROLE_VALUE)
        val adminRole = roleDatasource.findOneByFilter(filter, mapper = {it.toRoleRDTO()})
        val permissions = permissionDatasource.all(mapper = {it.toPermissionRDTOList()})
        if(adminRole != null && !permissions.isNullOrEmpty()) {
            data = permissions.map {it->
                RolePermissionCDTO(
                    roleId = adminRole.id,
                    permissionId = it.id,
                )
            }
        }
        return data
    }
}