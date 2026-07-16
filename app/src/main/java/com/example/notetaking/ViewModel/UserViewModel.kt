package com.example.notetaking.ViewModel

import androidx.lifecycle.ViewModel
import com.example.notetaking.model.UserModel
import com.example.notetaking.repo.UserRepo

class UserViewModel (val repo: UserRepo) : ViewModel() {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun editProfile(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.editProfile(id, model, callback)
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUser(id, model, callback)
    }
}