package com.example.notetaking.model

data class UserModel(
    val id : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
) {
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
        )
    }
}
