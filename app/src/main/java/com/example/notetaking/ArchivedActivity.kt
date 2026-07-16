package com.example.notetaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

class ArchivedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NoteTakingTheme { ArchivedScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedScreen() {
    val vm      = remember { NoteViewModel().also { it.observeArchived() } }
    val context = LocalContext.current
    var editNote by remember { mutableStateOf<NoteModel?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Archived",
                        style = TextStyle(
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
            )
        },
        bottomBar = { BottomNav(context, 4) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${vm.notes.size} notes",
                style = TextStyle(color = TextMuted, fontSize = 13.sp),
            )
            Spacer(Modifier.height(12.dp))

            if (vm.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (vm.notes.isEmpty()) {
                EmptyState(
                    title    = "Nothing archived",
                    subtitle = "Archive a note to move it here.",
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                ) {
                    items(vm.notes, key = { it.id }) { note ->
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