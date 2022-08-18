package dev.arogundade.wojak.storage.dao

import androidx.room.*
import dev.arogundade.wojak.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("select * from transactions order by date desc")
    fun allTransactions(): Flow<List<Transaction>>

    @Query("select * from transactions where currencyId == :currencyId order by date desc")
    fun allTransactions(currencyId: String): Flow<List<Transaction>>

    @Query("select * from transactions where id == :id limit 1")
    fun getTransaction(id: Int): Flow<Transaction?>

    @Insert(entity = Transaction::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(transaction: Transaction)

    @Update(entity = Transaction::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(transaction: Transaction)

    @Delete(entity = Transaction::class)
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions where currencyId == :currencyId")
    suspend fun deleteAll(currencyId: String)

}