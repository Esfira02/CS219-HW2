package am.esfira.cs219_hw2.ui

import am.esfira.cs219_hw2.model.CurrentResponse
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CityView(
    cityName: String,
    cityDesc: String,
    cityImage: Int,
    weatherResponse: CurrentResponse
){
    Column(modifier = Modifier
        .padding(horizontal = 10.dp)
        .fillMaxWidth()){
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
        Text(
            text = weatherResponse.current.tempC.toString()
                    + "°C - "
                    + weatherResponse.current.condition
        )
    }
}