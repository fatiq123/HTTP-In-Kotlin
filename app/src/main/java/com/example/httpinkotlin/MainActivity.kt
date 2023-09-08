package com.example.httpinkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import com.example.httpinkotlin.ui.theme.HTTPInKotlinTheme
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val responseLiveData = MutableLiveData<String>()
    private val httpClient = HttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HTTPInKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val responseState = responseLiveData.observeAsState("").value

                    Column {
                        Button(
                            onClick = {
                                lifecycleScope.launch {
                                    val response = fetchContent()
                                    runOnUiThread {
                                        responseLiveData.value = response
                                    }
                                }
                            }
                        ) {
                            Text(text = "Download")
                        }

                        Text(text = responseState.toString())
                    }
                }
            }
        }
    }
    private suspend fun fetchContent(): String {
        return httpClient
            .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
            .bodyAsText()
    }
}
