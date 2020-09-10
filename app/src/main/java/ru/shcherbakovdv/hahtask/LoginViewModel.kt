package ru.shcherbakovdv.hahtask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var mode = SIGN_IN
    val userEmail: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val userPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val userRepeatPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isEmailCorrect: MutableLiveData<Boolean> = MutableLiveData(true)
    val isPasswordCorrect: MutableLiveData<Boolean> = MutableLiveData(true)
    val isRepeatPasswordCorrect: MutableLiveData<Boolean> = MutableLiveData(true)

    // This is famous expression from https://emailregex.com which should "99.99% works"
    private fun isCorrectEmail(email: String) =
        """(?:[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"""
            .toRegex().matches(email)

    private fun isCorrectPassword(password: String) =
        password.toLowerCase() != password
                && password.toUpperCase() != password
                && password.length >= 6
                && password.contains("""\d""".toRegex())

    fun signIn() {
        isEmailCorrect.value = isCorrectEmail(userEmail.value ?: "")
        isPasswordCorrect.value = isCorrectPassword(userPassword.value ?: "")
        if (isEmailCorrect.value == true && isPasswordCorrect.value == true) {
            TODO()
        }
        if (isEmailCorrect.value == true
            && isPasswordCorrect.value == true
        ) {
            TODO()
        }
    }

    fun signUp() {
        isEmailCorrect.value = isCorrectEmail(userEmail.value ?: "")
        isPasswordCorrect.value = isCorrectPassword(userPassword.value ?: "")
        isRepeatPasswordCorrect.value = userPassword.value.equals(userRepeatPassword.value)
                && !userRepeatPassword.value.isNullOrBlank()
        if (isEmailCorrect.value == true
            && isPasswordCorrect.value == true
            && isRepeatPasswordCorrect.value == true
        ) {
            TODO()
        }
    }

    companion object {
        val SIGN_IN = 0
        val SIGN_UP = 1
    }
}