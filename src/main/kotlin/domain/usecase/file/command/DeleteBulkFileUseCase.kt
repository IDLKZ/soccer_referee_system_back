package kz.kff.domain.usecase.file.command

import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.mapper.toFileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeleteBulkFileUseCase(
    private val fileService: FileService,
    private val fileDatasource: FileDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(ids: List<Long>, deleteOldFile: Boolean): Int {
        return tx {
            if (deleteOldFile) {
                ids.forEach { id ->
                    val file = fileDatasource.findByLongId(id = id, mapper = eachRow { it.toFileRDTO() })
                    file?.let { fileService.delete(it.uniqueFileName) }
                }
            }
            fileDatasource.bulkDelete(ids)
        }
    }
}
