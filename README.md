# 📚 Biblioteca Digital — Unifor

> Aplicativo mobile de biblioteca universitária desenvolvido com **Kotlin + Android Studio** para a disciplina de Desenvolvimento de Plataformas Móveis (T197-26) — Universidade de Fortaleza.

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-blue)
![Versão](https://img.shields.io/badge/versão-0.2.0--alpha-blue)
![Android](https://img.shields.io/badge/Android-API%2026%2B-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-blue)

---

## 👥 Equipe de Desenvolvimento

| Nome | Função | GitHub |
|---|---|---|
| Lucas Nathan | Tech Lead / Android | [@LucNath](https://github.com/LucNath) |
| Marcos Gomes | Android Developer | [@marcbaraGomes](https://github.com/marcbaraGomes) |
| Maria Clara | Android Developer | [@MClaraLDantas](https://github.com/MClaraLDantas) |
| Pedro Costa | Android Developer | [@pedrocosta-7](https://github.com/pedrocosta-7) |

---

## 📱 Sobre o Projeto

O **Biblioteca Digital** digitaliza e moderniza a experiência dos usuários da biblioteca da Unifor, oferecendo:

- 🔍 **Catálogo inteligente** com busca por título, autor e ISBN
- 📋 **Gerenciamento de empréstimos** com renovação e lembretes automáticos
- 🔖 **Sistema de reservas** com fila de espera em tempo real
- 📅 **Eventos e clubes de leitura** com inscrições integradas
- 🔔 **Notificações push** para prazos, reservas disponíveis e multas
- 👤 **Perfil personalizado** com histórico, metas de leitura e conquistas
- 🔐 **Painel administrativo** para gestão do acervo e usuários

---

## 🛠️ Stack Tecnológico

| Tecnologia | Uso |
|---|---|
| **Kotlin 1.9+** | Linguagem principal |
| **Android Studio Hedgehog+** | IDE de desenvolvimento |
| **Firebase Auth** | Autenticação e-mail/senha |
| **Firebase Firestore** | Banco de dados NoSQL em nuvem |
| **Firebase Cloud Storage** | Imagens de capas e avatares |
| **Firebase Cloud Messaging (FCM)** | Notificações push |
| **Firebase Cloud Functions** | Automação: multas, lembretes |
| **Navigation Component** | Roteamento entre fragmentos |
| **View Binding** | Referências type-safe de views |
| **Material Design 3** | Componentes e temas de UI |
| **Coroutines + Flow** | Programação assíncrona |

**Metodologia:** Scrum · Sprints de 2 semanas · GitHub Projects

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)**:

```
app/src/main/java/com/bibliotecadigital/app/
│
├── data/
│   ├── model/                    # Data classes
│   │   ├── UserProfile.kt
│   │   ├── Book.kt
│   │   ├── Loan.kt
│   │   ├── Reservation.kt
│   │   ├── Event.kt
│   │   ├── Fine.kt
│   │   └── Review.kt
│   │
│   └── repository/               # Camada de dados (Firebase)
│       ├── AuthRepository.kt
│       ├── UserRepository.kt
│       ├── BookRepository.kt
│       ├── LoanRepository.kt
│       ├── ReservationRepository.kt
│       ├── EventRepository.kt
│       ├── FineRepository.kt
│       └── ReviewRepository.kt
│
├── ui/
│   ├── auth/                     # Login, Cadastro, Recuperação
│   ├── home/                     # Tela inicial
│   ├── collection/               # Acervo e Detalhes do Livro
│   ├── events/                   # Eventos e Clubes
│   ├── notifications/            # Notificações
│   └── profile/                  # Perfil, Histórico, Metas, Multas
│
├── viewmodel/                    # Lógica de apresentação
└── utils/                        # Extensions, helpers, validators
```

---

## 🗄️ Banco de Dados — Firestore

**Região:** `southamerica-east1` (São Paulo)

| Coleção | Campos principais |
|---|---|
| `users/` | uid, name, email, matricula, course, role, fcmToken |
| `books/` | title, author, isbn, category, totalCopies, available, coverUrl |
| `loans/` | userId, bookId, loanDate, dueDate, status, renewalCount |
| `reservations/` | userId, bookId, queuePosition, status |
| `fines/` | userId, loanId, amount, daysLate, status |
| `events/` | title, date, facilitator, totalSlots, usedSlots, participants[] |
| `reviews/` | userId, bookId, rating, comment |
| `notifications/` | userId, title, body, type, read |

---

## 📊 Backlog Completo

Gerenciado via **GitHub Projects**: `Ready → In Progress → In Review → Done`

### 🔐 Autenticação

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-01 | Login | 🔴 Alta | 🔲 |
| TASK-02 | Cadastro | 🔴 Alta | 🔲 |
| TASK-03 | Recuperação de Senha | 🔴 Alta | 🔲 |

### 🏠 Home e Navegação

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-04 | Home — Empréstimos Ativos | 🔴 Alta | 🔲 |
| TASK-05 | Home — Reservas | 🔴 Alta | 🔲 |
| TASK-06 | Bottom Navigation Bar | 🔴 Alta | 🔲 |

### 📖 Acervo

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-07 | Acervo — Listagem e Categorias | 🔴 Alta | 🔲 |
| TASK-08 | Detalhes do Livro | 🔴 Alta | 🔲 |
| TASK-20 | Categoria Filtrada | 🔴 Alta | 🔲 |

### 📅 Eventos

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-09 | Eventos e Clubes de Leitura | 🔴 Alta | 🔲 |
| TASK-29 | Pop-up — Confirmar Inscrição | 🔴 Alta | 🔲 |
| TASK-30 | Pop-up — Cancelar Inscrição | 🔴 Alta | 🔲 |

### 🔔 Notificações

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-10 | Tela de Notificações | 🔴 Alta | 🔲 |
| TASK-28 | Config. Notificações Push | 🟢 Baixa | 🔲 |

### 👤 Perfil

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-11 | Tela de Perfil (Base) | 🔴 Alta | ✅ Concluído |
| TASK-14 | Popup — Editar Perfil | 🟡 Média | 🔲 |
| TASK-15 | Histórico de Leituras | 🟡 Média | 🔲 |
| TASK-16 | Metas de Leitura | 🟡 Média | 🔲 |
| TASK-17 | Multas e Pagamentos | 🟡 Média | 🔲 |
| TASK-27 | Multas — Tela Detalhada | 🟡 Média | 🔲 |

### ⚙️ Configurações

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-12 | Tela de Configurações | 🔴 Alta | 🔲 |
| TASK-13 | Popup — Alterar Senha | 🔴 Alta | 🔲 |
| TASK-18 | Personalização da Interface | 🟢 Baixa | 🔲 |

### 🎬 Fluxos, Pop-ups e Estados

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-19 | Splash Screen + Onboarding | 🔴 Alta | 🔲 |
| TASK-21 | Leitura Digital in-app | 🔴 Alta | 🔲 |
| TASK-22 | Renovação de Empréstimo | 🔴 Alta | 🔲 |
| TASK-23 | Reviews e Avaliações | 🔴 Alta | 🔲 |
| TASK-31 | Sucesso — Empréstimo Confirmado | 🔴 Alta | 🔲 |
| TASK-32 | Sucesso — Reserva Confirmada | 🔴 Alta | 🔲 |
| TASK-33 | Popup — Adicionar Meta de Leitura | 🟡 Média | 🔲 |
| TASK-34 | Erro — Email Duplicado no Cadastro | 🟡 Média | 🔲 |
| TASK-35 | Erro — Senha Fraca no Cadastro | 🟡 Média | 🔲 |
| TASK-36 | Erros — Alterar Senha (3 estados) | 🟡 Média | 🔲 |
| TASK-37 | Estado Vazio — Home | 🟡 Média | 🔲 |
| TASK-38 | Estado Vazio — Acervo sem Resultados | 🟡 Média | 🔲 |
| TASK-39 | Suporte e Feedback | 🟡 Média | 🔲 |
| TASK-40 | Tela Offline / Sem Conexão | 🟢 Baixa | 🔲 |

### 👔 Painel Administrativo

| ID | Tela | Prioridade | Status |
|---|---|---|---|
| TASK-24 | Dashboard Admin | 🟡 Média | 🔲 |
| TASK-25 | Cadastro de Obras | 🟡 Média | 🔲 |
| TASK-26 | Gestão de Empréstimos | 🟡 Média | 🔲 |

### 🔥 Backend — Firebase

| ID | Camada | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-BE01 | Auth | Configurar Firebase Auth no projeto | 🔴 Alta | 🔲 |
| TASK-BE02 | Auth | Integrar Auth com Login e Cadastro | 🔴 Alta | 🔲 |
| TASK-BE03 | Firestore | Configurar Firestore e Regras de Segurança | 🔴 Alta | 🔲 |
| TASK-BE04 | Firestore | CRUD Usuários — `users/` | 🔴 Alta | 🔲 |
| TASK-BE05 | Firestore | CRUD Livros — `books/` | 🔴 Alta | 🔲 |
| TASK-BE06 | Firestore | CRUD Empréstimos — `loans/` | 🔴 Alta | 🔲 |
| TASK-BE07 | Firestore | CRUD Reservas — `reservations/` | 🟡 Média | 🔲 |
| TASK-BE08 | Functions | Cloud Function — Multas automáticas | 🟡 Média | 🔲 |
| TASK-BE09 | Functions | Cloud Function — Lembretes de vencimento | 🟡 Média | 🔲 |
| TASK-BE10 | FCM | Push Notifications no app | 🟡 Média | 🔲 |
| TASK-BE11 | Storage | Upload de capas de livros | 🟡 Média | 🔲 |
| TASK-BE12 | Storage | Upload de avatar do usuário | 🟢 Baixa | 🔲 |
| TASK-BE13 | Firestore | CRUD Eventos — `events/` | 🟡 Média | 🔲 |
| TASK-BE14 | Firestore | CRUD Reviews — `reviews/` | 🟢 Baixa | 🔲 |

---

## ✅ Implementações Concluídas

### TASK-11 — Tela de Perfil (Base)

**Responsável:** [@LucNath](https://github.com/LucNath)

**Arquivos:**
```
data/model/UserProfile.kt
ui/profile/ProfileFragment.kt
res/layout/fragment_profile.xml
res/layout/item_stat_card.xml
res/layout/item_menu_row.xml
res/drawable/bg_avatar.xml · bg_card.xml · bg_button_outline.xml
res/values/colors.xml (atualizado)
```

**Funcionalidades:**
- ✅ Avatar com iniciais geradas dinamicamente
- ✅ Stats: Emprestados / Devolvidos / Reservas
- ✅ Seção "Minha Conta": Histórico, Metas, Multas
- ✅ Seção "Configurações": Alterar Senha, Sair
- ✅ Dialog de confirmação ao fazer logout
- ✅ Hooks de navegação para tasks futuras

---

## 🎨 Design System

**Paleta de Cores**

| Token | Hex | Uso |
|---|---|---|
| `blue_royal` | `#1A56DB` | Primária — botões, headers, destaques |
| `blue_deep` | `#1E429F` | Estados pressed/hover |
| `blue_soft` | `#76A9FA` | Ícones secundários, links |
| `blue_ice` | `#EBF5FF` | Cards de destaque, badges |
| `bg_light` | `#F3F4F6` | Fundo geral das telas |
| `surface` | `#FFFFFF` | Cards, modais, inputs |
| `bg_dark` | `#111827` | Fundo modo escuro |
| `surface_dark` | `#1F2937` | Cards no modo escuro |
| `border_dark` | `#374151` | Bordas no modo escuro |
| `text_primary` | `#111827` | Títulos e textos principais |
| `text_secondary` | `#6B7280` | Subtítulos, metadados |
| `text_primary_dark` | `#F9FAFB` | Texto em fundo escuro |
| `text_secondary_dark` | `#9CA3AF` | Texto secundário em fundo escuro |
| `success` | `#16A34A` | Disponível, confirmações |
| `error` | `#EF4444` | Erros, indisponível, logout |
| `warning` | `#D97706` | Pendente, atenção |
| `star` | `#F59E0B` | Avaliações com estrelas |
| `divider` | `#E5E7EB` | Separadores e bordas |

**Figma:** [Biblioteca Digital — Protótipo Alta Fidelidade](https://www.figma.com/design/iQD4toeJpH64uwODgtTorZ/)

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17+
- Android SDK 34
- Android 8.0+ (API 26) no dispositivo/emulador

### Passo a Passo

```bash
# 1. Clone o repositório
git clone https://github.com/LucNath/DesenvolvimentoEmPlataformasMoveis.git
cd DesenvolvimentoEmPlataformasMoveis
```

**2. Abra no Android Studio:** `File → Open → selecione a pasta do projeto`

**3. Configure o Firebase:**
1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Adicione app Android com package: `com.bibliotecadigital.app`
3. Baixe `google-services.json` e cole em `app/`
4. Ative: **Authentication** (e-mail/senha) + **Firestore** + **Storage** + **FCM**

**4. Dependências — `app/build.gradle.kts`:**

```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
```

```bash
# 5. Sincronize e execute
# Sync Now → Shift + F10
```

### Solução de Problemas

| Erro | Solução |
|---|---|
| `google-services plugin not found` | Verifique se `google-services.json` está em `app/` |
| `Gradle sync failed` | `Build → Clean Project → Rebuild Project` |
| `PERMISSION_DENIED` | Atualize as regras de segurança no Firestore Console |
| `fatal: 'origin' does not exist` | `git remote add origin <URL_DO_REPO>` |
| `Merge conflict` | `git merge --abort` depois `git push --force` |

---

## 📐 Metodologia — Scrum

```
Backlog do Produto → (Planning) → Backlog da Sprint
      ↓
Desenvolvimento → Revisão de Código
      ↓
Entrega Incremental → (Demo) → Retrospectiva → Novo ciclo
```

| Ícone | Prioridade | Critério |
|---|---|---|
| 🔴 | Alta | MVP — implementação obrigatória |
| 🟡 | Média | Importante — implementar se o tempo permitir |
| 🟢 | Baixa | Desejável — não afeta a funcionalidade principal |

---

## 📁 Branches e Commits

```
main      → código estável / produção
develop   → integração de features
feature/  → feature/task-11-perfil
fix/      → fix/login-validacao-email
```

```bash
# Exemplos de commit
feat(perfil): implementa tela base - TASK-11
fix(login): corrige validação de email - TASK-01
feat(backend): configura Firebase Auth - TASK-BE01
docs(readme): atualiza backlog com tasks de backend
```

---

## 🗺️ Roadmap

### Sprint 1 — Auth + Home
- [ ] Login, Cadastro, Recuperação (TASK-01, 02, 03)
- [ ] Home: Empréstimos e Reservas (TASK-04, 05, 06)
- [ ] Firebase Auth + Firestore setup (TASK-BE01, BE02, BE03)

### Sprint 2 — Acervo + Eventos + Backend
- [ ] Acervo e Detalhes (TASK-07, 08, 20)
- [ ] Eventos com inscrições (TASK-09, 29, 30)
- [ ] CRUD: users, books, loans (TASK-BE04, BE05, BE06)

### Sprint 3 — Perfil + Notificações
- [ ] Perfil completo: Histórico, Metas, Multas (TASK-14–17)
- [ ] Notificações push FCM (TASK-10, 28, TASK-BE10)
- [ ] Cloud Functions: multas e lembretes (TASK-BE08, BE09)

### Sprint 4 — Polimento + Admin
- [ ] Estados, pop-ups e fluxos de sucesso (TASK-29–40)
- [ ] Painel admin (TASK-24, 25, 26)
- [ ] Reviews e Storage (TASK-23, TASK-BE11–14)

---

## 📄 Licença

Projeto Acadêmico — Universidade de Fortaleza (Unifor)
Disciplina: Desenvolvimento de Plataformas Móveis (T197-26) · 2026

---

**Versão:** 0.2.0-alpha · **Última atualização:** Maio 2026 · **Status:** 🚧 Em desenvolvimento
