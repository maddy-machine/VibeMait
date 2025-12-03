package com.runanywhere.startup_hackathon20

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    private val _signUpState = mutableStateOf<SignUpState>(SignUpState.Idle)
    val signUpState: State<SignUpState> = _signUpState

    suspend fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty")
            return
        }
        _loginState.value = LoginState.Loading
        delay(1000) // Simulate network call
        _loginState.value = LoginState.Success
    }

    suspend fun signUp(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _signUpState.value = SignUpState.Error("Email and password cannot be empty")
            return
        }
        _signUpState.value = SignUpState.Loading
        delay(1000) // Simulate network call
        _signUpState.value = SignUpState.Success
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetSignUpState() {
        _signUpState.value = SignUpState.Idle
    }

    fun signOut() {
        _loginState.value = LoginState.Idle
        _signUpState.value = SignUpState.Idle
    }
}
