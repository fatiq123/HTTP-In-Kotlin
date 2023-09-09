package com.example.httpinkotlin

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.httpinkotlin.database.MenuDatabase
import com.example.httpinkotlin.database.MenuItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : ComponentActivity() {

    // Room Database
    private val database by lazy {
        Room.databaseBuilder(applicationContext, MenuDatabase::class.java, "menu_db")
            .build()
    }


    // shared preferences
    private val tipMenuLiveData = MutableLiveData<Boolean>()
    private val sharedPreferences by lazy {
        getSharedPreferences("LittleLemon", MODE_PRIVATE)
    }

    // ktor 
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }
    private val menuItemsLiveData = MutableLiveData<List<String>>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tipMenuLiveData.value = sharedPreferences.getBoolean("Tip", false)

        setContent {

            val menuItems by database.menuDao().getAllMenuItems()
                .observeAsState(initial = emptyList())

            Column {
                var dishName by remember { mutableStateOf("") }
                var priceInput by remember { mutableStateOf("") }
                Row(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        modifier = Modifier.weight(.6f),
                        value = dishName,
                        onValueChange = { value -> dishName = value },
                        label = { Text("Dish name") }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TextField(
                        modifier = Modifier.weight(.4f),
                        value = priceInput,
                        onValueChange = { value -> priceInput = value },
                        label = { Text("Price") }
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        val newMenuItem = MenuItem(
                            id = UUID.randomUUID().toString(),
                            name = dishName,
                            price = priceInput.toDouble()
                        )
                        lifecycleScope.launch {
                            withContext(IO) {
                                database.menuDao().saveMenuItems(newMenuItem)
                            }
                        }
                        dishName = ""
                        priceInput = ""
                    }
                ) {
                    Text("Add dish")
                }
                ItemsList(menuItems)
            }
        }

            // Shared preference
            /*Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Add Tip?")

                val selected = tipMenuLiveData.observeAsState(false)

                Switch(checked = selected.value, onCheckedChange = {
                    sharedPreferences.edit(commit = true) {
                        putBoolean("Tip", it)
                    }
                    runOnUiThread {
                        tipMenuLiveData.value = it
                    }
                })
            }*/

            // using Ktor
            /*HTTPInKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val items = menuItemsLiveData.observeAsState(emptyList())
                        MenuItems(items.value)
                    }
                }
            }*/
        }

    /*private suspend fun fetchContent(): String {
        return httpClient
            .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
            .bodyAsText()
    }*/
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        lifecycleScope.launch {
            val menuItems = getMenu("Salads")

            runOnUiThread {
                menuItemsLiveData.value = menuItems
            }
        }
    }


    private suspend fun getMenu(category: String): List<String> {
        val response: Map<String, MenuCategory> =
            client
                .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
                .body()

        return response[category]?.menu ?: listOf()
    }



    @Composable
    private fun ItemsList(menuItems: List<MenuItem>) {
        if (menuItems.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                text = "The menu is empty"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                items(count = menuItems.size) { menuItem ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
//                        Text(menuItem.name)
                        Text(
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right,
                            text = "%.2f".format(/*menuItem.price*/)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            lifecycleScope.launch {
                                withContext(IO) {
                                    database.menuDao().deleteMenuItem(menuItem)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }




}


@Composable
fun MenuItems(
    items: List<String> = emptyList(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyColumn {
            itemsIndexed(items) { _, item ->
                MenuItemDetails(item)
            }
        }
    }
}

@Composable
fun MenuItemDetails(menuItem: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = menuItem)
    }
}




