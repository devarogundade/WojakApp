package dev.arogundade.wojak.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.arogundade.wojak.models.WojakPreference
import dev.arogundade.wojak.utils.Constants
import dev.arogundade.wojak.utils.Constants.WOJAK_PREFERENCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class WojakDatastore(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(WOJAK_PREFERENCE)

    suspend fun setFirstTime() {
        if (isFirstTime()) {
            context.dataStore.edit {
                it[FIRST_TIME] = Calendar.getInstance().time.time
            }
        }
    }

    private suspend fun isFirstTime(): Boolean {
        return context.dataStore.data.first()[FIRST_TIME] == null
    }

    fun observe(): Flow<WojakPreference> =
        context.dataStore.data.map {
            WojakPreference(
                firstTime = it[FIRST_TIME]
            )
        }

    companion object {
        val FIRST_TIME = longPreferencesKey(Constants.FIRST_TIME)
    }

}
