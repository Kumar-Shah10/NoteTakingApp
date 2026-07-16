package com.example.notetaking.repo

import com.example.notetaking.model.UserModel

interface UserRepo {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit)

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit)

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)

    fun logout(callback: (Boolean, String) -> Unit)

    fun editProfile(id: String, model: UserModel, callback: (Boolean, String) -> Unit)
    fun addUser(id: String, model: com.example.notetaking.model.UserModel, callback: (Boolean, String) -> Unit)


}