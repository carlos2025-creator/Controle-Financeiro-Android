// 9º arquivo criado
package com.example.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.controlefinanceiro.ui.AddTransactionScreen
import com.example.controlefinanceiro.ui.ReportsScreen
import com.example.controlefinanceiro.ui.TransactionListScreen
import com.example.controlefinanceiro.ui.TransactionViewModel
import com.example.controlefinanceiro.ui.theme.ControleFinanceiroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleFinanceiroTheme {
                FinanceApp()
            }
        }
    }
}

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val viewModel: TransactionViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TransactionListScreen(
                viewModel = viewModel,
                onAddTransactionClick = { navController.navigate("add") },
                onReportsClick = { navController.navigate("reports") }
            )
        }
        composable("add") {
            AddTransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("reports") {
            ReportsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
