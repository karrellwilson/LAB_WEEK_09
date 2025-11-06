package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Step 5: Buat NavController dan panggil App
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

// Step 4: Composable baru untuk NavHost (root aplikasi)
@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        // Rute untuk layar "home"
        composable("home") {
            Home (
                navigateFromHomeToResult = { listDataString ->
                    // Aksi navigasi: kirim data ke rute "resultContent"
                    navController.navigate("resultContent/$listDataString")
                }
            )
        }

        // Rute untuk layar "resultContent"
        composable(
            "resultContent/{listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) {
            // Ambil data yang dikirim
            ResultContent(
                listData = it.arguments?.getString("listData").orEmpty()
            )
        }
    }
}

// Step 6: Tambah parameter navigasi
@Composable
fun Home(navigateFromHomeToResult: (String) -> Unit) {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }
    val inputField = remember { mutableStateOf(Student("")) }

    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { newName ->
            inputField.value = inputField.value.copy(name = newName)
        },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        },
        // Step 8: Kirim data list saat navigasi
        navigateFromHomeToResult = {
            navigateFromHomeToResult(listData.toList().toString())
        }
    )
}

// Step 7: Tambah parameter navigasi
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit // Parameter baru
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

        TextField(
            value = inputField.name,
            onValueChange = { onInputValueChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Step 9: Buat Row untuk dua tombol
        Row {
            PrimaryTextButton(
                text = stringResource(id = R.string.button_click),
                onClick = { onButtonClick() }
            )
            PrimaryTextButton(
                text = stringResource(id = R.string.button_navigate),
                onClick = { navigateFromHomeToResult() } // Panggil navigasi
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(listData) { item ->
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// Step 10: Composable baru untuk layar hasil
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundItemText(text = listData)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        // Perlu di-update untuk panggil Home dengan parameter
        Home(navigateFromHomeToResult = {})
    }
}

data class Student(
    var name: String
)