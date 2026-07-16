package com.example.notetaking

import com.example.notetaking.repo.UserRepo
import com.example.notetaking.ViewModel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelTest {
    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login success")
            null
        }.`when`(repo).login(eq("kumar@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""

        viewModel.login("kumar@gmail.com", "123456") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Login success", messageResult)
        verify(repo).login(eq("kumar@gmail.com"), eq("123456"), any())
    }

    @Test
    fun login_failure_test_wrongPassword() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Incorrect password")
            null
        }.`when`(repo).login(eq("kumar@gmail.com"), eq("wrongpass"), any())

        var successResult = true
        var messageResult = ""

        viewModel.login("kumar@gmail.com", "wrongpass") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Incorrect password", messageResult)
        verify(repo).login(eq("kumar@gmail.com"), eq("wrongpass"), any())
    }
}