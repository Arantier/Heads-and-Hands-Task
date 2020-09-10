package ru.shcherbakovdv.hahtask

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.core.widget.doOnTextChanged
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    var repeatPasswordTransition = 0f

    private fun shakeView(view: View) {
        SpringAnimation(view, DynamicAnimation.TRANSLATION_X, 0f).apply {
            setStartValue(-32f)
            spring.apply {
                stiffness = SpringForce.STIFFNESS_MEDIUM
                dampingRatio = 0.1f
            }
        }.start()
    }

    private fun showRepeatPassword() {
        buttonCreate.text = getString(R.string.title_button_login)
        buttonLogin.text = getString(R.string.title_button_create)
        editTextRepeatPassword.apply {
            visibility = View.VISIBLE
            animate().translationY(repeatPasswordTransition)
                .withStartAction {
                    buttonLogin.animate().translationY(repeatPasswordTransition)
                }
        }
    }

    private fun hideRepeatPassword() {
        buttonCreate.text = getString(R.string.title_button_create)
        buttonLogin.text = getString(R.string.title_button_login)
        editTextRepeatPassword.apply {
            animate().translationY(0f)
                .withStartAction {
                    buttonLogin.animate().translationY(0f)
                }
                .withEndAction { visibility = View.GONE }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextRepeatPassword.post {
            repeatPasswordTransition =
                editTextPassword.measuredHeight + editTextPassword.marginTop + .0f
        }

        editTextEmail.apply {
            editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.userEmail.value = text.toString()
                isErrorEnabled = false
            } ?: throw IllegalStateException()
            viewModel.isEmailCorrect.observe(this@LoginActivity) {
                if (!it) {
                    error = getString(R.string.msg_incorrect_email)
                    shakeView(this)
                }
                isErrorEnabled = !it
            }
        }

        editTextPassword.apply {
            editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.userPassword.value = text.toString()
                isErrorEnabled = false
            } ?: throw IllegalStateException()
            viewModel.isPasswordCorrect.observe(this@LoginActivity) {
                if (!it) {
                    error = getString(R.string.msg_incorrect_password)
                    shakeView(this)
                }
                isErrorEnabled = !it
            }

            // Animation of slider with password requirements
            setEndIconOnClickListener {
                textPasswordHint.apply {
                    animate()
                        .setStartDelay(0)
                        .translationY(toolbar.height.toFloat())
                        .withEndAction {
                            animate()
                                .setStartDelay(3000)
                                .translationY(resources.getDimension(R.dimen.password_hint_initial_coordinateY))
                        }
                }
            }
        }

        editTextRepeatPassword.apply {
            editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.userRepeatPassword.value = text.toString()
                isErrorEnabled = false
            } ?: throw IllegalStateException()
            viewModel.isRepeatPasswordCorrect.observe(this@LoginActivity) {
                if (!it) {
                    error = getString(R.string.msg_passwords_not_match)
                    shakeView(this)
                }
                isErrorEnabled = !it
            }
        }


        buttonCreate.setOnClickListener {
            if (viewModel.mode == LoginViewModel.SIGN_IN) {
                showRepeatPassword()
                viewModel.mode = LoginViewModel.SIGN_UP
            } else {
                hideRepeatPassword()
                viewModel.mode = LoginViewModel.SIGN_IN
            }
        }

        buttonLogin.setOnClickListener {
            if (viewModel.mode == LoginViewModel.SIGN_IN) {
                viewModel.signIn()
            } else {
                viewModel.signUp()
            }
        }
    }
}