package com.example.tallycounter

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Count Repository responsible for persisting and loading the tally count.
 */
class CounterRepository(private val context: Context) {

    companion object {
        private val COUNT_KEY = intPreferencesKey("count")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "counter_prefs"
        )
    }

    /**
     * Observe the saved count as a [Flow]. Emits 0 if not exists.
     */
    val countFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[COUNT_KEY] ?: 0
    }

    /**
     * Save the given count.
     */
    suspend fun saveCount(value: Int) {
        context.dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[COUNT_KEY] = value
            }
        }
    }
}
