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
// IMPORT BARU UNTUK MOSHI (BONUS)
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
// IMPORT BARU UNTUK URL ENCODING (BONUS)
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            Home (
                navigateFromHomeToResult = { listDataJson ->
                    // --- PERUBAHAN (BONUS) ---
                    // Encode JSON agar aman dikirim sebagai URL
                    val encodedJson = URLEncoder.encode(listDataJson, StandardCharsets.UTF_8.toString())
                    navController.navigate("resultContent/$encodedJson")
                }
            )
        }

        composable(
            "resultContent/{listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) {
            ResultContent(
                // getString() otomatis URL-decode, jadi kita dapat JSON-nya
                listData = it.arguments?.getString("listData").orEmpty()
            )
        }
    }
}

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

    // --- PERUBAHAN (BONUS) ---
    // Siapkan Moshi untuk konversi ke JSON
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val listType = Types.newParameterizedType(List::class.java, Student::class.java)
    val jsonAdapter: JsonAdapter<List<Student>> = moshi.adapter(listType)

    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { newName ->
            inputField.value = inputField.value.copy(name = newName)
        },
        onButtonClick = {
            // TUGAS 1: Cek isNotBlank() sudah ada di sini
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        },
        navigateFromHomeToResult = {
            // --- PERUBAHAN (BONUS) ---
            // Kirim data sebagai JSON string, bukan list.toString()
            val jsonString = jsonAdapter.toJson(listData.toList())
            navigateFromHomeToResult(jsonString)
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
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

        Row {
            PrimaryTextButton(
                text = stringResource(id = R.string.button_click),
                onClick = { onButtonClick() }
            )
            PrimaryTextButton(
                text = stringResource(id = R.string.button_navigate),
                onClick = { navigateFromHomeToResult() }
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

// --- PERUBAHAN BESAR (BONUS) ---
@Composable
fun ResultContent(listData: String) { // listData sekarang adalah JSON String

    // 1. Siapkan Moshi untuk mem-parsing JSON
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val listType = Types.newParameterizedType(List::class.java, Student::class.java)
    val jsonAdapter: JsonAdapter<List<Student>> = moshi.adapter(listType)

    // 2. Parse JSON kembali menjadi List<Student>
    val studentList = listData.let {
        jsonAdapter.fromJson(it)
    }.orEmpty()

    // 3. Tampilkan dengan LazyColumn (mirip HomeContent)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Beri padding agar rapi
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = "Submitted Data") // Judul Halaman

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(studentList) { item ->
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home(navigateFromHomeToResult = {})
    }
}

data class Student(
    var name: String
)