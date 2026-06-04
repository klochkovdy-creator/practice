package ci.nsu.mobile.main.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.data.repository.DepositRepository
import ci.nsu.mobile.main.data.token.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DepositUiState {
    object Idle : DepositUiState()
    object Loading : DepositUiState()
    data class Success(val calculations: List<DepositCalculation>) : DepositUiState()
    data class Error(val message: String) : DepositUiState()
    object CalculationSaved : DepositUiState()
}

class DepositViewModel(
    private val repository: DepositRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DepositUiState>(DepositUiState.Idle)
    val uiState: StateFlow<DepositUiState> = _uiState

    fun loadUserCalculations() {
        viewModelScope.launch {
            _uiState.value = DepositUiState.Loading
            val userId = TokenManager.userId
            if (userId == null) {
                _uiState.value = DepositUiState.Error("Пользователь не авторизован")
                return@launch
            }
            val calculations = repository.getUserCalculations(userId)
            _uiState.value = DepositUiState.Success(calculations)
        }
    }

    fun saveCalculation(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double?,
        finalAmount: Double,
        interestEarned: Double
    ) {
        viewModelScope.launch {
            val userId = TokenManager.userId
            if (userId == null) {
                _uiState.value = DepositUiState.Error("Пользователь не авторизован")
                return@launch
            }
            val calculation = DepositCalculation(
                userId = userId,
                initialAmount = initialAmount,
                periodMonths = periodMonths,
                interestRate = interestRate,
                monthlyTopUp = monthlyTopUp,
                finalAmount = finalAmount,
                interestEarned = interestEarned,
                calculationDate = System.currentTimeMillis()
            )
            repository.saveCalculation(calculation)
            _uiState.value = DepositUiState.CalculationSaved
        }
    }

    fun deleteCalculation(calculation: DepositCalculation) {
        viewModelScope.launch {
            repository.deleteCalculation(calculation)
            loadUserCalculations()
        }
    }

    fun resetState() {
        _uiState.value = DepositUiState.Idle
    }
}