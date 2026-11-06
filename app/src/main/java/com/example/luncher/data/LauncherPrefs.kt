package com.example.luncher.data

import android.content.Context

/** Home search modes, matching One UI 9's "Show search on Home screen":
 *  Google only, Finder only, both side by side, or off. */
object HomeSearchMode {
    const val OFF = "off"
    const val GOOGLE = "google"
    const val FINDER = "finder"
    const val BOTH = "both"
}

data class LauncherSettings(
    val showLabels: Boolean = true,
    val homeColumns: Int = 4,
    val drawerColumns: Int = 4,
    val floatingDock: Boolean = true,
    val homeSearchMode: String = HomeSearchMode.BOTH,
    /** Package names, in pin order. Empty = show the first apps instead. */
    val pinned: List<String> = emptyList(),
    val hidden: Set<String> = emptySet()
)

object PrefsStore {
    private const val FILE = "luncher_prefs"
    private const val SEPARATOR = ","

    fun load(context: Context): LauncherSettings {
        val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return LauncherSettings(
            showLabels = prefs.getBoolean("show_labels", true),
            homeColumns = prefs.getInt("home_columns", 4),
            drawerColumns = prefs.getInt("drawer_columns", 4),
            floatingDock = prefs.getBoolean("floating_dock", true),
            homeSearchMode = prefs.getString("home_search", HomeSearchMode.BOTH)
                ?: HomeSearchMode.BOTH,
            pinned = prefs.getString("pinned", "")
                ?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList(),
            hidden = prefs.getString("hidden", "")
                ?.split(SEPARATOR)?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        )
    }

    fun save(context: Context, settings: LauncherSettings) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putBoolean("show_labels", settings.showLabels)
            .putInt("home_columns", settings.homeColumns)
            .putInt("drawer_columns", settings.drawerColumns)
            .putBoolean("floating_dock", settings.floatingDock)
            .putString("home_search", settings.homeSearchMode)
            .putString("pinned", settings.pinned.joinToString(SEPARATOR))
            .putString("hidden", settings.hidden.joinToString(SEPARATOR))
            .apply()
    }
}
