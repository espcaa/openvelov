package fish.alice.openvelov.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import fish.alice.openvelov.R

enum class TopLevelDestination(
    val route: Any,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    @StringRes val label: Int,
) {
    MAP(MapTab, R.drawable.outline_map_24, R.drawable.filled_map_24, R.string.tab_map),
    PROFILE(ProfileTab, R.drawable.outline_person_24, R.drawable.filled_person_24, R.string.tab_profile),
}