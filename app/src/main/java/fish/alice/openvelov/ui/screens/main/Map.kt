package fish.alice.openvelov.ui.screens.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fish.alice.openvelov.ui.maps.MapLibreView

@Composable
fun MapScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MapLibreView(
            modifier = Modifier.fillMaxSize()
        )
    }
}