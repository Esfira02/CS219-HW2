package am.esfira.cs219_hw2.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Extension property to get the DataStore instance
val Context.dataStore by preferencesDataStore(name = "settings")

// Defining the key for the String value in DataStore
private val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")

// Function to update the String value in DataStore
suspend fun Context.updateTemperatureUnit(newValue: String) {
    dataStore.edit { preferences ->
        preferences[TEMPERATURE_UNIT_KEY] = newValue
    }
}

// Flow to observe changes in the String value from DataStore
val Context.temperatureUnitFlow
    get() = dataStore.data
        .map { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] ?: "C" // Default value is "C"
        }
@Composable
fun SettingsView() {
    val context = LocalContext.current
    // Collect the current temperature unit as state in the composable
    val currentUnit by context.temperatureUnitFlow.collectAsState(initial = "C")

    // The switch is checked if the current unit is "F"
    val isFahrenheit = currentUnit == "F"

    Text(
        text = "Show temperatures in:",
        modifier = Modifier.padding(bottom = 16.dp)
    )
    // Switch for C/F temperature unit
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top) {
        Text(text = "Fahrenheit")
        Switch(
            checked = isFahrenheit,
            onCheckedChange = { isChecked ->
                // Update the temperature unit in DataStore when the switch is toggled
                CoroutineScope(Dispatchers.IO).launch {
                    context.updateTemperatureUnit(if (isChecked) "F" else "C")
                }
            }
        )
        Text(text = "Celsius")
    }
}
