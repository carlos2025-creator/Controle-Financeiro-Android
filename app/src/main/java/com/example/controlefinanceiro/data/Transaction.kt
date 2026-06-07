// 13º arquivo criado
package com.example.controlefinanceiro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category = Category.OTHERS,
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    INVESTMENT,
    WITHDRAWAL
}
