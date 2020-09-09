package ru.shcherbakovdv.hahtask

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private fun shakeView(view: View) {
        SpringAnimation(view, DynamicAnimation.TRANSLATION_X, 0f).apply {
            setStartValue(-32f)
            spring.apply {
                stiffness = SpringForce.STIFFNESS_MEDIUM
                dampingRatio = 0.1f
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextEmail.apply {
            editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.userEmail.value = text.toString()
                isErrorEnabled = false
            } ?: throw IllegalStateException()
            viewModel.isEmailCorrect.observe(this@MainActivity) {
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
            viewModel.isPasswordCorrect.observe(this@MainActivity) {
                if (!it) {
                    error = getString(R.string.msg_incorrect_password)
                    shakeView(this)
                }
                isErrorEnabled = !it
            }

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

        buttonLogin.setOnClickListener {
            viewModel.checkCredentials()
        }
    }
}