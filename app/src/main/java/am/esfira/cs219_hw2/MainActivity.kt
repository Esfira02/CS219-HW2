package am.esfira.cs219_hw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import am.esfira.cs219_hw2.ui.theme.CS219_HW2Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavHostScreen()
        }
    }
}

val descriptions = mapOf("Yerevan" to "Yerevan is the capital city of Armenia. It is one of the oldest cities in the world, having a beautiful heritage.",
    "Bangkok" to "Bangkok, Thailand’s capital, is a large city known for ornate shrines and vibrant street life.",
    "Rome" to "Rome is the capital city of Italy. It is also the capital of the Lazio region, the centre of the Metropolitan City of Rome, and a special comune named Comune di Roma Capitale.")


@Composable
fun CityView(
    cityName: String,
    cityDesc: String,
    cityImage: Int
){
    Column(modifier = Modifier.padding(horizontal = 10.dp)){
        Text(
            text = cityName,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = cityDesc
        )
        Spacer(modifier = Modifier.height(15.dp))
        Image(
            painter = painterResource(id = cityImage),
            contentDescription = cityName,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun NavHostScreen() {
    CS219_HW2Theme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "WelcomeScreen") {
                composable(
                    route = "WelcomeScreen",
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to Our App",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = { navController.navigate(route = "ListScreen") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier
                                .width(256.dp)
                                .height(48.dp)

                        ) {
                            Text(
                                text = "Next",
                                color = Color.White
                            )
                        }
                    }
                }

                composable(
                    route = "ListScreen",
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
//                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "List of cities",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        CityView(cityName = "Yerevan", cityDesc = descriptions["Yerevan"].toString(), cityImage = R.drawable.yerevan )
                        CityView(cityName = "Rome", cityDesc = descriptions["Rome"].toString(), cityImage = R.drawable.rome )
                        CityView(cityName = "Bangkok", cityDesc = descriptions["Bangkok"].toString(), cityImage = R.drawable.bangkok )

                    }
                }
            }
        }
    }
}