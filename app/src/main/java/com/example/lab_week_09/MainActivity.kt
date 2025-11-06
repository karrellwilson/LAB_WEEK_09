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
import androidx.compose.runtime.mutableStateListOf // <-- IMPORT BARU
import androidx.compose.runtime.mutableStateOf    // <-- IMPORT BARU
import androidx.compose.runtime.remember          // <-- IMPORT BARU
import androidx.compose.runtime.snapshots.SnapshotStateList // <-- IMPORT BARU
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // <-- Untuk warna ungu
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Step 6: Panggil Home() tanpa parameter
                    Home()
                }
            }
        }
    }
}

@Composable
fun Home() {
    // Step 3: Buat state untuk menyimpan data
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    // Buat state untuk menyimpan nilai input
    val inputField = remember { mutableStateOf(Student("")) }

    // Step 3: Panggil HomeContent (Composable baru)
    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { newName ->
            // Update state inputField setiap ada ketikan
            inputField.value = inputField.value.copy(name = newName)
        },
        onButtonClick = {
            // Logika saat tombol diklik
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("") // Kosongkan field setelah submit
            }
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. TAMBAHKAN KEMBALI Text DI SINI
        Text(text = stringResource(id = R.string.enter_item))

        TextField(
            value = inputField.name,
            onValueChange = { onInputValueChange(it) },

            // Sesuai slide Part 2, ganti ke Text
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

            // 2. KEMBALIKAN modifier ke default (hapus .fillMaxWidth dan .padding)
            //    HAPUS placeholder
            //    HAPUS colors
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = { onButtonClick() }
            // 3. HAPUS paksaan warna ungu dari tombol
            //    colors = ButtonDefaults.buttonColors(...)
        ) {
            Text(text = stringResource(id = R.string.button_click))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(listData) { item ->
                Text(text = item.name, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home() // Preview juga panggil Home()
    }
}

// Step 2: Buat Data Model di luar class
data class Student(
    var name: String
)