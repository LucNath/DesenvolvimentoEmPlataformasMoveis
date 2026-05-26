package com.bibliotecadigital.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
private val TextPrimary = Color(0xFFE6EDF3)
private val TextSecondary = Color(0xFF8B949E)
private val BorderColor = Color(0xFF30363D)
private val RedDestructive = Color(0xFFCF4242)

@Composable
fun CancelarInscricaoDialog(
    nomeEvento: String,
    onCancelarInscricao: () -> Unit,
    onSair: () -> Unit
) {
    Dialog(onDismissRequest = onSair) {
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
                // Ícone de Alerta
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alerta",
                    tint = RedDestructive,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cancelar Inscrição?",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Você realmente deseja cancelar sua inscrição no evento:",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = nomeEvento,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botões
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onCancelarInscricao,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedDestructive),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Cancelar inscrição",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onSair,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Sair",
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
fun CancelarInscricaoDialogPreview() {
    CancelarInscricaoDialog(
        nomeEvento = "Workshop de Android com Compose",
        onCancelarInscricao = {},
        onSair = {}
    )
}
