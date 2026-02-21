package kz.kff.domain.usecase.user.command

import io.ktor.http.content.MultiPartData
import jakarta.validation.Validator
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.exception_handlers.api.ApiValidationException
import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.core.shared.utils.file_utils.receiveMultipartWithDto
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.dto.role.RoleRDTO
import kz.kff.domain.dto.user.UserCDTO
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.mapper.toRoleRDTOList
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.user.UserFilter

class CreateUserUseCase(
    private val userDatasource: UserDatasource,
    private val roleDatasource: RoleDatasource,
    private val fileDatasource: FileDatasource,
    private val fileService: FileService,
    private val validator: Validator,
    private val jwtService: AppJwtService,
) : UseCaseTransaction() {

    suspend operator fun invoke(
        multipart: MultiPartData,
    ): UserRDTO {
        val (dto, fileDTO) = receiveMultipartWithDto(
            multipart = multipart,
            dtoClass = UserCDTO::class,
            fileService = fileService,
            filePartName = "file",
            skipIfNotValidate = false,
            folder = "user_avatars",
        )

        return tx(
            before = {
                validateDTO(validator,dto)
            }
        ) {
            validateFields(dto)

            if (fileDTO != null) {
                val savedFile = fileDatasource.create(
                    insertBlock = fileDTO.createEntity(FileTable),
                    mapper = { it.toFileRDTO() }
                )
                if (savedFile != null) {
                    dto.imageId = savedFile.id
                }
            }
            dto.passwordHash = jwtService.hashPassword(dto.passwordHash)
            userDatasource.create(
                insertBlock = dto.createEntity(UserTable),
                mapper = { it.toUserRDTO() }
            ) ?: throw ApiInternalException(messageKey = LocalizedMessageConstraints.ErrorInternalMessage)
        }
    }

    private suspend fun validateFields(dto: UserCDTO) {
        val existingRole: RoleRDTO? = roleDatasource.findByLongId(dto.roleId) { rows ->
            rows.toRoleRDTOList()
        }
        if (existingRole == null) {
            throw ApiNotFoundException(LocalizedMessageConstraints.RoleNotFoundMessage)
        }

        val existEmailCount = userDatasource.countWithFilter(
            filter = UserFilter(table = UserTable, email = dto.email)
        )
        if (existEmailCount > 0) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniqueEmailExistMessage,
                errors = mapOf("email" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniqueEmailExistMessage))
            )
        }

        val existUsernameCount = userDatasource.countWithFilter(
            filter = UserFilter(table = UserTable, username = dto.username)
        )
        if (existUsernameCount > 0) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniqueUsernameExistMessage,
                errors = mapOf("username" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniqueUsernameExistMessage))
            )
        }

        val existPhoneCount = userDatasource.countWithFilter(
            filter = UserFilter(table = UserTable, phone = dto.phone)
        )
        if (existPhoneCount > 0) {
            throw ApiValidationException(
                LocalizedMessageConstraints.ValidationUniquePhoneExistMessage,
                errors = mapOf("phone" to LocalizedMessage(LocalizedMessageConstraints.ValidationUniquePhoneExistMessage))
            )
        }
    }
}