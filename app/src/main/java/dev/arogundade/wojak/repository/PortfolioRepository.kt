package dev.arogundade.wojak.repository

import dev.arogundade.wojak.models.Portfolio
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import dev.arogundade.wojak.storage.WojakDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PortfolioRepository @Inject
constructor(
    private val database: WojakDatabase
) {

    suspend fun addToPortfolio(portfolio: Portfolio) {
        try {
            database.portfolioDao().save(portfolio)
        } catch (e: Exception) {
            // already exist
        }
    }

    fun getPortfolioTransaction(id: String): Flow<PortfolioCurrencyTransactions?> {
        return database.portfolioDao().getPortfolioCurrencyTransactions(id)
    }

    suspend fun updatePortfolio(portfolio: Portfolio) {
        try {
            database.portfolioDao().update(portfolio)
        } catch (e: Exception) {
        }
    }

    suspend fun removeFromPortfolio(portfolio: PortfolioCurrencyTransactions) {
        try {
            database.portfolioDao().delete(portfolio.portfolio)
            database.transactionDao().deleteAll(portfolio.currency.id)
        } catch (e: Exception) {
        }
    }

    fun allPortfolioCurrencyTransactions(): Flow<List<PortfolioCurrencyTransactions>> {
        return database.portfolioDao().allPortfolioCurrencyTransactions()
    }

    fun allPortfolioCurrencyTransactionsWithGoal(): Flow<List<PortfolioCurrencyTransactions>> {
        return database.portfolioDao().allPortfolioCurrencyTransactionsWithGoal()
    }

}