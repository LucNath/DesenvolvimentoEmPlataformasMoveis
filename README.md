# 📚 Biblioteca Digital — Unifor

Aplicativo mobile de biblioteca universitária desenvolvido com **Kotlin + Android Studio** para a disciplina de Desenvolvimento de Plataformas Móveis (T197-26) da Universidade de Fortaleza.

---

## 👥 Equipe

| Nome | GitHub |
|---|---|
| Marcos Gomes | [@marcosGomes](#) |
| Lucas Gomes | [@lucasGomes](#) |
| Maria Clara | [@mariaClara](#) |
| Pedro Costa | [@pedroCosta](#) |

---

## 📱 Sobre o Projeto

O **Biblioteca Digital** é um app Android que digitaliza e moderniza a experiência dos usuários da biblioteca da Unifor. Permite acesso ao catálogo de obras físicas e digitais, gerenciamento de empréstimos, leitura de livros digitais, eventos e clubes de leitura.

---

## 🛠️ Tecnologias

| Ferramenta | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Android Studio | IDE de desenvolvimento |
| Firebase Auth | Autenticação de usuários |
| Firebase Firestore | Banco de dados em nuvem |
| Navigation Component | Navegação entre telas |
| View Binding | Ligação de views |
| Material Design 3 | Componentes de UI |
| Scrum | Metodologia de desenvolvimento |

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)**:

```
app/
├── data/
│   ├── model/          # Data classes (UserProfile, Book, Event…)
│   ├── repository/     # Repositórios (Firebase, local)
│   └── remote/         # Serviços de API
├── ui/
│   ├── auth/           # Login, Cadastro, Recuperação de Senha
│   ├── home/           # Tela inicial
│   ├── collection/     # Acervo e Detalhes do Livro
│   ├── events/         # Eventos e Clubes de Leitura
│   ├── notifications/  # Notificações
│   └── profile/        # Perfil, Histórico, Metas, Multas
├── viewmodel/          # ViewModels de cada tela
└── utils/              # Extensions, helpers
```

---

## 🗂️ Backlog do Produto

Backlog gerenciado via **GitHub Projects** com as colunas: `Ready → In Progress → In Review → Done`.

### 🔐 Autenticação

| ID | Tela | Descrição | Prioridade |
|---|---|---|---|
| TASK-01 | Login | Tela de login com e-mail, senha e validações | 🔴 Alta |
| TASK-02 | Cadastro | Criação de conta com nome, e-mail institucional, matrícula e senha | 🔴 Alta |
| TASK-03 | Recuperação de Senha | Fluxo de redefinição de senha via e-mail | 🔴 Alta |

### 🏠 Navegação e Home

| ID | Tela | Descrição | Prioridade |
|---|---|---|---|
| TASK-04 | Home | Seção de empréstimos ativos com título, autor e data de devolução | 🔴 Alta |
| TASK-05 | Home | Seção de reservas com posição na fila de espera | 🔴 Alta |
| TASK-06 | Navegação | Barra de navegação inferior fixa (Home, Acervo, Eventos, Notif., Perfil) | 🔴 Alta |

### 📖 Acervo

| ID | Tela | Descrição | Prioridade |
|---|---|---|---|
| TASK-07 | Acervo | Listagem de obras por categoria com barra de busca | 🔴 Alta |
| TASK-08 | Detalhes do Livro | Informações completas da obra com ações de emprestar/reservar | 🔴 Alta |

### 📅 Eventos

| ID | Tela | Descrição | Prioridade |
|---|---|---|---|
| TASK-09 | Eventos | Calendário, listagem de eventos e gerenciamento de inscrições | 🔴 Alta |

### 🔔 Notificações

| ID | Tela | Descrição | Prioridade |
|---|---|---|---|
| TASK-10 | Notificações | Listagem de alertas com marcação de lidas | 🔴 Alta |

### 👤 Perfil

| ID | Tela | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-11 | Perfil | Tela base: avatar, stats, seções Minha Conta e Configurações | 🔴 Alta | ✅ Concluído |
| TASK-14 | Perfil | Popup Editar Perfil | 🟡 Média | 🔲 Pendente |
| TASK-15 | Perfil | Tela de Histórico de Leituras | 🟡 Média | 🔲 Pendente |
| TASK-16 | Perfil | Tela de Metas de Leitura | 🟡 Média | 🔲 Pendente |
| TASK-17 | Perfil | Tela de Multas e Pagamentos | 🟡 Média | 🔲 Pendente |

### ⚙️ Configurações

| ID | Tela | Descrição | Prioridade |
|---|---|---|---|
| TASK-12 | Configurações | Tela com opções de conta | 🔴 Alta |
| TASK-13 | Configurações | Popup Alterar Senha com validações | 🔴 Alta |
| TASK-18 | Configurações | Personalização de interface (fonte, tema, rotação) | 🟢 Baixa |

---

## ✅ Implementações Concluídas

### TASK-11 — Tela de Perfil (base)

**Arquivos criados/modificados:**

```
app/src/main/java/.../
├── model/UserProfile.kt
└── ui/profile/ProfileFragment.kt

app/src/main/res/
├── layout/fragment_profile.xml
├── layout/item_stat_card.xml
├── layout/item_menu_row.xml
├── drawable/bg_avatar.xml
├── drawable/bg_card.xml
├── drawable/bg_button_outline_orange.xml
└── values/colors.xml  (cores adicionadas)
```

**O que foi implementado:**
- Avatar circular com iniciais do usuário geradas dinamicamente
- Nome e curso do usuário abaixo do avatar
- Botão "Editar Perfil" com borda laranja (abre TASK-14)
- Cards de estatísticas: Emprestados / Devolvidos / Reservas
- Seção "Minha Conta" com linhas para Histórico, Metas e Multas
- Seção "Configurações" com Alterar Senha e Sair da conta
- "Sair da conta" em vermelho sem chevron
- Dialog de confirmação ao sair
- Listeners preparados como ganchos para as tasks seguintes

**Design reference:**

> Tema escuro azul-marinho (`#0F1B2D`), avatar laranja (`#E8832A`), cards com fundo `#1A2740`, tipografia branca.

---

## 🎨 Design System

O app segue o protótipo de alta fidelidade com o seguinte sistema de cores:

| Token | Hex | Uso |
|---|---|---|
| `bg_screen` | `#0F1B2D` | Fundo geral das telas |
| `bg_card` | `#1A2740` | Fundo de cards e seções |
| `brand_orange` | `#E8832A` | Avatar, ícones, botões de ação |
| `text_primary` | `#FFFFFF` | Textos principais |
| `text_secondary` | `#8A9BB5` | Textos secundários e labels |
| `text_section` | `#5A6E88` | Labels de seção ("MINHA CONTA") |
| `error_red` | `#E05252` | Ações destrutivas ("Sair da conta") |

---

## 🚀 Como rodar o projeto

### Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 34
- Dispositivo ou emulador com Android 8.0+ (API 26)

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/LucNath/biblioteca-digital.git

# 2. Abra no Android Studio
# File → Open → selecione a pasta do projeto

# 3. Aguarde o Gradle sync

# 4. Configure o Firebase
# Adicione o arquivo google-services.json em app/

# 5. Rode o app
# Shift + F10 ou botão Run
```

### Configuração do Firebase

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Crie um projeto ou use o existente
3. Adicione um app Android com o package name do projeto
4. Baixe o `google-services.json` e coloque em `app/`
5. Ative **Authentication** (e-mail/senha) e **Firestore**

---

## 📐 Metodologia — Scrum

O desenvolvimento segue ciclos de Sprint gerenciados pelo GitHub Projects:

```
Backlog do Produto
      ↓
Backlog da Sprint  ←──────────────────┐
      ↓                               │
Desenvolvimento                       │  Novo ciclo
      ↓                               │
Revisão de Código                     │
      ↓                               │
Entrega Incremental                   │
      ↓                               │
Retrospectiva da Sprint ──────────────┘
```

**Prioridades:**
- 🔴 **Alta** — Essencial para o MVP, deve ser implementado obrigatoriamente
- 🟡 **Média** — Importante, implementar se o tempo permitir
- 🟢 **Baixa** — Desejável, não afeta a funcionalidade principal

---

## 📁 Estrutura de Branches

```
main          → código estável, aprovado em review
develop       → integração das features
feature/      → uma branch por task (ex: feature/task-11-perfil)
fix/          → correções de bugs
```

**Padrão de commit:**
```
feat(perfil): implementa tela base do perfil TASK-11
fix(login): corrige validação de e-mail vazio TASK-01
```

---

## 👤 Personas

**Lucas Mendes** — Usuário da biblioteca  
21 anos, estudante de Direito na Unifor. Usa a biblioteca para pesquisas acadêmicas e leituras complementares. Precisa de um app ágil para encontrar, reservar e organizar leituras.

**Administrador do Sistema**  
Responsável por cadastrar obras, acompanhar empréstimos e manter o acervo atualizado e organizado.

---

## 📄 Licença

Projeto acadêmico — Universidade de Fortaleza (Unifor) · 2026
