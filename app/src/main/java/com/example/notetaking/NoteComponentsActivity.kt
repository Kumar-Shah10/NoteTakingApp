package com.example.notetaking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Note
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.notetaking.OutlinedTextFieldColorsDark
import com.example.notetaking.model.NoteModel
import com.example.notetaking.ui.theme.BorderColor
import com.example.notetaking.ui.theme.Gold
import com.example.notetaking.ui.theme.TextFaint
import com.example.notetaking.ui.theme.TextMuted
import com.example.notetaking.ui.theme.TextPlaceholder
import com.example.notetaking.ui.theme.TextPrimary
import com.example.notetaking.ui.theme.TextTagline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val cardAccents = listOf(
    Color(0xFFE8EEFF),
    Color(0xFFFFEEEE),
    Color(0xFFEEF8EE),
    Color(0xFFFFF8E8),
    Color(0xFFF5EEFF),
    Color(0xFFE8FFF8),
)

fun accentFor(id: String): Color {
    val idx = Math.abs(id.hashCode()) % cardAccents.size
    return cardAccents[idx]
}

// ── Shared Note Card ──────────────────────────────────────────────────────────

@Composable
fun SharedNoteCard(
    note: NoteModel,
    onTap: () -> Unit,
    onPin: () -> Unit,
    onFavorite: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    showArchiveIcon: Boolean = true,
) {
    val fmt     = SimpleDateFormat("MMM d", Locale.getDefault())
    val dateStr = fmt.format(Date(note.updatedAt))

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(166.dp)
            .clickable { onTap() },
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accentFor(note.id)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
        ) {
            // Title + content share the remaining space above the footer, so the
            // footer (date + action icons) always sits at the same spot regardless
            // of how much text the note has.
            Column(modifier = Modifier.weight(1f)) {
                if (note.title.isNotBlank()) {
                    Text(
                        note.title,
                        style = TextStyle(color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (note.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = TextMuted, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
                if (note.content.isNotBlank()) {
                    Text(
                        note.content,
                        style = TextStyle(color = TextTagline, fontSize = 13.sp, lineHeight = 18.sp),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(dateStr, style = TextStyle(color = TextFaint, fontSize = 11.sp))
                Row {
                    SmallIconBtn(
                        if (note.isPinned) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        tint = if (note.isPinned) Gold else TextMuted,
                        onClick = onPin,
                        // Add this:
                        testTag = "note_pin_button"
                    )
                    SmallIconBtn(
                        if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        tint = if (note.isFavorite) Color(0xFFE53E3E) else TextMuted,
                        onClick = onFavorite,
                        testTag = "note_favorite_button"
                    )
                    if (showArchiveIcon) {
                        SmallIconBtn(Icons.Outlined.Archive, tint = TextMuted, onClick = onArchive,
                            testTag = "note_archive_button")
                    }
                    SmallIconBtn(
                        Icons.Outlined.Delete,
                        tint = TextMuted,
                        onClick = { showDeleteConfirm = true },
                        testTag = "note_delete_button"
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        ConfirmDeleteDialog(
            title        = "Delete note?",
            message      = "This note will be moved to Trash. You can restore it later.",
            onDismiss    = { showDeleteConfirm = false },
            onConfirm    = { onDelete() }
        )
    }
}

@Composable
fun SmallIconBtn(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    testTag: String = ""   // new parameter
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(28.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

// ── Shared Confirm Delete Dialog ──────────────────────────────────────────────

@Composable
fun ConfirmDeleteDialog(
    title: String = "Delete note?",
    message: String = "This action cannot be undone.",
    confirmLabel: String = "Delete",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(title,   style = TextStyle(color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold))
                Spacer(modifier = Modifier.height(10.dp))
                Text(message, style = TextStyle(color = TextMuted,   fontSize = 14.sp))
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(); onDismiss() },
                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)),
                        shape   = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_delete_button")
                    ) {
                        Text(confirmLabel, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Shared Empty State ────────────────────────────────────────────────────────

@Composable
fun EmptyState(title: String, subtitle: String) {
    Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFE8EEFF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Note, contentDescription = null, tint = Gold, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = TextStyle(color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, style = TextStyle(color = TextMuted, fontSize = 13.sp))
        }
    }
}

// ── Shared Create / Edit Dialog ───────────────────────────────────────────────

@Composable
fun NoteDialog(
    dialogTitle: String,
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var noteTitle   by remember { mutableStateOf(initialTitle) }
    var noteContent by remember { mutableStateOf(initialContent) }

    // A consistent, pleasant accent for this dialog session (doesn't change on recompose).
    val accent = remember { cardAccents.random() }
    val accentDark = remember(accent) {
        Color(
            red   = (accent.red * 0.5f).coerceIn(0f, 1f),
            green = (accent.green * 0.4f).coerceIn(0f, 1f),
            blue  = (accent.blue * 0.55f).coerceIn(0f, 1f),
        )
    }

    val canSave = noteTitle.isNotBlank() || noteContent.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            shape     = RoundedCornerShape(28.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(16.dp),
            modifier  = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Gradient header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(
                            Brush.horizontalGradient(listOf(accentDark, accent)),
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.Note,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            dialogTitle,
                            style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Scrollable body so it never feels cramped, even with a lot of text
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                ) {
                    // Title field — big, bold, minimal
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        placeholder = { Text("Untitled note", color = TextPlaceholder, fontSize = 19.sp, fontWeight = FontWeight.Medium) },
                        textStyle = TextStyle(color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold),
                        singleLine = true,
                        shape  = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldColorsDark(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_field")

                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Content field — big roomy writing area
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        placeholder = { Text("What's on your mind?", color = TextPlaceholder, fontSize = 15.sp) },
                        textStyle = TextStyle(color = TextTagline, fontSize = 15.sp, lineHeight = 22.sp),
                        minLines = 10,
                        maxLines = 20,
                        shape  = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldColorsDark(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 260.dp)
                            .testTag("note_content_field"),

                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live character count
                    Text(
                        "${noteContent.length} characters",
                        style = TextStyle(color = TextFaint, fontSize = 12.sp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                // Footer actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick  = { onConfirm(noteTitle, noteContent) },
                        modifier = Modifier.testTag("note_save_button"),

                        enabled  = canSave,
                        shape    = RoundedCornerShape(50),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = Gold,
                            disabledContainerColor = Gold.copy(alpha = 0.35f),
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        contentPadding = PaddingValues(horizontal = 26.dp, vertical = 12.dp),
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}