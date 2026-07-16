package com.example.notetaking.model

import com.google.firebase.database.PropertyName

data class NoteModel(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val type: String = "note",
    val color: String = "#FFFFFF",

    @get:PropertyName("isPinned")
    @set:PropertyName("isPinned")
    var isPinned: Boolean = false,

    @get:PropertyName("isFavorite")
    @set:PropertyName("isFavorite")
    var isFavorite: Boolean = false,

    @get:PropertyName("isArchived")
    @set:PropertyName("isArchived")
    var isArchived: Boolean = false,

    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)