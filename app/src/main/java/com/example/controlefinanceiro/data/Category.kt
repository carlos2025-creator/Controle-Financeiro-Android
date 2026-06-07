package com.example.controlefinanceiro.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(val displayName: String, val icon: ImageVector) {
    FOOD("Alimentação", Icons.Default.ShoppingCart),
    TRANSPORT("Transporte", Icons.Default.Build),
    LIVING("Moradia", Icons.Default.Home),
    HEALTH("Saúde", Icons.Default.Favorite),
    ENTERTAINMENT("Lazer", Icons.Default.Star),
    SALARY("Salário", Icons.Default.AccountBox),
    INVESTMENT("Investimento", Icons.Default.Refresh),
    OTHERS("Outros", Icons.Default.List)
}
