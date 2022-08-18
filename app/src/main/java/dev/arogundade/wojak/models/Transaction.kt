package dev.arogundade.wojak.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val currencyId: String,
    val price: Double,
    val amount: Double,
    val date: Date,
    val type: String,
    val message: String?
) : Serializable