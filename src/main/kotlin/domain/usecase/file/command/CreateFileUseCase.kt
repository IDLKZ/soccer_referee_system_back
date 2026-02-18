package kz.kff.domain.usecase.file.command

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.dto.file.FileCDTO
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class CreateFileUseCase(
    private val fileDatasource: FileDatasource,
    private val fileService: FileService,
) : UseCaseTransaction() {

    suspend operator fun invoke(skipIfNotValidate: Boolean, folder: String?, multipart: MultiPartData): FileRDTO? {
        var fileDTO: FileCDTO? = null

        return tx {
            if (fileDTO == null) {
                throw ApiBadRequestException(LocalizedMessageConstraints.ErrorFileUpload)
            }
            fileDatasource.create(
                insertBlock = fileDTO.createEntity(FileTable),
                mapper = { it.toFileRDTO() }
            )
        }
    }

}