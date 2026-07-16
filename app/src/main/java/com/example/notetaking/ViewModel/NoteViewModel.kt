package com.example.notetaking.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.notetaking.model.NoteModel
import com.example.notetaking.repo.NoteRepo
import com.example.notetaking.repo.NoteRepoImpl
import com.google.firebase.database.ValueEventListener

class NoteViewModel(
    private val repo: NoteRepo = NoteRepoImpl(),
) : ViewModel() {

    var notes     by mutableStateOf<List<NoteModel>>(emptyList()); private set
    var isLoading by mutableStateOf(true);                          private set

    private var listener: ValueEventListener? = null

    // ── Per-screen observe starters ──────────────────────────────────────────

    fun observeAll() {
        isLoading = true
        listener = repo.observeNotes { fetched ->
            notes     = fetched
            isLoading = false
        }
    }

    fun observePinned() {
        isLoading = true
        listener = repo.observePinnedNotes { fetched ->
            notes     = fetched
            isLoading = false
        }
    }

    fun observeFavorites() {
        isLoading = true
        listener = repo.observeFavoriteNotes { fetched ->
            notes     = fetched
            isLoading = false
        }
    }

    fun observeArchived() {
        isLoading = true
        listener = repo.observeArchivedNotes { fetched ->
            notes     = fetched
            isLoading = false
        }
    }

    fun observeDeleted() {
        isLoading = true
        listener = repo.observeDeletedNotes { fetched ->
            notes     = fetched
            isLoading = false
        }
    }

    // ── Note operations ──────────────────────────────────────────────────────

    fun createNote(title: String, content: String, type: String = "note") {
        val note = NoteModel(
            title   = title.ifBlank { "Untitled" },
            content = content,
            type    = type,
        )
        repo.createNote(note) { _, _ -> }
    }

    fun updateNote(note: NoteModel, title: String, content: String, color: String = note.color) {
        repo.updateNote(note.copy(title = title, content = content, color = color)) { _, _ -> }
    }

    fun togglePin(note: NoteModel)       = repo.togglePin(note) { }
    fun toggleFavorite(note: NoteModel)  = repo.toggleFavorite(note) { }
    fun toggleArchive(note: NoteModel)   = repo.toggleArchive(note) { }
    fun deleteNote(note: NoteModel)      = repo.softDelete(note) { }
    fun restoreNote(note: NoteModel)     = repo.restoreNote(note) { }
    fun permanentDelete(note: NoteModel) = repo.permanentDelete(note) { }

    // ── Count helpers (used by HomeActivity stats) ────────────────────────────

    val pinnedCount   get() = notes.count { it.isPinned   && !it.isArchived }
    val favoriteCount get() = notes.count { it.isFavorite && !it.isArchived }
    val archivedCount get() = notes.count { it.isArchived }

    override fun onCleared() {
        super.onCleared()
        listener?.let { repo.removeObserver(it) }
    }
}