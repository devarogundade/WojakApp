package dev.arogundade.wojak.models

import androidx.room.Embedded
import androidx.room.Relation

data class CurrencyAlerts(
    @Embedded val currency: Currency,
    @Relation(parentColumn = "id", entityColumn = "currencyId")
    val alerts: List<Alert>
)