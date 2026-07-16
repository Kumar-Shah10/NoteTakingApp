package com.example.notetaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.outlined.Search
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

class AllNotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NoteTakingTheme { AllNotesScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllNotesScreen() {
    val vm      = remember { NoteViewModel().also { it.observeAll() } }
    val context = LocalContext.current

    var search     by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var showCreate by remember { mutableStateOf(false) }
    var editNote   by remember { mutableStateOf<NoteModel?>(null) }

    val pinned   = vm.notes.filter { it.isPinned && !it.isArchived }
    val unpinned = vm.notes.filter { !it.isPinned && !it.isArchived }
    val all      = (pinned + unpinned).let { list ->
        if (search.isBlank()) list
        else list.filter { it.title.contains(search, true) || it.content.contains(search, true) }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = { search = it },
                            placeholder = { Text("Search…", color = TextPlaceholder, fontSize = 14.sp) },
                            singleLine = true,
                            shape  = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldColorsDark(),
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        )
                    } else {
                        Text("All Notes", style = TextStyle(color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold))
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch; if (!showSearch) search = "" }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search",
                            tint = if (showSearch) Gold else TextMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
            )
        },
        bottomBar = { BottomNav(context, 1) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }, containerColor = Gold, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Filled.Add, contentDescription = "New", modifier = Modifier.size(26.dp))
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(4.dp))
            Text("${all.size} note${if (all.size != 1) "s" else ""}", style = TextStyle(color = TextMuted, fontSize = 13.sp))
            Spacer(Modifier.height(12.dp))

            if (vm.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Gold) }
            } else if (all.isEmpty()) {
                EmptyState("No notes", "Tap + to create your first note.")
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                ) {
                    val pinnedFiltered = all.filter { it.isPinned }
                    val otherFiltered  = all.filter { !it.isPinned }

                    if (pinnedFiltered.isNotEmpty()) {
                        item(key = "ph") { Text("PINNED", style = TextStyle(color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(vertical = 4.dp)) }
                        items(pinnedFiltered, key = { it.id }) { note ->
                            SharedNoteCard(note = note, onTap = { editNote = note },
                                onPin = { vm.togglePin(note) }, onFavorite = { vm.toggleFavorite(note) },
                                onArchive = { vm.toggleArchive(note) }, onDelete = { vm.deleteNote(note) })
                        }
                        if (otherFiltered.isNotEmpty()) {
                            item(key = "oh") { Text("OTHER", style = TextStyle(color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(vertical = 4.dp)) }
                        }
                    }
                    items(otherFiltered, key = { it.id }) { note ->
                        SharedNoteCard(note = note, onTap = { editNote = note },
                            onPin = { vm.togglePin(note) }, onFavorite = { vm.toggleFavorite(note) },
                            onArchive = { vm.toggleArchive(note) }, onDelete = { vm.deleteNote(note) })
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