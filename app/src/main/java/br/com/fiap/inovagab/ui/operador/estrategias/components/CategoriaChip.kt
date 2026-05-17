package br.com.fiap.inovagab.ui.operador.estrategias.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoriaChip(
    texto: String
) {

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE7F0FF)
    ) {

        Text(
            text = texto,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            color = Color(0xFF003DA5),
            fontSize = 12.sp
        )
    }
}