package dev.arogundade.wojak.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.arogundade.wojak.models.*
import dev.arogundade.wojak.storage.dao.*

@TypeConverters(TypeConverter::class)
@Database(
    entities = [Currency::class, Transaction::class, Portfolio::class, Metadata::class, Alert::class],
    version = 1,
    exportSchema = false
)
abstract class WojakDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
    abstract fun transactionDao(): TransactionDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun metadataDao(): MetadataDao
    abstract fun alertDao(): AlertDao

}