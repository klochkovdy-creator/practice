package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.local.DepositCalculation

class DepositRepository(private val database: AppDatabase) {

    private val dao = database.depositDao()

    suspend fun saveCalculation(calculation: DepositCalculation) {
        dao.insert(calculation)
    }

    suspend fun getUserCalculations(userId: Long): List<DepositCalculation> {
        return dao.getUserCalculations(userId)
    }

    suspend fun deleteCalculation(calculation: DepositCalculation) {
        dao.delete(calculation)
    }

    suspend fun getCalculationById(id: Long, userId: Long): DepositCalculation? {
        return dao.getById(id, userId)
    }
}