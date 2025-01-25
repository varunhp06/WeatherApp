package com.example.weatherapptest.ui.theme

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapptest.LocationUtils
import com.example.weatherapptest.LocationViewModel
import com.example.weatherapptest.R
import com.example.weatherapptest.getAQI
import com.example.weatherapptest.getCityName
import com.example.weatherapptest.getCurrentDateAndTime
import com.example.weatherapptest.getCurrentDetails
import com.example.weatherapptest.getHourlyDetails
import com.example.weatherapptest.getTimeZone
import com.example.weatherapptest.getWeeklyDetails
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainView(viewModel: LocationViewModel){
    val latitude = remember { mutableDoubleStateOf(0.0) }
    val longitude = remember { mutableDoubleStateOf(0.0) }
    GetLocation(viewModel = viewModel)
    val location = viewModel.location.value
    if (location?.latitude != null) {
        latitude.doubleValue = location.latitude
        longitude.doubleValue = location.longitude
    }
    val color = remember { mutableStateOf(Color(0XFFFFFFFF)) }
    val city = remember { mutableStateOf("...") }
    city.value = getCityName(lat = latitude.doubleValue, lon = longitude.doubleValue).toString()
    val temp = remember { mutableStateOf("...") }
    val desc = remember { mutableStateOf("...") }
    val humidity = remember { mutableStateOf("...") }
    val apparentTemp = remember{ mutableStateOf("...") }
    val dayAndDate = remember{ mutableStateOf("...") }
    val aqi = remember { mutableStateOf("...") }
    val weatherCode = remember { mutableIntStateOf(0) }
    val isDay = remember { mutableIntStateOf(0) }
    val icon = remember { mutableIntStateOf(R.drawable.ic_launcher_foreground) }
    val hourlyTempList: MutableState<List<Double>> = remember { mutableStateOf(emptyList()) }
    val hourlyWeatherList: MutableState<List<Int>> = remember { mutableStateOf(emptyList()) }
    val weeklyMaxTempList: MutableState<List<Double>> = remember { mutableStateOf(emptyList()) }
    val weeklyMinTempList: MutableState<List<Double>> = remember { mutableStateOf(emptyList()) }
    val weeklyWeatherList: MutableState<List<Int>> = remember { mutableStateOf(emptyList()) }
    val weeklyTimeList: MutableState<List<String>> = remember { mutableStateOf(emptyList()) }
    val dayMap = mapOf("Monday" to "MON", "Tuesday" to "TUE", "Wednesday" to "WED", "Thursday" to "THU", "Friday" to " FRI", "Saturday" to "SAT", "Sunday" to "SUN")
    val isTempLocationFetched = remember { mutableStateOf(false) }
    val isTimeZoneFetched = remember { mutableStateOf(false) }
    val isAQIFetched = remember { mutableStateOf(false) }
    val isHourlyFetched = remember { mutableStateOf(false) }
    val isWeeklyFetched = remember { mutableStateOf(false) }

    LaunchedEffect(latitude.doubleValue, longitude.doubleValue) {
        if (latitude.doubleValue != 0.0 && longitude.doubleValue != 0.0 && !isTempLocationFetched.value) {
            val tempLocation = getCurrentDetails(latitude.doubleValue, longitude.doubleValue)
            tempLocation?.let {
                temp.value = it.current.temperature_2m.roundToInt().toString() + "°"
                apparentTemp.value = it.current.apparent_temperature.roundToInt().toString() + "°"
                humidity.value = it.current.relative_humidity_2m.toString() + "%"
                isDay.intValue = it.current.is_day
                weatherCode.intValue = it.current.weather_code
                isTempLocationFetched.value = true
            }
        }
    }
    LaunchedEffect(latitude.doubleValue, longitude.doubleValue) {
        if (latitude.doubleValue != 0.0 && longitude.doubleValue != 0.0 && !isTimeZoneFetched.value) {
            val timeZone = getTimeZone(latitude.doubleValue, longitude.doubleValue)
            timeZone?.let {
                dayAndDate.value = getCurrentDateAndTime(timeZone).uppercase(Locale.getDefault())
                isTimeZoneFetched.value = true
            }
        }
    }
    LaunchedEffect(latitude.doubleValue, longitude.doubleValue) {
        if (latitude.doubleValue != 0.0 && longitude.doubleValue != 0.0 && !isAQIFetched.value) {
            val currentAQI = getAQI(latitude.doubleValue, longitude.doubleValue)
            currentAQI?.let {
                aqi.value = currentAQI.current.us_aqi.toString()
                isAQIFetched.value = true
            }
        }
    }
    LaunchedEffect(latitude.doubleValue, longitude.doubleValue) {
        if (latitude.doubleValue != 0.0 && longitude.doubleValue != 0.0 && !isHourlyFetched.value) {
            val hourlyDetails = getHourlyDetails(latitude.doubleValue, longitude.doubleValue)
            hourlyDetails?.let {
                hourlyTempList.value = hourlyDetails.temperature_2m
                hourlyWeatherList.value = hourlyDetails.weather_code
                isHourlyFetched.value = true
            }
        }
    }
    LaunchedEffect(latitude.doubleValue, longitude.doubleValue) {
        if (latitude.doubleValue != 0.0 && longitude.doubleValue != 0.0 && !isWeeklyFetched.value) {
            val weeklyDetails = getWeeklyDetails(latitude.doubleValue, longitude.doubleValue)
            weeklyDetails?.let {
                weeklyMaxTempList.value = weeklyDetails.temperature_2m_max
                weeklyMinTempList.value = weeklyDetails.temperature_2m_min
                weeklyWeatherList.value = weeklyDetails.weather_code
                weeklyTimeList.value = weeklyDetails.time
                isWeeklyFetched.value = true
            }
        }
    }
    desc.value = getWeatherDescription(weatherCode.intValue).uppercase(Locale.ROOT)
    icon.intValue = getWeatherIcon(desc.value, isDay.intValue)
    color.value = getWeatherColor(desc.value, isDay.intValue)

    var colorBack = Color.Black
    var colorFront = Color.White
    if (isDay.intValue == 1){
        colorBack = Color.White
        colorFront = Color.Black
    }

    Card(
        colors = CardColors(
            colorBack,
            colorFront,
            colorBack,
            colorFront
        )
    ){
        Column (modifier = Modifier
            .padding(top = 40.dp)
            .verticalScroll(rememberScrollState())){
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = dayAndDate.value,
                    modifier = Modifier.padding(
                        start = 15.dp
                    ),
                    fontSize = 75.sp,
                    fontFamily = FontFamily(
                        Font(
                            R.font.dongle
                        )
                    )
                )
            }
            Row (modifier = Modifier
                .fillMaxWidth()){
                Image(painter = painterResource(id = icon.intValue), contentDescription = "Current Weather Icon", modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Crop)
            }
            Row (modifier = Modifier.fillMaxWidth()
                .height(180.dp), horizontalArrangement = Arrangement.SpaceBetween){
                var description = desc.value
                if (desc.value == "THUNDERSTORM") description = "T-STORM"
                Text(text = temp.value, color = color.value, modifier = Modifier.padding(start = 20.dp), fontSize = 170.sp, fontFamily = FontFamily(Font(R.font.dongle)),style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f
                    )))
                Text(text = description, color = color.value, modifier = Modifier.padding(top = 85.dp, end = 20.dp), fontSize = 70.sp, fontFamily = FontFamily(Font(R.font.dongle)), style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f
                    )))
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .padding(start = 25.dp, end = 25.dp), color = colorFront
                )
            }

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
                .horizontalScroll(rememberScrollState())
            ){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = city.value.uppercase(Locale.ROOT),
                    style = TextStyle(fontSize = 90.sp, fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(R.font.dongle)),
                    maxLines = 2
                )
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .padding(start = 25.dp, end = 25.dp), color = colorFront
                )
            }
            Row(modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()) {
                Spacer(modifier = Modifier.width(8.dp))
                var isDayWeek = 1
                if (isDay.intValue == 1)  isDayWeek = 0
                hourlyTempList.value.take(24).forEachIndexed { index, temp ->
                    WeatherCard(
                        time = "${index.toString().padStart(2, '0')}:00",
                        temp = "${temp.roundToInt()}°",
                        color = getWeatherColor(getWeatherDescription(hourlyWeatherList.value[index]), isDay.intValue),
                        weatherIcon = getWeatherIconWeeklyHourly(
                            getWeatherDescription(hourlyWeatherList.value[index]),
                            if (index in 0..6 || index in 19..23) 0 else 1,
                            dayWeek = isDayWeek),
                        isDay = isDay.intValue
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .padding(start = 25.dp, end = 25.dp), color = colorFront
                )
            }
            Column{
                InfoCard(category = "Feels Like", value = apparentTemp.value, color = color.value, isDay = isDay.intValue)
                Spacer(Modifier.padding(5.dp))
                InfoCard(category = "Humidity", value = humidity.value, color = color.value, isDay = isDay.intValue)
                Spacer(Modifier.padding(5.dp))
                InfoCard(category = "AQI", value = aqi.value, color = color.value, isDay = isDay.intValue)
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Row (modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .padding(start = 25.dp, end = 25.dp), color = colorFront
                )
            }
            Row (modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()){
                Spacer(modifier = Modifier.width(10.dp))
                var isDayWeek = 1
                if (isDay.intValue == 1)  isDayWeek = 0
                weeklyTimeList.value.forEachIndexed{ index, time ->
                    WeeklyWeatherCard(
                        time = dayMap[LocalDate.parse(time).dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale(Locale.getDefault().language))].toString(),
                        temp = "${((weeklyMaxTempList.value[index]+weeklyMinTempList.value[index])/2).roundToInt()}°",
                        color = getWeatherColor(getWeatherDescription(weeklyWeatherList.value[index]), isDay.intValue),
                        weatherIcon = getWeatherIconWeeklyHourly(getWeatherDescription(weeklyWeatherList.value[index]), isDay.intValue, isDayWeek),
                        isDay = isDay.intValue
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .padding(start = 25.dp, end = 25.dp), color = colorFront
                )
            }
        }
    }
}

@Composable
fun InfoCard(category: String, value: String, color: Color, isDay: Int){
    var colorBack = Color.Black
    var colorFront = Color.White
    if (isDay == 1){
        colorBack = Color.White
        colorFront = Color.Black
    }
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 25.dp, end = 25.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardColors(colorFront,
            colorBack,
            colorFront,
            colorBack),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = category, fontSize = 50.sp, modifier = Modifier.padding(start = 25.dp), color = colorBack, fontFamily = FontFamily(Font(R.font.dongle)))
            Text(text = value, style = TextStyle(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 2f
                )
            ), fontSize = 50.sp, modifier = Modifier.padding(end = 25.dp), color = color, fontFamily = FontFamily(Font(R.font.dongle)))
        }
    }
}

fun getWeatherDescription(weatherCode: Int): String{
    if (weatherCode in 0..19) return "CLEAR"
    if (weatherCode in 95..99) return "THUNDERSTORM"
    if (weatherCode in 76..77 || weatherCode == 85 || weatherCode in 90..95) return "SNOW"
    if (weatherCode in 50..55 || weatherCode in 60..65 || weatherCode in 70..75) return "RAIN"
    if (weatherCode in 56..57 || weatherCode in 66..67) return "ICE"
    return if (weatherCode in 80..84 || weatherCode in 86..89) "CLOUDY"
    else ""
}

fun getWeatherIcon(weatherDescription: String, isDay: Int): Int {
    if (weatherDescription == "CLEAR" && isDay == 1) return R.drawable.sun
    if (weatherDescription == "CLEAR" && isDay == 0) return R.drawable.moon
    if (weatherDescription == "CLOUDY" && isDay == 1) return R.drawable.sun_cloud
    if (weatherDescription == "CLOUDY" && isDay == 0) return R.drawable.moon_cloud
    if (weatherDescription == "THUNDERSTORM" && isDay == 1) return R.drawable.thunderstorm_black
    if (weatherDescription == "THUNDERSTORM" && isDay == 0) return R.drawable.thunderstorm
    if (weatherDescription == "SNOW" && isDay == 1) return R.drawable.snow_black
    if (weatherDescription == "SNOW" && isDay == 0) return R.drawable.snow
    if (weatherDescription == "RAIN" && isDay == 1) return R.drawable.rain_black
    if (weatherDescription == "RAIN" && isDay == 0) return R.drawable.rain
    if (weatherDescription == "ICE" && isDay == 1) return R.drawable.ice_black
    return if (weatherDescription == "ICE" && isDay == 0) R.drawable.ice
    else R.drawable.ic_launcher_foreground
}

fun getWeatherIconWeeklyHourly(weatherDescription: String, isDay: Int, dayWeek: Int): Int {
    if (weatherDescription == "CLEAR" && isDay == 1 && dayWeek == 0) return R.drawable.sun
    if (weatherDescription == "CLEAR" && isDay == 1 && dayWeek == 1) return R.drawable.sun_white
    if (weatherDescription == "CLEAR" && isDay == 0 && dayWeek == 0) return R.drawable.moon_black
    if (weatherDescription == "CLEAR" && isDay == 0 && dayWeek == 1) return R.drawable.moon
    if (weatherDescription == "CLOUDY" && isDay == 1 && dayWeek == 0) return R.drawable.sun_cloud
    if (weatherDescription == "CLOUDY" && isDay == 1 && dayWeek == 1) return R.drawable.sun_cloud_white
    if (weatherDescription == "CLOUDY" && isDay == 0 && dayWeek == 0) return R.drawable.moon_cloud_black
    if (weatherDescription == "CLOUDY" && isDay == 0 && dayWeek == 1) return R.drawable.moon_cloud
    if (weatherDescription == "THUNDERSTORM" && isDay == 1 && dayWeek == 0) return R.drawable.thunderstorm_black
    if (weatherDescription == "THUNDERSTORM" && isDay == 1 && dayWeek == 1) return R.drawable.thunderstorm
    if (weatherDescription == "THUNDERSTORM" && isDay == 0 && dayWeek == 0) return R.drawable.thunderstorm_black
    if (weatherDescription == "THUNDERSTORM" && isDay == 0 && dayWeek == 1) return R.drawable.thunderstorm
    if (weatherDescription == "SNOW" && isDay == 1 && dayWeek == 0) return R.drawable.snow_black
    if (weatherDescription == "SNOW" && isDay == 1 && dayWeek == 1) return R.drawable.snow
    if (weatherDescription == "SNOW" && isDay == 0 && dayWeek == 0) return R.drawable.snow_black
    if (weatherDescription == "SNOW" && isDay == 0 && dayWeek == 1) return R.drawable.snow
    if (weatherDescription == "RAIN" && isDay == 1 && dayWeek == 0) return R.drawable.rain_black
    if (weatherDescription == "RAIN" && isDay == 1 && dayWeek == 1) return R.drawable.rain
    if (weatherDescription == "RAIN" && isDay == 0 && dayWeek == 0) return R.drawable.rain_black
    if (weatherDescription == "RAIN" && isDay == 0 && dayWeek == 1) return R.drawable.rain
    if (weatherDescription == "ICE" && isDay == 1 && dayWeek == 0) return R.drawable.ice_black
    if (weatherDescription == "ICE" && isDay == 1 && dayWeek == 1) return R.drawable.ice
    if (weatherDescription == "ICE" && isDay == 0 && dayWeek == 0) return R.drawable.ice_black
    return if (weatherDescription == "ICE" && isDay == 0 && dayWeek == 1) R.drawable.ice
    else R.drawable.ic_launcher_foreground
}

fun getWeatherColor(weatherDescription: String, isDay: Int): Color{
    if (weatherDescription == "CLEAR") return Color(0xFFFFD93B)
    if (weatherDescription == "CLOUDY") return Color(0xFFFFD3D3)
    if (weatherDescription == "THUNDERSTORM") return Color(0XFF244474)
    if (weatherDescription == "SNOW") return Color(0XFF4294FF)
    if (weatherDescription == "RAIN") return Color(0XFF2A98B7)
    return if (weatherDescription == "ICE") Color(0XFFA0C8D7)
    else{
        if (isDay == 0) Color(0XFFFFFFFF)
        else Color(0XFF000000)
    }
}

@Composable
fun WeatherCard(time: String, temp: String, color: Color, weatherIcon: Int, isDay: Int){
    var colorBack = Color.Black
    var colorFront = Color.White
    if (isDay == 1){
        colorBack = Color.White
        colorFront = Color.Black
    }
    Card (colors = CardColors(colorBack,
        colorFront,
        colorBack,
        colorFront),
        modifier = Modifier.padding(5.dp)){
        Text(text = time, color = color, fontSize = 50.sp, fontFamily = FontFamily(Font(R.font.dongle)), style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(2f, 2f),
                blurRadius = 2f
            )))
        Image(painter = painterResource(id = weatherIcon),contentDescription = "Current Weather Icon", modifier = Modifier
            .height(80.dp)
            .width(80.dp), contentScale = ContentScale.Crop)
        Text(modifier = Modifier.padding(start = 25.dp), text = temp, color = color, fontSize = 50.sp, fontFamily = FontFamily(Font(R.font.dongle)), style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(2f, 2f),
                blurRadius = 2f
            )))
    }
}

@Composable
fun WeeklyWeatherCard(time: String, temp: String, color: Color, weatherIcon: Int, isDay: Int){
    var colorBack = Color.Black
    var colorFront = Color.White
    if (isDay == 1){
        colorBack = Color.White
        colorFront = Color.Black
    }
    Card (colors = CardColors(colorBack,
        colorFront,
        colorBack,
        colorFront),
        modifier = Modifier.padding(5.dp)){
        Text(modifier = Modifier.padding(start = 10.dp), text = time, color = color, fontSize = 50.sp, fontFamily = FontFamily(Font(R.font.dongle)), style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(2f, 2f),
                blurRadius = 2f
            )))
        Image(painter = painterResource(id = weatherIcon),contentDescription = "Current Weather Icon", modifier = Modifier
            .height(80.dp)
            .width(80.dp), contentScale = ContentScale.Crop)
        Text(modifier = Modifier.padding(start = 25.dp), text = temp, color = color, fontSize = 50.sp, fontFamily = FontFamily(Font(R.font.dongle)),style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(2f, 2f),
                blurRadius = 2f
            )))
    }
}

@Composable
fun GetLocation(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    val hasLocationPermission = locationUtils.hasLocationPermission(context)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                Log.d("MainView", "Location permissions granted")
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                Toast.makeText(
                    context,
                    "Location Permission is Required. Please enable it in settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            locationUtils.requestLocationUpdates(viewModel)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            locationUtils.stopLocationUpdates()
        }
    }
}