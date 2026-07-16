package com.example.notetaking

import com.example.notetaking.ViewModel.NoteViewModel
import com.example.notetaking.model.NoteModel
import com.example.notetaking.repo.NoteRepo
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class NoteViewModelTest {

    @Test
    fun createNote_callsRepoCreateNote_withCorrectFields() {
        val repo = mock<NoteRepo>()
        val viewModel = NoteViewModel(repo)

        viewModel.createNote("Groceries", "Milk, eggs, bread")


        verify(repo).createNote(
            argThat { note -> note.title == "Groceries" && note.content == "Milk, eggs, bread" && note.type == "note" },
            any(),
        )
    }

    @Test
    fun deleteNote_callsRepoSoftDelete_withSameNote() {
        val repo = mock<NoteRepo>()
        val viewModel = NoteViewModel(repo)

        val note = NoteModel(
            id = "note_1",
            title = "Groceries",
            content = "Milk, eggs, bread",
        )

        viewModel.deleteNote(note)

        verify(repo).softDelete(eq(note), any())
    }

    @Test
    fun updateNote_callsRepoUpdateNote_withEditedFields() {
        val repo = mock<NoteRepo>()
        val viewModel = NoteViewModel(repo)

        val original = NoteModel(
            id = "note_1",
            title = "Groceries",
            content = "Milk, eggs, bread",
        )

        viewModel.updateNote(original, "Groceries (updated)", "Milk, eggs, bread, butter")


        verify(repo).updateNote(
            argThat { note ->
                note.id == "note_1" &&
                        note.title == "Groceries (updated)" &&
                        note.content == "Milk, eggs, bread, butter" &&
                        note.color == original.color
            },
            any(),
        )
    }
}