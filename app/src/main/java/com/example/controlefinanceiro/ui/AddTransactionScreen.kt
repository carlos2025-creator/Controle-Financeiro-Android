package com.example.controlefinanceiro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.controlefinanceiro.data.Category
import com.example.controlefinanceiro.data.TransactionType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(Category.OTHERS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Lançamento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Valor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tipo de Lançamento:", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TransactionType.values().forEach { transactionType ->
                    FilterChip(
                        selected = type == transactionType,
                        onClick = { type = transactionType },
                        label = { 
                            Text(
                                when(transactionType) {
                                    TransactionType.INCOME -> "Receita"
                                    TransactionType.EXPENSE -> "Despesa"
                                    TransactionType.INVESTMENT -> "Investir"
                                    TransactionType.WITHDRAWAL -> "Resgatar"
                                }
                            ) 
                        }
                    )
                }
            }

            Text("Categoria:", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.values().forEach { cat ->
                    InputChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat.displayName) },
                        leadingIcon = { Icon(cat.icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotEmpty() && value > 0) {
                        viewModel.addTransaction(title, value, type, category)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text("Salvar")
            }
        }
    }
}
