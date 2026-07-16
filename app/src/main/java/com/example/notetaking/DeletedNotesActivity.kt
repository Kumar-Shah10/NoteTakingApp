package com.example.notetaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notetaking.ViewModel.NoteViewModel
import com.example.notetaking.model.NoteModel
import com.example.notetaking.repo.NoteRepoImpl
import com.example.notetaking.ui.EmptyState
import com.example.notetaking.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeletedNotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NoteTakingTheme { DeletedNotesScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedNotesScreen() {
    val vm      = remember { NoteViewModel().also { it.observeDeleted() } }
    val context = LocalContext.current

    var showDeleteAllDialog  by remember { mutableStateOf(false) }
    var showDeleteOneDialog  by remember { mutableStateOf<NoteModel?>(null) }
    var showRestoreAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Trash",
                            style = TextStyle(color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                        )
                        if (vm.notes.isNotEmpty()) {
                            Text(
                                "${vm.notes.size} deleted note${if (vm.notes.size != 1) "s" else ""}",
                                style = TextStyle(color = TextMuted, fontSize = 12.sp),
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { (context as DeletedNotesActivity).finish() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = TextMuted)
                    }
                },
                actions = {
                    if (vm.notes.isNotEmpty()) {
                        // Restore all
                        IconButton(onClick = { showRestoreAllDialog = true }) {
                            Icon(Icons.Outlined.RestoreFromTrash, contentDescription = "Restore all", tint = Gold)
                        }
                        // Delete all permanently
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(Icons.Outlined.DeleteForever, contentDescription = "Delete all", tint = ErrorText)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
            )
        },
        bottomBar = { BottomNav(context, -1) }, // -1 = no tab highlighted
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {

            // Info banner
            if (vm.notes.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3CD), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF92600A), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Notes in Trash are permanently removed when deleted here.",
                        style = TextStyle(color = Color(0xFF92600A), fontSize = 12.sp),
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            if (vm.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (vm.notes.isEmpty()) {
                EmptyState("Trash is empty", "Deleted notes will appear here.")
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                ) {
                    items(vm.notes, key = { it.id }) { note ->
                        DeletedNoteCard(
                            note      = note,
                            onRestore = { vm.restoreNote(note) },
                            onDelete  = { showDeleteOneDialog = note },
                        )
                    }
                }
            }
        }
    }

    // ── Confirm: delete single note permanently ──
    showDeleteOneDialog?.let { note ->
        ConfirmDialog(
            title   = "Delete permanently?",
            message = "\"${note.title.ifBlank { "Untitled" }}\" will be gone forever.",
            confirmLabel  = "Delete",
            confirmColor  = ErrorText,
            onDismiss     = { showDeleteOneDialog = null },
            onConfirm     = {
                vm.permanentDelete(note)
                showDeleteOneDialog = null
            },
        )
    }

    // ── Confirm: delete ALL permanently ──
    if (showDeleteAllDialog) {
        ConfirmDialog(
            title   = "Empty Trash?",
            message = "All ${vm.notes.size} notes will be permanently deleted. This cannot be undone.",
            confirmLabel  = "Empty Trash",
            confirmColor  = ErrorText,
            onDismiss     = { showDeleteAllDialog = false },
            onConfirm     = {
                vm.notes.forEach { vm.permanentDelete(it) }
                showDeleteAllDialog = false
            },
        )
    }

    // ── Confirm: restore ALL ──
    if (showRestoreAllDialog) {
        ConfirmDialog(
            title   = "Restore all notes?",
            message = "All ${vm.notes.size} notes will be moved back to your notes.",
            confirmLabel  = "Restore all",
            confirmColor  = Gold,
            onDismiss     = { showRestoreAllDialog = false },
            onConfirm     = {
                vm.notes.forEach { vm.restoreNote(it) }
                showRestoreAllDialog = false
            },
        )
    }
}

// ── Deleted note card ──────────────────────────────────────────────────────────

@Composable
private fun DeletedNoteCard(
    note: NoteModel,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
) {
    val fmt     = SimpleDateFormat("MMM d", Locale.getDefault())
    val dateStr = fmt.format(Date(note.updatedAt))

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Deleted badge
            Box(
                modifier = Modifier
                    .background(ErrorBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text("Deleted", style = TextStyle(color = ErrorText, fontSize = 10.sp, fontWeight = FontWeight.Medium))
            }

            Spacer(Modifier.height(8.dp))

            if (note.title.isNotBlank()) {
                Text(
                    note.title,
                    style = TextStyle(color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
            }

            if (note.content.isNotBlank()) {
                Text(
                    note.content,
                    style = TextStyle(color = TextTagline, fontSize = 13.sp),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(10.dp))
            }

            Text(dateStr, style = TextStyle(color = TextFaint, fontSize = 11.sp))

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            Spacer(Modifier.height(6.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Restore button
                TextButton(
                    onClick = onRestore,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Icon(
                        Icons.Outlined.RestoreFromTrash,
                        contentDescription = "Restore",
                        tint = Gold,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Restore", style = TextStyle(color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Medium))
                }

                // Permanent delete button
                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Icon(
                        Icons.Outlined.DeleteForever,
                        contentDescription = "Delete forever",
                        tint = ErrorText,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Delete", style = TextStyle(color = ErrorText, fontSize = 12.sp, fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

// ── Reusable confirm dialog ───────────────────────────────────────────────────

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    confirmColor: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(confirmColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (confirmColor == Gold) Icons.Outlined.RestoreFromTrash else Icons.Outlined.DeleteForever,
                        contentDescription = null,
                        tint = confirmColor,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(title, style = TextStyle(color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold))
                Spacer(Modifier.height(8.dp))
                Text(message, style = TextStyle(color = TextMuted, fontSize = 14.sp))
                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors  = ButtonDefaults.buttonColors(containerColor = confirmColor),
                        shape   = RoundedCornerShape(10.dp),
                    ) {
                        Text(confirmLabel, color = if (confirmColor == Gold) Color(0xFF14172B) else Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}