package dev.arogundade.wojak.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.arogundade.wojak.networking.clients.NomicsClient
import dev.arogundade.wojak.networking.observer.NetworkConnectivityObserver
import dev.arogundade.wojak.storage.WojakDatabase
import dev.arogundade.wojak.storage.WojakDatastore
import dev.arogundade.wojak.utils.Constants.API_KEY
import dev.arogundade.wojak.utils.Constants.BASE_URL
import dev.arogundade.wojak.utils.Constants.CONN_TIMEOUT
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.ocpsoft.prettytime.PrettyTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modules {

    @Provides
    @Singleton
    fun prettyTime(): PrettyTime {
        return PrettyTime()
    }

    @Provides
    @Singleton
    fun glide(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context)
    }

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): WojakDatabase {
        return Room.databaseBuilder(context, WojakDatabase::class.java, "Wojak")
            .build()
    }

    @Provides
    @Singleton
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            client(okHttpClient)
            baseUrl(BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val originalHttpUrl: HttpUrl = original.url

                val url: HttpUrl = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()

                val requestBuilder = original.newBuilder()
                    .url(url)

                val request = requestBuilder.build()
                chain.proceed(request)
            })
        }.build()
    }

    @Provides
    @Singleton
    fun nomicsClient(retrofit: Retrofit): NomicsClient {
        return retrofit.create(NomicsClient::class.java)
    }

    @Provides
    @Singleton
    fun observer(@ApplicationContext context: Context): NetworkConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }

    @Provides
    @Singleton
    fun dataStore(@ApplicationContext context: Context): WojakDatastore {
        return WojakDatastore(context)
    }

}