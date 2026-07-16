package com.example.notetaking

import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.platform.testTag
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notetaking.ViewModel.NoteViewModel
import com.example.notetaking.model.NoteModel
import com.example.notetaking.ui.EmptyState
import com.example.notetaking.ui.NoteDialog
import com.example.notetaking.ui.SharedNoteCard
import com.example.notetaking.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NoteTakingTheme { HomeScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val vm      = remember { NoteViewModel().also { it.observeAll() } }
    val context = LocalContext.current
    val user    = FirebaseAuth.getInstance().currentUser

    var showCreate by remember { mutableStateOf(false) }
    var editNote   by remember { mutableStateOf<NoteModel?>(null) }

    val recentNotes = vm.notes
        .filter { !it.isArchived && !it.isDeleted }
        .sortedByDescending { it.updatedAt }
        .take(4)

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Gold),
                            contentAlignment = Alignment.Center,
                        ) { Text("★", color = Color.White, fontSize = 14.sp) }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "NoteSphere",
                            style = TextStyle(color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, DeletedNotesActivity::class.java))
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Trash", tint = TextMuted)
                    }
                    // Profile avatar button
                    IconButton(
                        modifier = Modifier.testTag("profile_avatar"),
                        onClick = {
                        context.startActivity(Intent(context, ProfileActivity::class.java))
                    }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Gold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {

                            val avatarLetter = (user?.displayName?.ifBlank { null }
                                ?: user?.email?.substringBefore("@"))
                                ?.firstOrNull()?.uppercaseChar() ?: '?'
                            Text(
                                text = avatarLetter.toString(),
                                style = TextStyle(color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
            )
        },
        bottomBar = { BottomNav(context, 0) },
        floatingActionButton = {
            FloatingActionButton(
                modifier       = Modifier.testTag("add_note_fab"),
                onClick        = { showCreate = true },
                containerColor = Gold,
                contentColor   = Color.White,
                shape          = CircleShape,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New note", modifier = Modifier.size(26.dp))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Your workspace",
                style = TextStyle(color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
            )
            Text(
                "Capture, organise, remember.",
                style = TextStyle(color = TextMuted, fontSize = 13.sp),
            )
            Spacer(Modifier.height(20.dp))

            // Stats row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label     = "All Notes",
                    count     = vm.notes.count { !it.isArchived && !it.isDeleted },
                    bg        = Color(0xFFE8EEFF),
                    textColor = Gold,
                    modifier  = Modifier.weight(1f),
                ) { context.startActivity(Intent(context, AllNotesActivity::class.java)) }

                StatCard(
                    label     = "Pinned",
                    count     = vm.notes.count { it.isPinned && !it.isArchived && !it.isDeleted },
                    bg        = Color(0xFFF5EEFF),
                    textColor = Color(0xFF7C3AED),
                    modifier  = Modifier.weight(1f),
                ) { context.startActivity(Intent(context, PinnedActivity::class.java)) }

                StatCard(
                    label     = "Favorites",
                    count     = vm.notes.count { it.isFavorite && !it.isArchived && !it.isDeleted },
                    bg        = Color(0xFFFFEEEE),
                    textColor = Color(0xFFE53E3E),
                    modifier  = Modifier.weight(1f),
                ) { context.startActivity(Intent(context, FavoritesActivity::class.java)) }
            }

            Spacer(Modifier.height(24.dp))

            if (vm.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (recentNotes.isEmpty()) {
                EmptyState("No notes yet", "Tap + to write your first note.")
            } else {
                Text(
                    "RECENTLY UPDATED",
                    style = TextStyle(color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(12.dp))
                LazyVerticalStaggeredGrid(
                    columns               = StaggeredGridCells.Fixed(2),
                    contentPadding        = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing   = 12.dp,
                ) {
                    items(recentNotes, key = { it.id }) { note ->
                        SharedNoteCard(
                            note       = note,
                            onTap      = { editNote = note },
                            onPin      = { vm.togglePin(note) },
                            onFavorite = { vm.toggleFavorite(note) },
                            onArchive  = { vm.toggleArchive(note) },
                            onDelete   = { vm.deleteNote(note) },
                        )
                    }
                }
            }
        }
    }

    if (showCreate) {
        NoteDialog(
            dialogTitle = "New note",
            onDismiss   = { showCreate = false },
            onConfirm   = { t, c ->
                vm.createNote(t, c)
                showCreate = false
            },
        )
    }

    editNote?.let { note ->
        NoteDialog(
            dialogTitle    = "Edit note",
            initialTitle   = note.title,
            initialContent = note.content,
            onDismiss      = { editNote = null },
            onConfirm      = { t, c ->
                vm.updateNote(note, t, c)
                editNote = null
            },
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    count: Int,
    bg: Color,
    textColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                count.toString(),
                style = TextStyle(color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                label,
                style = TextStyle(color = textColor.copy(alpha = 0.7f), fontSize = 11.sp),
            )
        }
    }
}