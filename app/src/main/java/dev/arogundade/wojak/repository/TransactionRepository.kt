package dev.arogundade.wojak.repository

import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.storage.WojakDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*
import javax.inject.Inject

class TransactionRepository
@Inject
constructor(
    private val database: WojakDatabase
) {

    fun allTransaction(currencyId: String): Flow<List<Transaction>> {
        return database.transactionDao().allTransactions(currencyId)
    }

    suspend fun deleteTransaction(transaction: Transaction): Boolean {
        return try {
            database.transactionDao().delete(transaction)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addTransaction(transaction: Transaction): Boolean {
        return try {
            database.transactionDao().save(transaction)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Boolean {
        return try {
            database.transactionDao().update(transaction)
            true
        } catch (e: Exception) {
            false
        }
    }

    val filteredTransactions = MutableSharedFlow<ArrayList<List<Transaction>>>()

    suspend fun filterTransaction(
        interval: Int,
        currencyId: String?,
        n: (Int) -> Unit
    ) {
        val result = ArrayList<List<Transaction>>()

        try {
            val trx = if (currencyId != null) {
                database.transactionDao().allTransactions(currencyId)
            } else {
                database.transactionDao().allTransactions()
            }

            trx.collect { transactions ->
                when (interval) {
                    INTERVAL.HOUR -> {
                        val minutes = 60 downTo 1 step 5

                        for (fiveMinute in minutes) {
                            val start = Calendar.getInstance().apply {
                                add(Calendar.MINUTE, (-1 * fiveMinute))
                            }.time
                            val end = Calendar.getInstance().apply {
                                add(Calendar.MINUTE, ((-1 * fiveMinute) - minutes.step))
                            }.time

                            val set = transactions.filter {
                                it.date in start..end
                            }

                            result.add(set)
                        }
                    }
                    INTERVAL.DAY -> {
                        val hours = 24 downTo 1 step 1

                        for (hour in hours) {
                            val start = Calendar.getInstance().apply {
                                add(Calendar.HOUR_OF_DAY, (-1 * hour))
                            }.time
                            val end = Calendar.getInstance().apply {
                                add(Calendar.HOUR_OF_DAY, ((-1 * hour) + 1))
                            }.time

                            val set = transactions.filter {
                                it.date in start..end
                            }

                            result.add(set)
                        }
                    }
                    INTERVAL.WEEK -> {
                        val weeks = 7 downTo 1 step 1

                        for (day in weeks) {
                            val start = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_WEEK, (-1 * day))
                            }.time
                            val end = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_WEEK, ((-1 * day) + 1))
                            }.time

                            val set = transactions.filter {
                                it.date in start..end
                            }

                            result.add(set)
                        }
                    }
                    INTERVAL.MONTH -> {
                        val months = 30 downTo 1 step 1

                        for (day in months) {
                            val start = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_MONTH, (-1 * day))
                            }.time
                            val end = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_MONTH, ((-1 * day) + 1))
                            }.time

                            val set = transactions.filter {
                                it.date in start..end
                            }

                            result.add(set)
                        }
                    }
                    INTERVAL.YEAR -> {
                        val year = 12 downTo 1 step 1

                        for (month in year) {
                            val start = Calendar.getInstance().apply {
                                add(Calendar.MONTH, (-1 * month))
                            }.time
                            val end = Calendar.getInstance().apply {
                                add(Calendar.MONTH, ((-1 * month) + 1))
                            }.time

                            val set = transactions.filter {
                                it.date in start..end
                            }

                            result.add(set)
                        }
                    }
                }
                filteredTransactions.emit(result)
            }

        } catch (e: Exception) {
        }

        n(result.size)
    }

    object INTERVAL {
        const val HOUR = 1
        const val DAY = 2
        const val WEEK = 3
        const val MONTH = 4
        const val YEAR = 5
    }

}