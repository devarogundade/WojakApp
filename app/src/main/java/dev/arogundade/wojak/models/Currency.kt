package dev.arogundade.wojak.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "currencies")
data class Currency(
    @SerializedName("1h")
    val oneHour: PriceDate?,
    @SerializedName("1d")
    val oneDay: PriceDate?,
    @SerializedName("30d")
    val oneMonth: PriceDate?,
    @SerializedName("365d")
    val oneYear: PriceDate?,
    @SerializedName("7d")
    val oneWeek: PriceDate?,
    val circulating_supply: String?,
    val currency: String,
    val first_candle: String,
    val first_order_book: String?,
    val first_trade: String?,
    val high: String?,
    val high_timestamp: String?,
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val logo_url: String?,
    val market_cap: String?,
    val market_cap_dominance: String?,
    val max_supply: String?,
    val name: String?,
    val num_exchanges: String?,
    val num_pairs: String?,
    val num_pairs_unmapped: String?,
    val price: String?,
    val price_date: String,
    val price_timestamp: String?,
    val rank: Int?,
    val rank_delta: String?,
    val status: String?,
    val symbol: String?,
    val ytd: PriceDate?,
) : Serializable

