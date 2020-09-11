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
import kotlinx.android.synthetic.main.include_label_popup.*


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private var repeatPasswordTransition = 0f

    // Represents horizontal shake animation for any view
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

    private fun showLabelPopupMessage(message: String) {
        labelPopup.apply {
            text.text = message
            icon.visibility = View.GONE
            text.setTextColor(resources.getColor(R.color.textColor))
            animate()
                .setStartDelay(0)
                .translationY(toolbar.height.toFloat())
                .withEndAction {
                    animate()
                        .setStartDelay(3000)
                        .translationY(resources.getDimension(R.dimen.label_popup_initial_coordinateY))
                        .withEndAction {
                            text.text = ""
                        }
                }
        }
    }

    private fun showLabelPopupError(message: String) {
        labelPopup.apply {
            text.text = message
            icon.visibility = View.VISIBLE
            text.setTextColor(resources.getColor(R.color.design_default_color_error))
            animate()
                .setStartDelay(0)
                .translationY(toolbar.height.toFloat())
                .withEndAction {
                    animate()
                        .setStartDelay(3000)
                        .translationY(resources.getDimension(R.dimen.label_popup_initial_coordinateY))
                        .withEndAction {
                            text.text = ""
                        }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextRepeatPassword.post {
            repeatPasswordTransition =
                editTextPassword.measuredHeight + editTextPassword.marginTop + .0f
        }

        viewModel.labelNotifications.observe(this) {
            if (it.status == Status.ERROR) {
                showLabelPopupError(it.message ?: getString(R.string.msg_uknown_error))
            } else {
                showLabelPopupMessage(it.data ?: getString(R.string.msg_unexpected_outcome))
            }
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
                viewModel.labelNotifications.value =
                    Resource.success(getString(R.string.msg_password_hint))
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