package dev.arogundade.wojak.storage.dao

import androidx.room.*
import dev.arogundade.wojak.models.Portfolio
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {

    @Transaction
    @Query("select * from portfolio order by date desc")
    fun allPortfolioCurrencyTransactions(): Flow<List<PortfolioCurrencyTransactions>>

    @Transaction
    @Query("select * from portfolio  WHERE NULLIF(goalTitle, '') IS NOT NULL order by date desc")
    fun allPortfolioCurrencyTransactionsWithGoal(): Flow<List<PortfolioCurrencyTransactions>>

    @Transaction
    @Query("select * from portfolio where id == :id")
    fun getPortfolioCurrencyTransactions(id: String): Flow<PortfolioCurrencyTransactions>

    @Insert(entity = Portfolio::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun save(portfolio: Portfolio)

    @Update(entity = Portfolio::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(portfolio: Portfolio)

    @Delete(entity = Portfolio::class)
    suspend fun delete(portfolio: Portfolio)

}