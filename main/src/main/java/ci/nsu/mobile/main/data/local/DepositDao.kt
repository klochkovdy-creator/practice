package ci.nsu.mobile.main.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DepositDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: DepositCalculation)

    @Query("SELECT * FROM deposit_calculations WHERE userId = :userId ORDER BY calculationDate DESC")
    suspend fun getUserCalculations(userId: Long): List<DepositCalculation>

    @Delete
    suspend fun delete(calculation: DepositCalculation)

    @Query("SELECT * FROM deposit_calculations WHERE id = :id AND userId = :userId")
    suspend fun getById(id: Long, userId: Long): DepositCalculation?
}