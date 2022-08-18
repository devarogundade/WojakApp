package dev.arogundade.wojak.storage.dao

import androidx.room.*
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.CurrencyAlerts
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Query("select * from currencies order by rank")
    fun allCurrencies(): Flow<List<Currency>>

    @Query("select * from currencies where id == :id limit 1")
    fun getCurrency(id: String): Flow<Currency?>

    @Transaction
    @Query("select * from currencies order by rank")
    fun allCurrencyAlerts(): Flow<List<CurrencyAlerts>>

    @Insert(entity = Currency::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(currency: Currency)

    @Update(entity = Currency::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(currency: Currency)

    @Delete(entity = Currency::class)
    suspend fun delete(currency: Currency)

}