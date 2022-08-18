package dev.arogundade.wojak.models

import androidx.room.Embedded
import androidx.room.Relation

data class AlertCurrency(
    @Embedded val alert: Alert,
    @Relation(parentColumn = "currencyId", entityColumn = "id")
    val currency: Currency,
)