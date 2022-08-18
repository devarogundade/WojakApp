package dev.arogundade.wojak.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val currencyId: String,
    val target: Double,
    val active: Boolean,
    val watchType: Int,
    val note: String?,
    val frequency: Int = 1,
    val date: Date,
)