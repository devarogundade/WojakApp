package dev.arogundade.wojak.models

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class PortfolioCurrencyTransactions(
    @Embedded val portfolio: Portfolio,
    @Relation(parentColumn = "id", entityColumn = "id")
    val currency: Currency,
    @Relation(parentColumn = "id", entityColumn = "currencyId")
    val transactions: List<Transaction>
) : Serializable