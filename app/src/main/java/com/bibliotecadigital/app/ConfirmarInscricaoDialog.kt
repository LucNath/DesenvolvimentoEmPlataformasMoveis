package com.bibliotecadigital.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Paleta de Cores do Projeto (Dark Theme)
private val BgDark = Color(0xFF0D1117)
private val CardDark = Color(0xFF161B22)
private val AccentOrange = Color(0xFFF4A023)
private val TextPrimary = Color(0xFFE6EDF3)
private val TextSecondary = Color(0xFF8B949E)
private val BorderColor = Color(0xFF30363D)

@Composable
fun ConfirmarInscricaoDialog(
    nomeEvento: String,
    data: String,
    horario: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Dialog(onDismissRequest = onCancelar) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Confirmar Inscrição",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Você deseja se inscrever no evento abaixo?",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detalhes do Evento
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDark, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = nomeEvento,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Data: $data",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Horário: $horario",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botões
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onConfirmar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Confirmar",
                            color = BgDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun ConfirmarInscricaoDialogPreview() {
    ConfirmarInscricaoDialog(
        nomeEvento = "Workshop de Android com Compose",
        data = "25 de Outubro, 2024",
        horario = "19:00 - 21:00",
        onConfirmar = {},
        onCancelar = {}
    )
}
