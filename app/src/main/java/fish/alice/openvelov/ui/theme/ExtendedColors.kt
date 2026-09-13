package fish.alice.openvelov.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class ExtendedColors(
    val electricBikeContainer: Color,
    val onElectricBikeContainer: Color,
)

val LightExtendedColorScheme = ExtendedColors(
    electricBikeContainer = Color(0xFFA3F5AB),
    onElectricBikeContainer = Color(0xFF002108)
)

val DarkExtendedColorScheme = ExtendedColors(
    electricBikeContainer = Color(0xFF00531E),
    onElectricBikeContainer = Color(0xFFA3F5AB)
)

val LocalExtendedColorScheme = staticCompositionLocalOf { LightExtendedColorScheme }

val MaterialTheme.extendedColorScheme: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current