package kz.kff.domain.usecase.file.query

import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.file.FileFilter
import kz.kff.domain.mapper.toFileRDTO

class PaginateFileUseCase
    (
    private val fileDatasource: FileDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(filter: FileFilter): PaginationMeta<FileRDTO> {
        return tx {
            fileDatasource.paginate(
                filter = filter,
                eachRow { it.toFileRDTO() }
            )
        }
    }

}