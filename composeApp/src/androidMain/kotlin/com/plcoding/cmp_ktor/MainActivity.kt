package com.plcoding.cmp_ktor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import io.ktor.client.engine.okhttp.OkHttp
import networking.ClickUpClient
import networking.createHttpClient
import App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                client = remember {
                    val httpClient = createHttpClient(OkHttp.create()) // Crea el cliente HTTP
                    ClickUpClient(httpClient) // Crea ClickUpClient con el cliente HTTP
                }
            )
        }
    }
}