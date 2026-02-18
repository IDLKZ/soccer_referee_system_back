package kz.kff.domain.usecase.role.command

import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.usecase.shared.UseCaseTransaction

class BulkRestoreRoleUseCase (
    private val roleDatasource: RoleDatasource,
): UseCaseTransaction() {
    suspend operator fun invoke(ids: List<Long>): Int {
        if (ids.isEmpty()) {
            return 0
        }
        return tx {
            roleDatasource.bulkRestore(ids)
        }
    }
}