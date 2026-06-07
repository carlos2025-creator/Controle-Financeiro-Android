// 18º arquivo criado
package com.example.controlefinanceiro.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlefinanceiro.data.AppDatabase
import com.example.controlefinanceiro.data.Category
import com.example.controlefinanceiro.data.Transaction
import com.example.controlefinanceiro.data.TransactionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    val allTransactions: StateFlow<List<Transaction>> = transactionDao.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        allTransactions, _selectedMonth, _selectedYear
    ) { list, month, year ->
        list.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    val monthlyBalance: StateFlow<Double> = filteredTransactions.map { list ->
        list.sumOf { 
            when (it.type) {
                TransactionType.INCOME -> it.amount
                TransactionType.WITHDRAWAL -> it.amount
                TransactionType.EXPENSE -> -it.amount
                TransactionType.INVESTMENT -> -it.amount
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = 0.0
    )

    val totalInvested: StateFlow<Double> = allTransactions.map { list ->
        list.sumOf {
            when (it.type) {
                TransactionType.INVESTMENT -> it.amount
                TransactionType.WITHDRAWAL -> -it.amount
                else -> 0.0
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = 0.0
    )

    fun changeMonth(month: Int) {
        _selectedMonth.value = month
    }

    fun changeYear(year: Int) {
        _selectedYear.value = year
    }

    fun nextMonth() {
        if (_selectedMonth.value == 11) {
            _selectedMonth.value = 0
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
    }

    fun previousMonth() {
        if (_selectedMonth.value == 0) {
            _selectedMonth.value = 11
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
    }

    fun addTransaction(title: String, amount: Double, type: TransactionType, category: Category = Category.OTHERS) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, _selectedYear.value)
                set(Calendar.MONTH, _selectedMonth.value)
                val today = Calendar.getInstance()
                if (_selectedYear.value == today.get(Calendar.YEAR) && _selectedMonth.value == today.get(Calendar.MONTH)) {
                    set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH))
                } else {
                    set(Calendar.DAY_OF_MONTH, 1)
                }
            }

            transactionDao.insertTransaction(
                Transaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    date = calendar.timeInMillis
                )
            )
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transaction)
        }
    }

    fun investSurplus() {
        val surplus = monthlyBalance.value
        if (surplus > 0) {
            addTransaction("Aporte do Mês", surplus, TransactionType.INVESTMENT, Category.INVESTMENT)
        }
    }
}
