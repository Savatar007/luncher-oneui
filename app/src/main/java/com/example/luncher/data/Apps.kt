package com.example.luncher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

/** One launchable app. The icon is decoded once, off the main thread. */
data class AppEntry(
    val label: String,
    val packageName: String,
    val className: String,
    val icon: ImageBitmap
) {
    val key: String get() = "$packageName/$className"
}

object AppRepository {

    private const val ICON_PX = 168

    /** All launchable apps, A-Z. Call from a background dispatcher: icon
     *  decoding for 100+ apps does not belong on the UI thread. */
    fun loadApps(context: Context): List<AppEntry> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        @Suppress("DEPRECATION")
        val resolved = pm.queryIntentActivities(intent, 0)
        val seen = HashSet<String>()
        val apps = ArrayList<AppEntry>()
        for (info in resolved) {
            val activity = info.activityInfo ?: continue
            if (activity.packageName == context.packageName) continue
            val key = activity.packageName + "/" + activity.name
            if (!seen.add(key)) continue
            val label = try {
                info.loadLabel(pm).toString()
            } catch (e: Exception) {
                activity.packageName
            }
            val icon = try {
                info.loadIcon(pm).toBitmap(ICON_PX, ICON_PX).asImageBitmap()
            } catch (e: Exception) {
                Bitmap.createBitmap(ICON_PX, ICON_PX, Bitmap.Config.ARGB_8888).asImageBitmap()
            }
            apps.add(AppEntry(label, activity.packageName, activity.name, icon))
        }
        return apps.sortedBy { it.label.lowercase() }
    }

    fun launch(context: Context, app: AppEntry) {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(ComponentName(app.packageName, app.className))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // App was uninstalled between load and tap: ignore.
        }
    }

    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // No settings app: ignore.
        }
    }

    /** The four Samsung dock defaults, in order: Phone, Messages, Browser,
     *  Camera. Returns package names; missing ones are skipped. */
    fun defaultDockPackages(context: Context): List<String> {
        val pm = context.packageManager
        val probes = listOf(
            Intent(Intent.ACTION_DIAL),
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com")),
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        )
        val result = ArrayList<String>()
        for (probe in probes) {
            val pkg = resolvePackage(pm, probe)
            if (pkg != null && pkg != "android" && !result.contains(pkg)) {
                result.add(pkg)
            }
        }
        return result
    }

    private fun resolvePackage(pm: PackageManager, intent: Intent): String? {
        return try {
            @Suppress("DEPRECATION")
            pm.resolveActivity(intent, 0)?.activityInfo?.packageName
        } catch (e: Exception) {
            null
        }
    }
}
