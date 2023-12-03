package am.esfira.cs219_hw2

import am.esfira.cs219_hw2.ui.NavHostScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavHostScreen(viewModel(), applicationContext)
        }
    }
}