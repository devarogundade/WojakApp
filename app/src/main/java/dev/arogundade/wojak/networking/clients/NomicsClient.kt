package dev.arogundade.wojak.networking.clients

import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.Metadata
import dev.arogundade.wojak.utils.Constants.INTERVAL
import dev.arogundade.wojak.utils.Constants.NETWORK_PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NomicsClient {

    @GET("currencies/ticker")
    suspend fun currencies(
        @Query("ids") ids: String = "",
        @Query("covert") convert: String = "USD",
        @Query("interval") interval: String = INTERVAL,
        @Query("per-page") perPage: Int = NETWORK_PAGE_SIZE,
        @Query("page") page: Int,
        @Query("status") status: String = "active"
    ): Response<List<Currency>>

    @GET("currencies")
    suspend fun metadata(
        @Query("ids") ids: String
    ): Response<List<Metadata>>

}