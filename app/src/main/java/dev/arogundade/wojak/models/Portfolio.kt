package dev.arogundade.wojak.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "portfolio")
data class Portfolio(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val goalTitle: String?,
    val goalAmount: Double?,
    val date: Date,
) : Serializable