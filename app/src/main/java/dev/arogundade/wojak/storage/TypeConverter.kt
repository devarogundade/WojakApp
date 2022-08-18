package dev.arogundade.wojak.storage

import androidx.room.TypeConverter
import com.google.gson.Gson
import dev.arogundade.wojak.models.PriceDate
import java.util.*

class TypeConverter {

    @TypeConverter
    fun toDate(time: Long): Date = Date(time)

    @TypeConverter
    fun fromDate(date: Date) = date.time

    @TypeConverter
    fun fromPriceDate(priceDate: PriceDate): String {
        return Gson().toJson(priceDate)
    }

    @TypeConverter
    fun toPriceDate(string: String): PriceDate {
        return Gson().fromJson(string, PriceDate::class.java)
    }

}