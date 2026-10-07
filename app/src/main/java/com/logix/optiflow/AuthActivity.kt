package com.logix.optiflow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.logix.optiflow.data.remote.toUserMessage
import com.logix.optiflow.di.SearchBookingModule
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val registerPatientUseCase = SearchBookingModule.registerPatientUseCase
    private val loginPatientUseCase = SearchBookingModule.loginPatientUseCase
    private val getPatientSessionUseCase = SearchBookingModule.getPatientSessionUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SearchBookingModule.init(applicationContext)
        setContentView(R.layout.activity_auth)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val clearSessionButton = findViewById<Button>(R.id.clearSessionButton)
        val statusText = findViewById<TextView>(R.id.authStatusText)
        val loadingIndicator = findViewById<ProgressBar>(R.id.authLoadingIndicator)

        lifecycleScope.launch {
            renderSession(statusText)
        }

        registerButton.setOnClickListener {
            lifecycleScope.launch {
                setLoading(true, loadingIndicator, registerButton, loginButton)
                try {
                    val patient =
                        registerPatientUseCase(
                            name = nameInput.text.toString().trim(),
                            email = emailInput.text.toString().trim(),
                            phone = phoneInput.text.toString().trim(),
                            password = passwordInput.text.toString(),
                        )
                    Log.d(TAG, "Registered patient ${patient.id}")
                    statusText.text =
                        getString(R.string.auth_register_success, patient.email, patient.id)
                } catch (error: Exception) {
                    statusText.text = getString(R.string.auth_error, error.toUserMessage())
                } finally {
                    setLoading(false, loadingIndicator, registerButton, loginButton)
                }
            }
        }

        loginButton.setOnClickListener {
            lifecycleScope.launch {
                setLoading(true, loadingIndicator, registerButton, loginButton)
                try {
                    val patient =
                        loginPatientUseCase(
                            email = emailInput.text.toString().trim(),
                            password = passwordInput.text.toString(),
                        )
                    Log.d(TAG, "Logged in patient ${patient.id}")
                    renderSession(statusText)
                } catch (error: Exception) {
                    statusText.text = getString(R.string.auth_error, error.toUserMessage())
                } finally {
                    setLoading(false, loadingIndicator, registerButton, loginButton)
                }
            }
        }

        clearSessionButton.setOnClickListener {
            lifecycleScope.launch {
                SearchBookingModule.patientRepository.clearSession()
                statusText.text = getString(R.string.auth_session_cleared)
            }
        }
    }

    private suspend fun renderSession(statusText: TextView) {
        val session = getPatientSessionUseCase()
        statusText.text =
            if (session == null) {
                getString(R.string.auth_no_session)
            } else {
                getString(
                    R.string.auth_session_saved,
                    session.email,
                    session.patientId,
                    if (session.token.isNullOrBlank()) {
                        getString(R.string.auth_token_missing)
                    } else {
                        getString(R.string.auth_token_saved)
                    },
                )
            }
    }

    private fun setLoading(
        loading: Boolean,
        loadingIndicator: ProgressBar,
        registerButton: Button,
        loginButton: Button,
    ) {
        loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        registerButton.isEnabled = !loading
        loginButton.isEnabled = !loading
    }

    companion object {
        private const val TAG = "PatientAuth"
    }
}
