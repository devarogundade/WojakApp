package dev.arogundade.wojak.storage.dao

import androidx.room.*
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.models.AlertCurrency
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Transaction
    @Query("select * from alerts order by id desc")
    fun allAlertAlertCurrencies(): Flow<List<AlertCurrency>>

    @Query("select * from alerts order by id desc")
    suspend fun allAlerts(): List<Alert>

    @Transaction
    @Query("select * from alerts where id == :id limit 1")
    fun getAlertCurrency(id: String): Flow<AlertCurrency?>

    @Insert(entity = Alert::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(alert: Alert)

    @Update(entity = Alert::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(alert: Alert)

    @Delete(entity = Alert::class)
    suspend fun delete(alert: Alert)

}