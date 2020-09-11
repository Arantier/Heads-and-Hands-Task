package ru.shcherbakovdv.hahtask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var mode = SIGN_IN
    val userEmail: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val userPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val userRepeatPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val isEmailCorrect: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }
    val isPasswordCorrect: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }
    val isRepeatPasswordCorrect: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }

    val labelNotifications: MutableLiveData<Resource<String>> by lazy { MutableLiveData(Resource.success(""))}

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
        if (isEmailCorrect.value == true
            && isPasswordCorrect.value == true
        ) {
            val email = userEmail.value ?: ""
            val password = userPassword.value ?: ""
            requestUserData(email, password)
        }
    }

    private fun requestUserData(email: String, password: String) {

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
            val email = userEmail.value ?: ""
            val password = userPassword.value ?: ""
            registerUser(email, password)
        }
    }

    // Makes no sense, but I can't just user previous function. Looking messy.
    private fun registerUser(email: String, password: String) = requestUserData(email, password)

    companion object {
        val SIGN_IN = 10
        val SIGN_UP = 11
    }
}