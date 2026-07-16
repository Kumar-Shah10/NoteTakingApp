package com.example.notetaking

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notetaking.ui.theme.Gold
import com.example.notetaking.ui.theme.TextMuted

private data class Tab(val label: String, val icon: ImageVector, val selectedIcon: ImageVector, val index: Int)

private val TABS = listOf(
    Tab("Home",      Icons.Outlined.Home,          Icons.Filled.Home,     0),
    Tab("All",       Icons.Outlined.Note,           Icons.Outlined.Note,   1),
    Tab("Pinned",    Icons.Outlined.BookmarkBorder, Icons.Outlined.Bookmark, 2),
    Tab("Favorites", Icons.Outlined.FavoriteBorder, Icons.Filled.Star,     3),
    Tab("Archived",  Icons.Outlined.Inventory2,     Icons.Outlined.Archive, 4),
)

@Composable
fun BottomNav(context: Context, currentIndex: Int) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
    ) {
        TABS.forEach { tab ->
            val selected = currentIndex == tab.index
            NavigationBarItem(
                selected  = selected,
                onClick   = {
                    if (!selected) {
                        val dest = when (tab.index) {
                            0 -> HomeActivity::class.java
                            1 -> AllNotesActivity::class.java
                            2 -> PinnedActivity::class.java
                            3 -> FavoritesActivity::class.java
                            4 -> ArchivedActivity::class.java
                            else -> HomeActivity::class.java
                        }
                        context.startActivity(Intent(context, dest))
                    }
                },
                icon = {
                    Icon(if (selected) tab.selectedIcon else tab.icon, contentDescription = tab.label)
                },
                label = {
                    Text(tab.label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Gold,
                    selectedTextColor   = Gold,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor      = Color(0xFFE8EEFF),
                ),
            )
        }
    }
}