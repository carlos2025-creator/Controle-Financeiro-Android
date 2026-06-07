package com.example.controlefinanceiro.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlefinanceiro.data.Transaction
import com.example.controlefinanceiro.data.TransactionType
import java.text.DateFormatSymbols
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val allTransactions by viewModel.allTransactions.collectAsState()

    val groupedReports = allTransactions.groupBy {
        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
    }.toSortedMap(compareByDescending { it })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatórios Mensais") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (groupedReports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum dado salvo ainda.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedReports.forEach { (key, transactions) ->
                    val parts = key.split("-")
                    val year = parts[0]
                    val monthIndex = parts[1].toInt()
                    val monthName = DateFormatSymbols(Locale("pt", "BR")).months[monthIndex]

                    item {
                        ReportCard(
                            month = monthName.uppercase(),
                            year = year,
                            transactions = transactions
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(month: String, year: String, transactions: List<Transaction>) {
    val totalIncome = transactions.filter { it.type == TransactionType.INCOME || it.type == TransactionType.WITHDRAWAL }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val totalInvested = transactions.filter { it.type == TransactionType.INVESTMENT }.sumOf { it.amount }
    val balance = totalIncome - totalExpense - totalInvested

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$month $year", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(
                    text = "Saldo: R$ ${String.format("%.2f", balance)}",
                    color = if (balance >= 0) Color(0xFF006400) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ReportItem(label = "Receitas", value = totalIncome, color = Color(0xFF006400))
                ReportItem(label = "Despesas", value = totalExpense, color = Color.Red)
                ReportItem(label = "Investido", value = totalInvested, color = Color.Blue)
            }
        }
    }
}

@Composable
fun ReportItem(label: String, value: Double, color: Color) {
    Column {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "R$ ${String.format("%.2f", value)}", fontSize = 14.sp, color = color, fontWeight = FontWeight.Bold)
    }
}
