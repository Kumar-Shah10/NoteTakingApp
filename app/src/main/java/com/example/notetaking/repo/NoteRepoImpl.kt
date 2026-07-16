package com.example.notetaking.repo

import com.example.notetaking.model.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NoteRepoImpl : NoteRepo {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseDatabase.getInstance().reference

    private val uid: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("No signed-in user")

    private fun notesRef() = db.child("notes").child(uid)

    private fun observe(
        predicate: (NoteModel) -> Boolean,
        onUpdate: (List<NoteModel>) -> Unit,
    ): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = snapshot.children.mapNotNull { child ->
                    try {
                        child.getValue(NoteModel::class.java)
                            ?.copy(id = child.key ?: return@mapNotNull null)
                    } catch (e: Exception) {
                        android.util.Log.e("NoteRepoImpl", "Parse error for ${child.key}: ${e.message}")
                        null
                    }
                }.filter(predicate)
                    .sortedByDescending { it.updatedAt }
                onUpdate(notes)
            }
            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("NoteRepoImpl", "DB error: ${error.message}")
            }
        }
        notesRef().addValueEventListener(listener)
        return listener
    }

    override fun observeNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener =
        observe({ !it.isDeleted }, onUpdate)

    override fun observePinnedNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener =
        observe({ it.isPinned && !it.isArchived && !it.isDeleted }, onUpdate)

    override fun observeFavoriteNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener =
        observe({ it.isFavorite && !it.isArchived && !it.isDeleted }, onUpdate)

    override fun observeArchivedNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener =
        observe({ it.isArchived && !it.isDeleted }, onUpdate)

    override fun observeDeletedNotes(onUpdate: (List<NoteModel>) -> Unit): ValueEventListener =
        observe({ it.isDeleted }, onUpdate)

    override fun removeObserver(listener: ValueEventListener) {
        notesRef().removeEventListener(listener)
    }

    override fun createNote(note: NoteModel, callback: (Boolean, String?) -> Unit) {
        val ref     = notesRef().push()
        val noteKey = ref.key ?: run { callback(false, null); return }
        val newNote = note.copy(
            id        = noteKey,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        ref.setValue(newNote)
            .addOnSuccessListener { callback(true, noteKey) }
            .addOnFailureListener { callback(false, it.localizedMessage) }
    }

    override fun updateNote(note: NoteModel, callback: (Boolean, String?) -> Unit) {
        val updated = note.copy(updatedAt = System.currentTimeMillis())
        notesRef().child(note.id).setValue(updated)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { callback(false, it.localizedMessage) }
    }

    // These keys now match the @PropertyName("isPinned"/"isFavorite"/"isArchived"/"isDeleted")
    // annotations on NoteModel, so a targeted field write here lands on exactly the same
    // key that full-object serialization (createNote/updateNote) and deserialization
    // (observe/getValue) use. No more relying on Firebase's implicit "is" stripping.

    override fun togglePin(note: NoteModel, callback: (Boolean) -> Unit) {
        notesRef().child(note.id).child("isPinned").setValue(!note.isPinned)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener {
                android.util.Log.e("NoteRepoImpl", "togglePin failed: ${it.message}")
                callback(false)
            }
    }

    override fun toggleFavorite(note: NoteModel, callback: (Boolean) -> Unit) {
        notesRef().child(note.id).child("isFavorite").setValue(!note.isFavorite)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener {
                android.util.Log.e("NoteRepoImpl", "toggleFavorite failed: ${it.message}")
                callback(false)
            }
    }

    override fun toggleArchive(note: NoteModel, callback: (Boolean) -> Unit) {
        notesRef().child(note.id).child("isArchived").setValue(!note.isArchived)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener {
                android.util.Log.e("NoteRepoImpl", "toggleArchive failed: ${it.message}")
                callback(false)
            }
    }

    override fun softDelete(note: NoteModel, callback: (Boolean) -> Unit) {
        notesRef().child(note.id).child("isDeleted").setValue(true)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener {
                android.util.Log.e("NoteRepoImpl", "softDelete failed: ${it.message}")
                callback(false)
            }
    }

    override fun permanentDelete(note: NoteModel, callback: (Boolean) -> Unit) {
        notesRef().child(note.id).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    override fun restoreNote(note: NoteModel, callback: (Boolean) -> Unit) {
        notesRef().child(note.id).child("isDeleted").setValue(false)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}