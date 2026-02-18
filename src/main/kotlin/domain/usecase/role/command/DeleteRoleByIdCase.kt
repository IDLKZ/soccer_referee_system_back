package kz.kff.domain.usecase.role.command

import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRoleSDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeleteRoleByIdCase (
    private val roleDatasource: RoleDatasource,
) : UseCaseTransaction()  {

    operator suspend fun invoke(id:Long, hardDelete:Boolean = false): Boolean = tx{
        val existing = roleDatasource.findByLongId(id = id, showDeleted = true, mapper = eachRow { it.toRoleSDTO() })
        if (existing == null){
            throw ApiBadRequestException(messageKey = LocalizedMessageConstraints.ErrorNotFoundMessage)
        }
        roleDatasource.delete(id = id, hardDelete = hardDelete)
    }
}