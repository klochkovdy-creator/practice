package ci.nsu.mobile.main.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.ui.viewmodels.DepositUiState
import ci.nsu.mobile.main.ui.viewmodels.DepositViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositCalculatorScreen(
    onCalculationSaved: () -> Unit,
    viewModel: DepositViewModel
) {
    var step by remember { mutableStateOf(1) }

    var initialAmount by remember { mutableStateOf("") }
    var periodMonths by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var monthlyTopUp by remember { mutableStateOf("") }

    var resultFinalAmount by remember { mutableStateOf(0.0) }
    var resultInterestEarned by remember { mutableStateOf(0.0) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DepositUiState.CalculationSaved) {
            viewModel.resetState()
            onCalculationSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (step) {
            1 -> StepOneScreen(
                initialAmount = initialAmount,
                onInitialAmountChange = { initialAmount = it },
                periodMonths = periodMonths,
                onPeriodMonthsChange = { periodMonths = it },
                interestRate = interestRate,
                onInterestRateChange = { interestRate = it },
                monthlyTopUp = monthlyTopUp,
                onMonthlyTopUpChange = { monthlyTopUp = it },
                onNext = {
                    step = 2
                    val initial = initialAmount.toDoubleOrNull() ?: 0.0
                    val months = periodMonths.toIntOrNull() ?: 0
                    val rate = interestRate.toDoubleOrNull() ?: 0.0
                    val monthly = monthlyTopUp.toDoubleOrNull()

                    val monthlyRate = rate / 100 / 12
                    var finalAmount = initial
                    for (i in 1..months) {
                        finalAmount += finalAmount * monthlyRate
                        monthly?.let { finalAmount += it }
                    }
                    resultFinalAmount = finalAmount
                    resultInterestEarned = finalAmount - initial - (monthly ?: 0.0) * months
                }
            )
            2 -> StepTwoScreen(
                initialAmount = initialAmount.toDoubleOrNull() ?: 0.0,
                periodMonths = periodMonths.toIntOrNull() ?: 0,
                interestRate = interestRate.toDoubleOrNull() ?: 0.0,
                monthlyTopUp = monthlyTopUp.toDoubleOrNull(),
                finalAmount = resultFinalAmount,
                interestEarned = resultInterestEarned,
                isLoading = uiState is DepositUiState.Loading,
                onBack = { step = 1 },
                onSave = {
                    viewModel.saveCalculation(
                        initialAmount = initialAmount.toDoubleOrNull() ?: 0.0,
                        periodMonths = periodMonths.toIntOrNull() ?: 0,
                        interestRate = interestRate.toDoubleOrNull() ?: 0.0,
                        monthlyTopUp = monthlyTopUp.toDoubleOrNull(),
                        finalAmount = resultFinalAmount,
                        interestEarned = resultInterestEarned
                    )
                }
            )
        }

        if (uiState is DepositUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (uiState as DepositUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun StepOneScreen(
    initialAmount: String,
    onInitialAmountChange: (String) -> Unit,
    periodMonths: String,
    onPeriodMonthsChange: (String) -> Unit,
    interestRate: String,
    onInterestRateChange: (String) -> Unit,
    monthlyTopUp: String,
    onMonthlyTopUpChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Новый расчёт", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = initialAmount,
            onValueChange = onInitialAmountChange,
            label = { Text("Сумма вклада (руб)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = periodMonths,
            onValueChange = onPeriodMonthsChange,
            label = { Text("Срок (месяцев)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = interestRate,
            onValueChange = onInterestRateChange,
            label = { Text("Годовая ставка (%)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = monthlyTopUp,
            onValueChange = onMonthlyTopUpChange,
            label = { Text("Ежемесячное пополнение (руб, необязательно)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Рассчитать →")
        }
    }
}

@Composable
private fun StepTwoScreen(
    initialAmount: Double,
    periodMonths: Int,
    interestRate: Double,
    monthlyTopUp: Double?,
    finalAmount: Double,
    interestEarned: Double,
    isLoading: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Результат расчёта", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Начальная сумма: ${String.format("%.2f", initialAmount)} ₽")
                Text("Срок: $periodMonths мес.")
                Text("Ставка: ${String.format("%.2f", interestRate)}%")
                if (monthlyTopUp != null && monthlyTopUp > 0) {
                    Text("Ежемесячное пополнение: ${String.format("%.2f", monthlyTopUp)} ₽")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Итоговая сумма: ${String.format("%.2f", finalAmount)} ₽")
                Text("Заработано процентов: ${String.format("%.2f", interestEarned)} ₽")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Назад")
            }
            Button(onClick = onSave, modifier = Modifier.weight(1f), enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Сохранить")
                }
            }
        }
    }
}