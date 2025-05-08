package io.github.kroune.nine_mens_morris_kmp_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.retainedComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = retainedComponent {
                RootComponent(it)
        }
        setContent {
            App(component)
        }
    }
}