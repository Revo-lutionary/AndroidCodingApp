package com.revolutionary.codelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.revolutionary.codelearn.navigation.CodeLearnNavHost
import com.revolutionary.codelearn.ui.theme.CodeLearnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeLearnTheme {
                CodeLearnNavHost()
            }
        }
    }
}
