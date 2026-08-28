package fish.alice.openvelov.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import fish.alice.openvelov.R
import fish.alice.openvelov.ui.design.icons.MapFilledIcon
import fish.alice.openvelov.ui.design.icons.MapIcon
import fish.alice.openvelov.ui.design.icons.PersonFilledIcon
import fish.alice.openvelov.ui.design.icons.PersonIcon

enum class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    @StringRes val label: Int,
) {
    MAP(MapTab, MapIcon, MapFilledIcon, R.string.tab_map),
    PROFILE(ProfileTab, PersonIcon, PersonFilledIcon, R.string.tab_profile),
}