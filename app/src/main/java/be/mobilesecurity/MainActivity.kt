package be.mobilesecurity
import Helper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.howest.ti.mobilesecuirty.ui.theme.MobileSecurityTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import be.mobilesecurity.data.AppDatabase
import be.mobilesecurity.data.AppointmentEntity
import be.mobilesecurity.data.AppointmentRepository
import be.mobilesecurity.data.LocationDetails
import be.mobilesecurity.network.AppointmentApiService
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState


class MainActivity : ComponentActivity() {
    private lateinit var helper: Helper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "appointment-database"
        ).build()
        val appointmentDao = db.appointmentDao()
        val appointmentRepository = AppointmentRepository(appointmentDao)
        helper = Helper(this)

        setContent {
            MobileSecurityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        AppNavigation(appointmentRepository, helper)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(appointmentRepository: AppointmentRepository, helper: Helper) {
    val navController = rememberNavController()
    val appointmentsState = appointmentRepository.getAllAppointments().collectAsState(initial = emptyList())
    val appointments = appointmentsState.value

    NavHost(navController = navController, startDestination = "scheduleList") {
        composable("scheduleList") {
            Column {
                ScheduleHeader(navController, helper)
                ScheduleList(appointments)
            }
        }
        composable("adminPage"){ AdminPage(navController = navController) }
        composable("addAppointment") { AddAppointmentForm(navController, appointmentRepository) }
    }
}




@Composable
fun ScheduleHeader(navController: NavController, helper: Helper, modifier: Modifier = Modifier){
    val coroutineScope = rememberCoroutineScope()
    val (text, setText) = remember { mutableStateOf("")}
    val (secret, setSecret) = remember { mutableStateOf("")}
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Schedule",
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Button(
            onClick = { navController.navigate("addAppointment") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add New Appointment")
        }
        Button(
            onClick = { navController.navigate("adminPage") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Admin Page")
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    val (imagePath) = helper.takeScreenshot()
                    Log.d("MainActivity", "Screenshot captured at: $imagePath")
                    setText("Your Email and Password is incorrect! Use the correct one!! ")
                }

            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Get Free Money! $$$")
        }
        TextField(
            value = secret,
            onValueChange = setSecret,
            label = { Text("Put your email and password like this email-password") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = text)

    }
}

@Composable
fun AppointmentItem(appointment: AppointmentEntity) {
    Card(
        modifier = Modifier
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = when (appointment.importance) {
                        1 -> Color.LightGray
                        2 -> Color.Blue
                        3 -> Color.Yellow
                        4 -> Color.Magenta
                        5 -> Color.Cyan
                        else -> Color.White
                    }
                )
                .padding(8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = appointment.description)
        }
    }
}


@Composable
fun ScheduleList(appointments: List<AppointmentEntity>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(appointments) { appointment ->
            AppointmentItem(appointment)
        }
    }
}

@Composable
fun AdminPage(navController: NavController){
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (text, setText) = remember { mutableStateOf("")}

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = setUsername,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = setPassword,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Text(text = text)
        Button(
            onClick = {
                if (isAdmin(username, password)) {
                    setText("You are the admin. Correct Login Credentials")
                } else {
                    setText("Incorrect Credentials. You are not the Admin")
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text("Login")
        }

        Button(
            onClick = { navController.navigate("scheduleList") },
            modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Appointments")
        }
    }

}

fun isAdmin(username: String, password: String): Boolean {
    return username == "admin" && password == "admin123"
}


@Composable
fun AddAppointmentForm(navController: NavController, appointmentRepository: AppointmentRepository) {

    val (description, setDescription) = remember { mutableStateOf("") }
    val (importance, setImportance) = remember { mutableStateOf("") }
    val (location, setLocation) = remember { mutableStateOf("") }

    val (locationDetails, setLocationDetails) = remember { mutableStateOf(LocationDetails(0.0, 0.0, "")) }

    val apiService = AppointmentApiService()

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = description,
            onValueChange = setDescription,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = importance,
            onValueChange = setImportance,
            label = { Text("Importance (1-5)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val importanceValue = importance.toIntOrNull() ?: 1
                val appointment = AppointmentEntity(description = description, importance = importanceValue)
                coroutineScope.launch {
                    appointmentRepository.insertAppointment(appointment)
                    navController.navigate("scheduleList") {
                        popUpTo("scheduleList") { inclusive = true }
                    }
                }
                setDescription("")
                setImportance("")
                navController.navigate("scheduleList") {
                    popUpTo("scheduleList") { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Appointment")
        }
        TextField(
            value = location,
            onValueChange = setLocation,
            label = { Text("City") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        )
        Button(onClick = {
            apiService.fetchLocationDetails(location,
                successCallback = { lat, lon, code -> setLocationDetails(LocationDetails(lat, lon, code))
                },)
        }, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 10.dp)
        ) {
            Text(text = "Get Coordinates of City")
        }

        Text("Longitude:  ${locationDetails.longitude}")
        Text("Latitude:  ${locationDetails.latitude}")
        Text("Country Code:  ${locationDetails.countryCode}")



        Button(onClick = {
            navController.navigate("scheduleList") {
                popUpTo("scheduleList") { inclusive = true }
            }

        },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 100.dp)
        ) {
            Text("Go to Schedule")
        }

    }


}
