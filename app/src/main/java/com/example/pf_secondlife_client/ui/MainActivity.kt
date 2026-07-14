package com.example.pf_secondlife_client.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.pf_secondlife_client.navigation.MainNavHost
import com.example.pf_secondlife_client.ui.theme.PF_SecondLife_clientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PF_SecondLife_clientTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavHost()
                }
            }
        }
    }
}