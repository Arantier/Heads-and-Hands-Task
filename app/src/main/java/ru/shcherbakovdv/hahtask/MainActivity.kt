package ru.shcherbakovdv.hahtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextEmail.apply{
            editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.userEmail.value = text.toString()
            } ?: throw IllegalStateException()
            viewModel.isEmailCorrect.observe(this@MainActivity) {
                this.apply{
                    error = getString(R.string.msg_incorrect_email)
                    isErrorEnabled = !it
                }
            }

            editTextPassword.apply{
                editText?.doOnTextChanged { text, _, _, _ ->
                    viewModel.userPassword.value = text.toString()
                } ?: throw IllegalStateException()
                viewModel.isPasswordCorrect.observe(this@MainActivity) {
                    this.apply{
                        error = getString(R.string.msg_incorrect_password)
                        isErrorEnabled = !it
                    }
                }
            }
        }
    }
}