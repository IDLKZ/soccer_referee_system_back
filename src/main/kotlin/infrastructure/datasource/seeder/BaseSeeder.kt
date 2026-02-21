package kz.kff.infrastructure.datasource.seeder

import kotlinx.coroutines.Dispatchers
import kz.kff.core.config.AppEnvironment
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

interface BaseSeeder<T: LongIdTable, CreateDTO> {

    suspend fun seed(env: AppEnvironment) {
        newSuspendedTransaction(Dispatchers.IO) {
            when (env) {
                AppEnvironment.DEV -> seedDevData()
                AppEnvironment.TEST -> seedStageData()
                AppEnvironment.PROD -> seedProdData()
            }
        }
    }
    suspend fun seedProdData()
    suspend fun seedDevData()
    suspend fun seedStageData()

    suspend fun getDevData():List<CreateDTO>
    suspend fun getProdData():List<CreateDTO>
    suspend fun getStageData():List<CreateDTO>

}