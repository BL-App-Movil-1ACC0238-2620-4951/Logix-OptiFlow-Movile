package com.logix.optiflow.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logix.optiflow.data.remote.toUserMessage
import com.logix.optiflow.domain.model.PatientSession
import com.logix.optiflow.domain.usecase.GetPatientSessionUseCase
import com.logix.optiflow.domain.usecase.LoginPatientUseCase
import com.logix.optiflow.domain.usecase.RegisterPatientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UserRole {
    PATIENT,
    CLINICAL,
}

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val role: UserRole = UserRole.PATIENT,
    val passwordVisible: Boolean = false,
    val session: PatientSession? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val loginSucceeded: Boolean = false,
)

class AuthViewModel(
    private val registerPatient: RegisterPatientUseCase,
    private val loginPatient: LoginPatientUseCase,
    private val getPatientSession: GetPatientSessionUseCase,
    private val clearSession: suspend () -> Unit,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        refreshSession()
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun setRegisterMode(register: Boolean) {
        _uiState.update { it.copy(isRegisterMode = register, errorMessage = null, successMessage = null) }
    }

    fun setRole(role: UserRole) {
        _uiState.update { it.copy(role = role, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun refreshSession() {
        viewModelScope.launch {
            val session = getPatientSession()
            _uiState.update { it.copy(session = session, loginSucceeded = false) }
        }
    }

    fun continueWithSavedSession() {
        val session = _uiState.value.session
        if (session?.token.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Inicia sesión para continuar.") }
            return
        }
        _uiState.update { it.copy(loginSucceeded = true, errorMessage = null) }
    }

    fun submit() {
        if (_uiState.value.role == UserRole.CLINICAL) {
            _uiState.update {
                it.copy(errorMessage = "Personal clínico estará disponible en una próxima versión.")
            }
            return
        }
        if (_uiState.value.isRegisterMode) {
            register()
        } else {
            login()
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                registerPatient(
                    name = _uiState.value.name.trim(),
                    email = _uiState.value.email.trim(),
                    phone = _uiState.value.phone.trim(),
                    password = _uiState.value.password,
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegisterMode = false,
                        session = getPatientSession(),
                        successMessage = "Cuenta creada. Ahora inicia sesión.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                loginPatient(
                    email = _uiState.value.email.trim(),
                    password = _uiState.value.password,
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        session = getPatientSession(),
                        loginSucceeded = true,
                        successMessage = null,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            clearSession()
            _uiState.update {
                it.copy(session = null, loginSucceeded = false, successMessage = "Sesión cerrada.")
            }
        }
    }
}
