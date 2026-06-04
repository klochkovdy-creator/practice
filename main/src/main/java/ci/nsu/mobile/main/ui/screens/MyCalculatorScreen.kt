package ci.nsu.mobile.main.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.ui.viewmodels.DepositUiState
import ci.nsu.mobile.main.ui.viewmodels.DepositViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyCalculationsScreen(
    viewModel: DepositViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserCalculations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        Text("Мои расчёты", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is DepositUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DepositUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadUserCalculations() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is DepositUiState.Success -> {
                if (state.calculations.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("У вас пока нет сохранённых расчётов")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.calculations) { calculation ->
                            CalculationItem(
                                calculation = calculation,
                                onDelete = { viewModel.deleteCalculation(calculation) }
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun CalculationItem(
    calculation: DepositCalculation,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(calculation.calculationDate))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${String.format("%.2f", calculation.initialAmount)} ₽ → ${String.format("%.2f", calculation.finalAmount)} ₽",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Срок: ${calculation.periodMonths} мес. | Ставка: ${String.format("%.2f", calculation.interestRate)}%",
                style = MaterialTheme.typography.bodyMedium
            )
            if (calculation.monthlyTopUp != null && calculation.monthlyTopUp > 0) {
                Text(
                    text = "Пополнение: ${String.format("%.2f", calculation.monthlyTopUp)} ₽/мес",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Дата: $dateString",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Удалить")
            }
        }
    }
}