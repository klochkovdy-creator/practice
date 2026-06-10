package ci.nsu.mobile.main.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.ui.viewmodels.UsersState
import ci.nsu.mobile.main.ui.viewmodels.UsersViewModel

@Composable
fun UserDetailScreen(
    userId: Int,
    onBack: () -> Unit,
    viewModel: UsersViewModel = viewModel()
) {
    val usersState by viewModel.usersState.collectAsState()

    val user = when (val state = usersState) {
        is UsersState.Success -> state.users.find { it.id == userId }
        else -> null
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Text("Детали пользователя", style = MaterialTheme.typography.headlineSmall)
        }
        HorizontalDivider()

        if (user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                val fullName = listOfNotNull(
                    user.person?.lastName,
                    user.person?.firstName,
                    user.person?.middleName
                ).joinToString(" ").ifBlank { user.login }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(fullName, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Логин: ${user.login}")
                        Text("Email: ${user.email}")
                        user.phoneNumber?.let { Text("Телефон: $it") }
                        user.person?.let { person ->
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            Text("Дата рождения: ${person.birthDate}")
                            Text("Пол: ${if (person.gender == "MALE") "Мужской" else "Женский"}")
                        }
                    }
                }
            }
        }
    }
}
