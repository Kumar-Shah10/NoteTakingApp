package com.example.notetaking.repo

import com.example.notetaking.model.NoteModel
import com.google.firebase.database.ValueEventListener

interface NoteRepo {
    fun observeNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener
    fun observePinnedNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener
    fun observeFavoriteNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener
    fun observeArchivedNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener
    fun observeDeletedNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener
    fun removeObserver(listener: ValueEventListener)
    fun createNote(note: NoteModel, callback: (Boolean, String?) -> Unit)
    fun updateNote(note: NoteModel, callback: (Boolean, String?) -> Unit)
    fun togglePin(note: NoteModel, callback: (Boolean) -> Unit)
    fun toggleFavorite(note: NoteModel, callback: (Boolean) -> Unit)
    fun toggleArchive(note: NoteModel, callback: (Boolean) -> Unit)
    fun softDelete(note: NoteModel, callback: (Boolean) -> Unit)
    fun permanentDelete(note: NoteModel, callback: (Boolean) -> Unit)
    fun restoreNote(note: NoteModel, callback: (Boolean) -> Unit)
}