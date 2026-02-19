package kz.kff.domain.usecase.user.command

import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.usecase.shared.UseCaseTransaction

class BulkDeleteUserByIdsUseCase(
    private val userDatasource: UserDatasource,
) : UseCaseTransaction() {

    suspend operator fun invoke(ids: List<Long>, hardDelete: Boolean = false): Int {
        if (ids.isEmpty()) return 0
        return tx {
            userDatasource.bulkDelete(ids, hardDelete)
        }
    }
}
