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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_label_popup.*
import ru.shcherbakovdv.hahtask.data.Resource
import ru.shcherbakovdv.hahtask.data.Status


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private var repeatPasswordTransition = 0f
    private var isLabelVisible = false

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

    private fun showLabelMessage(data: Resource<String>) {
        labelPopup.apply {
            when (data.status) {
                Status.SUCCESS -> {
                    message.text = data.data
                    message.setTextColor(resources.getColor(R.color.textColor))
                    errorIcon.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    message.text = data.message
                    message.setTextColor(resources.getColor(R.color.design_default_color_error))
                    errorIcon.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    message.text = data.data ?: getString(R.string.msg_loading)
                    message.setTextColor(resources.getColor(R.color.textColor))
                    errorIcon.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            }
            if (!isLabelVisible) animate()
                .setStartDelay(0)
                .translationY(toolbar.height.toFloat())
                .withEndAction {
                    // I don't want the loading message to expire. Although, it has less priority,
                    // than new message.
                    if (data.status != Status.LOADING) animate()
                        .setStartDelay(3000)
                        .translationY(resources.getDimension(R.dimen.label_popup_initial_coordinateY))
                        .withEndAction {
                            message.text = ""
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

        viewModel.labelNotifications.observe(this, this::showLabelMessage)
        viewModel.requestResults.observe(this) { weather ->
            weather.data?.apply {
                val weatherIcon = when (weatherId) {
                    in 200..232 -> "⛈\n️"
                    in 300..321 -> "\uD83C\uDF27️ "
                    in 500..504 -> "\uD83C\uDF27️ "
                    511 -> "\uD83C\uDF28️"
                    in 600..622 -> "\uD83C\uDF28️"
                    in 701..771 -> "\uD83C\uDF2B️"
                    781 -> "\uD83C\uDF2A️"
                    800 -> "☀️"
                    801 -> "\uD83C\uDF24️"
                    802 -> "⛅"
                    803 -> "\uD83C\uDF25️"
                    804 -> "☁️"
                    else -> "❓"
                }
                var direction = when (windDirection) {
                    in 0..45 -> "N"
                    in 134..225 -> "S"
                    in 316..360 -> "N"
                    else -> ""
                } + when (windDirection) {
                    in 46..315 -> "E"
                    in 135..226 -> "W"
                    else -> ""
                }
                direction = when (direction) {
                    "N" -> "\u2b06"
                    "NW" -> "\u2196"
                    "NE" -> "\u2197"
                    "S" -> "\u2b07"
                    "SW" -> "\u2199"
                    "SE" -> "\u2198"
                    "W" -> "\u2b05"
                    "E" -> "\u27a1"
                    else -> ""
                } + "\ufe0f"
                Snackbar.make(
                    layout,
                    getString(
                        R.string.msg_weather_report,
                        weatherIcon,
                        temperature - 273.15,
                        pressure,
                        humidity,
                        direction,
                        windSpeed
                    ), Snackbar.LENGTH_LONG
                ).show()
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
            setEndIconOnClickListener { viewModel.showPasswordHint() }
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
            viewModel.processUserCredentials()
        }
    }
}