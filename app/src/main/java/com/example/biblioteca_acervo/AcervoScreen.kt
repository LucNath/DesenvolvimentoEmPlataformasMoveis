package com.example.biblioteca_acervo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Paleta de cores ──────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF0D1117)
private val CardDark      = Color(0xFF161B22)
private val CardMedium    = Color(0xFF1C2333)
private val AccentOrange  = Color(0xFFF4A023)
private val AccentGreen   = Color(0xFF2EA84F)
private val AccentRed     = Color(0xFFCF4242)
private val TextPrimary   = Color(0xFFE6EDF3)
private val TextSecondary = Color(0xFF8B949E)
private val ChipBorder    = Color(0xFF30363D)
private val NavSelected   = Color(0xFFF4A023)

// ── Modelos de dados ──────────────────────────────────────────────────────────
data class Categoria(
    val nome: String,
    val qtdObras: Int,
    val icon: ImageVector,
    val iconTint: Color = TextSecondary
)

data class Livro(
    val titulo: String,
    val autor: String,
    val status: LivroStatus,
    val corCapa: Color
)

enum class LivroStatus(val label: String, val color: Color) {
    DISPONIVEL("Disponível", AccentGreen),
    POUCOS("2 disponíveis", AccentOrange),
    INDISPONIVEL("Indisponível", AccentRed)
}

// ── Tela principal ────────────────────────────────────────────────────────────
@Composable
fun AcervoScreen() {
    val categoriasFiltro = listOf("Todos", "Literatura", "Direito", "Engenharia", "Tecnologia")
    var filtroSelecionado by remember { mutableStateOf("Todos") }

    val categorias = listOf(
        Categoria("Direito",     128, Icons.Default.Balance,       Color(0xFF8B949E)),
        Categoria("Tecnologia",  94,  Icons.Default.Computer,      Color(0xFF8B949E)),
        Categoria("Engenharia",  76,  Icons.Default.Architecture,  Color(0xFF8B949E)),
        Categoria("Literatura",  211, Icons.Default.MenuBook,      Color(0xFF2EA84F))
    )

    val maisEmprestados = listOf(
        Livro("Código Limpo",    "Robert C. Martin", LivroStatus.DISPONIVEL,    Color(0xFF1B6CA8)),
        Livro("Cálculo Vol. 1", "James Stewart",    LivroStatus.POUCOS,        Color(0xFF8B2FC9)),
        Livro("Processo Penal", "Aury Lopes Jr.",   LivroStatus.INDISPONIVEL,  Color(0xFF2EA84F))
    )

    Scaffold(
        containerColor = BgDark,
        bottomBar = { BottomNavBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Cabeçalho
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📚", fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Acervo",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Campo de busca
            item {
                SearchBar()
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Chips de filtro
            item {
                FiltroChips(
                    opcoes = categoriasFiltro,
                    selecionado = filtroSelecionado,
                    onSelecionado = { filtroSelecionado = it }
                )
                Spacer(modifier = Modifier.height(22.dp))
            }

            // Seção Categorias
            item {
                SectionTitle("Categorias")
                Spacer(modifier = Modifier.height(12.dp))
                CategoriaGrid(categorias = categorias)
                Spacer(modifier = Modifier.height(22.dp))
            }

            // Seção Mais Emprestados
            item {
                SectionTitle("Mais Emprestados")
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Lista de livros
            items(maisEmprestados.size) { idx ->
                LivroCard(livro = maisEmprestados[idx])
                Spacer(modifier = Modifier.height(10.dp))
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// ── Componentes ───────────────────────────────────────────────────────────────

@Composable
private fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, ChipBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Buscar",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Título, autor ou ISBN...",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FiltroChips(
    opcoes: List<String>,
    selecionado: String,
    onSelecionado: (String) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opcoes.forEach { opcao ->
            val isSelected = opcao == selecionado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) AccentOrange else Color.Transparent)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) Color.Transparent else ChipBorder,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onSelecionado(opcao) }
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = opcao,
                    color = if (isSelected) Color.White else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CategoriaGrid(categorias: List<Categoria>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        categorias.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { categoria ->
                    CategoriaCard(
                        categoria = categoria,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Preenche coluna vazia se linha ímpar
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoriaCard(categoria: Categoria, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, ChipBorder, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = categoria.icon,
            contentDescription = categoria.nome,
            tint = categoria.iconTint,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = categoria.nome,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "${categoria.qtdObras} obras",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun LivroCard(livro: Livro) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, ChipBorder, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Capa do livro
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(livro.corCapa),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Informações
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = livro.titulo,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = livro.autor,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusBadge(status = livro.status)
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun StatusBadge(status: LivroStatus) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(status.color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = status.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Barra de navegação inferior ───────────────────────────────────────────────
@Composable
private fun BottomNavBar() {
    data class NavItem(val label: String, val icon: ImageVector, val selected: Boolean)

    val items = listOf(
        NavItem("Home",   Icons.Default.Home,           false),
        NavItem("Acervo", Icons.Default.LibraryBooks,   true),
        NavItem("Event.", Icons.Default.CalendarMonth,  false),
        NavItem("Notif.", Icons.Default.Notifications,  false),
        NavItem("Perfil", Icons.Default.Person,         false)
    )

    NavigationBar(
        containerColor = CardMedium,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = { },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(item.label, fontSize = 11.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavSelected,
                    selectedTextColor = NavSelected,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF0D1117, showSystemUi = true)
@Composable
fun AcervoScreenPreview() {
    MaterialTheme {
        AcervoScreen()
    }
}
