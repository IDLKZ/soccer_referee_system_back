package kz.kff.domain.usecase.permission.command

import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.usecase.shared.UseCaseTransaction

class BulkDeletePermissionUseCase(
    private val permissionDatasource: PermissionDatasource,
) : UseCaseTransaction() {
    suspend operator fun invoke(ids: List<Long>): Int {
        if (ids.isEmpty()) {
            return 0
        }
        return tx {
            permissionDatasource.bulkDelete(ids, hardDelete = true)
        }
    }
}
