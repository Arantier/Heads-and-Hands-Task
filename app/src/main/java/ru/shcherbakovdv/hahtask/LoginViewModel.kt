package ru.shcherbakovdv.hahtask

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.shcherbakovdv.hahtask.data.LoginRepository
import ru.shcherbakovdv.hahtask.data.Resource
import ru.shcherbakovdv.hahtask.data.Weather
import java.net.UnknownHostException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var mode = SIGN_IN
    val context: Context = getApplication<Application>().applicationContext
    val loginRepository = LoginRepository.getInstance()

    val userEmail: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isEmailCorrect: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }
    val userPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isPasswordCorrect: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }
    val userRepeatPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isRepeatPasswordCorrect: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }
    val labelNotifications: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }
    val requestResults: MutableLiveData<Resource<Weather>> by lazy {
        MutableLiveData<Resource<Weather>>()
    }

    // This is famous expression from https://emailregex.com which should "99.99% works"
    private fun isCorrectEmail(email: String) =
        """(?:[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"""
            .toRegex().matches(email)

    private fun isCorrectPassword(password: String) =
        password.toLowerCase() != password
                && password.toUpperCase() != password
                && password.length >= 6
                && password.contains("""\d""".toRegex())

    fun processUserCredentials() {
        if (mode == SIGN_IN) {
            signIn()
        } else {
            signUp()
        }
    }

    private fun signIn() {
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
        labelNotifications.value = Resource.loading(context.getString(R.string.msg_loading))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loginRepository.authenticateUser(email, password)
                    .let {
                        withContext(Dispatchers.Main) {
                            requestResults.value = Resource.success(it)
                            labelNotifications.value = Resource.success(context.getString(R.string.msg_success))
                        }
                    }
            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) {
                    labelNotifications.value =
                        Resource.error(
                            context.getString(R.string.msg_no_internet),
                            null
                        )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    labelNotifications.value =
                        Resource.error(
                            context.getString(R.string.msg_uknown_error),
                            null
                        )
                }
            }
        }
    }

    private fun signUp() {
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

    fun showPasswordHint() {
        labelNotifications.value =
            Resource.success(context.getString(R.string.msg_password_hint))
    }

    // Makes no sense, but I can't just user previous function. Looking messy.
    private fun registerUser(email: String, password: String) = requestUserData(email, password)

    companion object {
        val SIGN_IN = 10
        val SIGN_UP = 11
    }
}