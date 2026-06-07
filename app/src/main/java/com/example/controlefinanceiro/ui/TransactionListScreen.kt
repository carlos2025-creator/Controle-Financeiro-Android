package com.example.controlefinanceiro.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlefinanceiro.data.Category
import com.example.controlefinanceiro.data.Transaction
import com.example.controlefinanceiro.data.TransactionType
import java.text.DateFormatSymbols
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel,
    onAddTransactionClick: () -> Unit,
    onReportsClick: () -> Unit
) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val monthlyBalance by viewModel.monthlyBalance.collectAsState()
    val totalInvested by viewModel.totalInvested.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    val incomes = transactions.filter { it.type == TransactionType.INCOME }
    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
    val investments = transactions.filter { it.type == TransactionType.INVESTMENT }
    val withdrawals = transactions.filter { it.type == TransactionType.WITHDRAWAL }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Controle Financeiro", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.ExtraBold 
                    ) 
                },
                actions = {
                    IconButton(onClick = onReportsClick) {
                        Icon(Icons.Default.List, contentDescription = "Relatórios")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClick) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MonthSelector(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onPrevious = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() }
            )

            BalanceDashboard(
                monthlyBalance = monthlyBalance, 
                totalInvested = totalInvested,
                onInvestClick = { viewModel.investSurplus() }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (incomes.isNotEmpty()) {
                    stickyHeader { SectionHeader(title = "RECEITAS", color = Color.Green) }
                    items(incomes) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) },
                            onEdit = { editingTransaction = it }
                        )
                    }
                }

                if (withdrawals.isNotEmpty()) {
                    stickyHeader { SectionHeader(title = "RESGATES", color = Color(0xFFFFA500)) }
                    items(withdrawals) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) },
                            onEdit = { editingTransaction = it }
                        )
                    }
                }

                if (expenses.isNotEmpty()) {
                    stickyHeader {
                        SectionHeader(
                            title = "DESPESAS", 
                            color = Color.Red,
                            topPadding = 8.dp 
                        )
                    }
                    items(expenses) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) },
                            onEdit = { editingTransaction = it }
                        )
                    }
                }

                if (investments.isNotEmpty()) {
                    stickyHeader {
                        SectionHeader(
                            title = "INVESTIMENTOS", 
                            color = Color.Cyan,
                            topPadding = 8.dp 
                        )
                    }
                    items(investments) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) },
                            onEdit = { editingTransaction = it }
                        )
                    }
                }
                
                if (transactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Nenhum registro neste mês.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (editingTransaction != null) {
        EditTransactionDialog(
            transaction = editingTransaction!!,
            onDismiss = { editingTransaction = null },
            onConfirm = { updated ->
                viewModel.updateTransaction(updated)
                editingTransaction = null
            }
        )
    }
}

@Composable
fun MonthSelector(selectedMonth: Int, selectedYear: Int, onPrevious: () -> Unit, onNext: () -> Unit) {
    val months = DateFormatSymbols(Locale("pt", "BR")).months
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Anterior")
        }
        Text(
            text = "${months[selectedMonth].uppercase()} $selectedYear",
            modifier = Modifier.width(180.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Próximo")
        }
    }
}

@Composable
fun BalanceDashboard(monthlyBalance: Double, totalInvested: Double, onInvestClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(3.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "SALDO MENSAL", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            OutlinedText(
                text = "R$ ${String.format("%.2f", monthlyBalance)}",
                fillColor = if (monthlyBalance >= 0) Color.Green else Color.Red,
                fontSize = 28.sp
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 2.dp, color = Color.Black)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TOTAL INVESTIDO", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = "R$ ${String.format("%.2f", totalInvested)}", 
                        color = Color.Blue, 
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
                
                if (monthlyBalance > 0) {
                    Button(
                        onClick = onInvestClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                    ) {
                        Text("INVESTIR SOBRA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: () -> Unit, onEdit: (Transaction) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onEdit(transaction) },
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = transaction.category.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp).padding(end = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = transaction.category.displayName, fontSize = 12.sp, color = Color.Gray)
                val color = when(transaction.type) {
                    TransactionType.INCOME -> Color.Green
                    TransactionType.EXPENSE -> Color.Red
                    TransactionType.INVESTMENT -> Color.Cyan
                    TransactionType.WITHDRAWAL -> Color(0xFFFFA500)
                }
                OutlinedText(
                    text = "R$ ${String.format("%.2f", transaction.amount)}",
                    fillColor = color,
                    fontSize = 18.sp
                )
            }
            IconButton(onClick = { onEdit(transaction) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var title by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var category by remember { mutableStateOf(transaction.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Lançamento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                TextField(
                    value = amount, 
                    onValueChange = { amount = it }, 
                    label = { Text("Valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Text("Categoria:", style = MaterialTheme.typography.labelSmall)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Category.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.displayName, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val value = amount.toDoubleOrNull() ?: transaction.amount
                onConfirm(transaction.copy(title = title, amount = value, category = category))
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun OutlinedText(
    text: String,
    fillColor: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Box {
        val outlineOffset = 1.dp
        Text(text = text, color = Color.Black, fontSize = fontSize, fontWeight = fontWeight, modifier = Modifier.offset(outlineOffset, outlineOffset))
        Text(text = text, color = Color.Black, fontSize = fontSize, fontWeight = fontWeight, modifier = Modifier.offset(-outlineOffset, -outlineOffset))
        Text(text = text, color = Color.Black, fontSize = fontSize, fontWeight = fontWeight, modifier = Modifier.offset(outlineOffset, -outlineOffset))
        Text(text = text, color = Color.Black, fontSize = fontSize, fontWeight = fontWeight, modifier = Modifier.offset(-outlineOffset, outlineOffset))
        Text(text = text, color = fillColor, fontSize = fontSize, fontWeight = fontWeight)
    }
}

@Composable
fun SectionHeader(title: String, color: Color, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = topPadding)) {
            Text(
                text = title,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            HorizontalDivider(thickness = 4.dp, color = color)
        }
    }
}
