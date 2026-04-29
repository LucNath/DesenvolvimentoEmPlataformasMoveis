# 📚 Biblioteca Digital — Unifor

Vou analisar o README atual e propor melhorias de estrutura, clareza e completude.Analisando o README.md que você compartilhou, aqui está uma versão **melhorada e otimizada** do documento:

```markdown
# 📚 Biblioteca Digital — Unifor

Aplicativo mobile de biblioteca universitária desenvolvido com **Kotlin + Android Studio** para a disciplina de Desenvolvimento de Plataformas Móveis (T197-26) da Universidade de Fortaleza.

---

## 👥 Equipe de Desenvolvimento

| Nome | GitHub |
|---|---|
| Marcos Gomes | [@marcosGomes](https://github.com/marcosGomes) |
| Lucas Gomes | [@lucasGomes](https://github.com/lucasGomes) |
| Maria Clara | [@mariaClara](https://github.com/mariaClara) |
| Pedro Costa | [@pedroCosta](https://github.com/pedroCosta) |

---

## 📱 Sobre o Projeto

O **Biblioteca Digital** é um app Android que digitaliza e moderniza a experiência dos usuários da biblioteca da Unifor. Ele oferece:

- ✅ **Acesso ao catálogo** de obras físicas e digitais com busca inteligente
- ✅ **Gerenciamento de empréstimos** com lembretes de devolução
- ✅ **Sistema de reservas** com fila de espera
- ✅ **Eventos e clubes de leitura** organizados por interesse
- ✅ **Notificações em tempo real** para prazos e eventos
- ✅ **Perfil do usuário** com histórico de leitura e metas personalizadas

**Objetivo:** Melhorar a usabilidade e acessibilidade da biblioteca para mais de 10 mil alunos da Unifor.

---

## 🛠️ Stack Tecnológico

| Tecnologia | Versão | Uso |
|---|---|---|
| **Kotlin** | 1.9+ | Linguagem principal |
| **Android** | API 26+ (8.0+) | Plataforma alvo |
| **Android Studio** | Hedgehog 2023.1.1+ | IDE |
| **Firebase Auth** | Latest | Autenticação (e-mail/senha) |
| **Firebase Firestore** | Latest | Banco de dados em nuvem (NoSQL) |
| **Navigation Component** | Latest | Roteamento entre fragmentos |
| **View Binding** | - | Type-safe view references |
| **Material Design 3** | Latest | Componentes e temas de UI |

**Metodologia:** Scrum com sprints de 2 semanas

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com separação clara de responsabilidades:

```
app/src/main/java/com/unifor/bibliotecadigital/
│
├── data/
│   ├── model/                    # Data classes
│   │   ├── UserProfile.kt        # Perfil do usuário
│   │   ├── Book.kt               # Informações do livro
│   │   ├── Loan.kt               # Empréstimo
│   │   ├── Reservation.kt        # Reserva
│   │   └── Event.kt              # Evento/clube de leitura
│   │
│   ├── repository/               # Camada de dados
│   │   ├── UserRepository.kt     # Operações de usuário
│   │   ├── BookRepository.kt     # Operações de catálogo
│   │   ├── LoanRepository.kt     # Empréstimos
│   │   └── EventRepository.kt    # Eventos
│   │
│   └── remote/                   # Serviços de API
│       └── FirebaseService.kt    # Integração Firebase
│
├── ui/
│   ├── auth/                     # Fluxo de autenticação
│   │   ├── LoginFragment.kt
│   │   ├── RegisterFragment.kt
│   │   └── ForgotPasswordFragment.kt
│   │
│   ├── home/                     # Tela inicial
│   │   ├── HomeFragment.kt
│   │   ├── HomeViewModel.kt
│   │   └── fragment_home.xml
│   │
│   ├── collection/               # Acervo e detalhes
│   │   ├── CollectionFragment.kt
│   │   ├── BookDetailFragment.kt
│   │   └── (layouts)
│   │
│   ├── events/                   # Eventos e clubes
│   │   ├── EventsFragment.kt
│   │   ├── EventDetailFragment.kt
│   │   └── (layouts)
│   │
│   ├── notifications/            # Centro de notificações
│   │   ├── NotificationsFragment.kt
│   │   └── (layouts)
│   │
│   ├── profile/                  # Perfil do usuário
│   │   ├── ProfileFragment.kt
│   │   ├── EditProfileFragment.kt
│   │   ├── ReadingHistoryFragment.kt
│   │   ├── ReadingGoalsFragment.kt
│   │   ├── FeesFragment.kt
│   │   └── (layouts)
│   │
│   └── common/                   # Componentes reutilizáveis
│       ├── MainActivity.kt
│       ├── BaseFragment.kt
│       └── (shared layouts)
│
├── viewmodel/                    # ViewModels (lógica de apresentação)
│   ├── AuthViewModel.kt
│   ├── HomeViewModel.kt
│   ├── ProfileViewModel.kt
│   ├── BookViewModel.kt
│   └── EventViewModel.kt
│
└── utils/
    ├── extensions/               # Extension functions
    ├── constants/                # Constantes da app
    ├── helpers/                  # Funções auxiliares
    └── validators/               # Validações
```

---

## 📊 Backlog do Produto

Gerenciado via **GitHub Projects** com workflow: `Ready → In Progress → In Review → Done`

### 🔐 **Autenticação** — Sprint 1

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-01 | Login | Tela com validação de e-mail, senha e recuperação de sessão | 🔴 **Alta** | 🔲 |
| TASK-02 | Cadastro | Criar conta com nome, e-mail institucional, matrícula e senha com regras | 🔴 **Alta** | 🔲 |
| TASK-03 | Recuperação de Senha | Fluxo de redefinição via e-mail com token temporário | 🔴 **Alta** | 🔲 |

### 🏠 **Home** — Sprint 1

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-04 | Empréstimos Ativos | Listar empréstimos com autor, título e data de devolução | 🔴 **Alta** | 🔲 |
| TASK-05 | Reservas | Mostrar posição na fila de espera com status | 🔴 **Alta** | 🔲 |
| TASK-06 | Bottom Navigation | Barra fixa (Home, Acervo, Eventos, Notificações, Perfil) | 🔴 **Alta** | 🔲 |

### 📖 **Acervo** — Sprint 2

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-07 | Listagem | Busca, filtros por categoria e ordenação | 🔴 **Alta** | 🔲 |
| TASK-08 | Detalhes | Capa, sinopse, disponibilidade, botões (emprestar/reservar) | 🔴 **Alta** | 🔲 |

### 📅 **Eventos** — Sprint 2

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-09 | Eventos | Calendário, listagem com inscrição em tempo real | 🔴 **Alta** | 🔲 |

### 🔔 **Notificações** — Sprint 1-2

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-10 | Centro de Notificações | Listar alertas (prazos, reservas), marcar como lidas | 🔴 **Alta** | 🔲 |

### 👤 **Perfil** — Sprint 2-3

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-11 | Tela Base | Avatar, stats, "Minha Conta", "Configurações" | 🔴 **Alta** | ✅ **Concluído** |
| TASK-14 | Editar Perfil | Dialog com avatar, nome, curso, e-mail | 🟡 **Média** | 🔲 |
| TASK-15 | Histórico de Leituras | Livros emprestados/devolvidos com datas | 🟡 **Média** | 🔲 |
| TASK-16 | Metas de Leitura | Definir, visualizar e acompanhar progresso de metas | 🟡 **Média** | 🔲 |
| TASK-17 | Multas e Pagamentos | Listar atrasos, valores e status de pagamento | 🟡 **Média** | 🔲 |

### ⚙️ **Configurações** — Sprint 3

| ID | Componente | Descrição | Prioridade | Status |
|---|---|---|---|---|
| TASK-12 | Tela de Configurações | Opções de conta e privacidade | 🔴 **Alta** | 🔲 |
| TASK-13 | Alterar Senha | Dialog com validação (senha atual, nova, confirmação) | 🔴 **Alta** | 🔲 |
| TASK-18 | Personalização de UI | Tema, tamanho de fonte, rotação de tela | 🟢 **Baixa** | 🔲 |

---

## ✅ Implementações Concluídas

### TASK-11 — Tela de Perfil (Base)

**Data de conclusão:** Sprint 2  
**Responsável:** [@LucNath](https://github.com/LucNath)

#### Arquivos criados/modificados:

```
app/src/main/java/com/unifor/bibliotecadigital/
├── data/model/UserProfile.kt                    [novo]
└── ui/profile/
    └── ProfileFragment.kt                       [novo]

app/src/main/res/
├── layout/
│   ├── fragment_profile.xml                     [novo]
│   ├── item_stat_card.xml                       [novo]
│   └── item_menu_row.xml                        [novo]
├── drawable/
│   ├── bg_avatar.xml                            [novo]
│   ├── bg_card.xml                              [novo]
│   ├── bg_button_outline_orange.xml             [novo]
│   └── ic_chevron_right.xml                     [novo]
└── values/
    ├── colors.xml                               [modificado]
    └── strings.xml                              [modificado]
```

#### Funcionalidades implementadas:

✅ Avatar circular com iniciais geradas dinamicamente  
✅ Nome e curso do usuário abaixo do avatar  
✅ Botão "Editar Perfil" (link para TASK-14)  
✅ Cards de estatísticas (Emprestados | Devolvidos | Reservas)  
✅ Seção "Minha Conta" (Histórico → TASK-15, Metas → TASK-16, Multas → TASK-17)  
✅ Seção "Configurações" (Alterar Senha → TASK-13, Sair da Conta)  
✅ Dialog de confirmação ao fazer logout  
✅ Navigation listeners preparados como hooks para tasks futuras  

#### Design implementado:

- Fundo escuro azul-marinho (`#0F1B2D`)
- Avatar laranja (`#E8832A`) com iniciais em branco
- Cards com fundo `#1A2740`
- Tipografia branca com hierarquia clara
- Ícones chevron à direita das ações

---

## 🎨 Design System

O app segue um **Dark Mode** consistente com Material Design 3:

### Paleta de Cores

| Token | Hex | RGB | Uso |
|---|---|---|---|
| `bg_screen` | `#0F1B2D` | 15, 27, 45 | Fundo geral das telas |
| `bg_card` | `#1A2740` | 26, 39, 64 | Cards, seções, inputs |
| `bg_card_hover` | `#232E45` | 35, 46, 69 | Estados hover/pressed |
| `brand_orange` | `#E8832A` | 232, 131, 42 | Avatar, ícones, CTA |
| `brand_orange_dark` | `#C76A1A` | 199, 106, 26 | Estados pressed |
| `text_primary` | `#FFFFFF` | 255, 255, 255 | Títulos, conteúdo principal |
| `text_secondary` | `#8A9BB5` | 138, 155, 181 | Subtítulos, metadados |
| `text_section` | `#5A6E88` | 90, 110, 136 | Labels de seção, labels de form |
| `error_red` | `#E05252` | 224, 82, 82 | Ações destrutivas (logout) |
| `success_green` | `#52C77A` | 82, 199, 122 | Confirmações, status positivo |
| `divider` | `#1F3052` | 31, 48, 82 | Separadores, bordas |

### Tipografia

- **Família:** Roboto (Google Fonts)
- **Títulos principais:** 24sp, Bold (`#FFFFFF`)
- **Subtítulos:** 16sp, Medium (`#8A9BB5`)
- **Body:** 14sp, Regular (`#FFFFFF`)
- **Labels:** 12sp, Regular (`#5A6E88`)

### Componentes

- **Botões primários:** Background `#E8832A`, texto branco, radius 8dp
- **Botões secundários:** Border `1.5dp #E8832A`, background transparente
- **Cards:** Background `#1A2740`, elevation 2dp, radius 12dp
- **Inputs:** Background `#1A2740`, border `1dp #2D3E52`, radius 8dp

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- ✅ **Android Studio** Hedgehog (2023.1.1) ou superior
- ✅ **JDK 17** ou superior
- ✅ **Android SDK 34**
- ✅ Dispositivo/emulador com **Android 8.0+ (API 26)**
- ✅ Conta **Firebase** (opcional para desenvolvimento local)

### Passo a Passo

**1. Clone o repositório:**
```bash
git clone https://github.com/LucNath/DesenvolvimentoEmPlataformasMoveis.git
cd DesenvolvimentoEmPlataformasMoveis
```

**2. Abra no Android Studio:**
```
File → Open → selecione a pasta do projeto
```

**3. Aguarde o Gradle sync** (pode levar 2-3 minutos)

**4. Configure o Firebase:**
   1. Acesse [Firebase Console](https://console.firebase.google.com/)
   2. Crie um novo projeto ou use um existente
   3. Adicione um **App Android** com o package name: `com.unifor.bibliotecadigital`
   4. Baixe o arquivo `google-services.json`
   5. Coloque em `app/google-services.json`

**5. Ative os serviços Firebase:**
   - ✅ **Authentication** → E-mail/Senha
   - ✅ **Firestore Database** → Modo teste (durante desenvolvimento)
   - ✅ **Cloud Storage** (para imagens de livros)

**6. Execute o app:**
```
Shift + F10  (ou botão Run na toolbar)
```

### Solução de Problemas

| Erro | Solução |
|---|---|
| `Could not find google-services plugin` | Verifique se `google-services.json` está em `app/` |
| `Gradle sync failed` | Limpe o build: `Build → Clean Project` |
| `Firebase connection error` | Verifique as regras de segurança no Firestore |

---

## 📐 Metodologia — Scrum

O desenvolvimento segue **ciclos de Sprint de 2 semanas** com gestão via GitHub Projects:

```
┌─────────────────────────────────────────────────────────┐
│                 BACKLOG DO PRODUTO                      │
│  Todas as tasks priorizadas por valor e complexidade    │
└────────────────────────┬────────────────────────────────┘
                         │
                    (Planning)
                         │
                         ↓
┌─────────────────────────────────────────────────────────┐
│              BACKLOG DA SPRINT                           │
│  Tasks selecionadas com story points e responsáveis     │
└────────────────────────┬────────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         ↓                               ↓
    [Desenvolvimento]             [Revisão & Testes]
         │                               │
         └───────────────┬───────────────┘
                         ↓
           [Entrega Incremental]
                         │
                    (Review)
                    (Demo)
                         ↓
           [Retrospectiva da Sprint]
                         │
                   (Melhorias)
                         │
                    (Novo ciclo)
```

### Prioridades

| Ícone | Nível | Descrição |
|---|---|---|
| 🔴 | **Alta** | Essencial para MVP, implementação obrigatória |
| 🟡 | **Média** | Importante, implementar se tempo permitir |
| 🟢 | **Baixa** | Desejável, não afeta funcionalidade principal |

### Planning de Sprint

- **Duração:** 2 semanas (14 dias)
- **Reunião de Planning:** 2ª-feira, 14h
- **Reunião Diária (Standup):** 10h (15 min)
- **Revisão/Demo:** 5ª-feira, 15h
- **Retrospectiva:** 5ª-feira, 16h

---

## 📁 Estrutura de Branches

```
main/
├─ código estável (versão em produção)
│
develop/
├─ integração das features (staging)
│
feature/task-{ID}-{descricao}/
├─ feature/task-01-login
├─ feature/task-11-perfil
└─ uma branch por task

fix/bug-{descricao}/
├─ correções de bugs de produção

hotfix/
└─ correções urgentes
```

### Padrão de Commits

```bash
# Feature
git commit -m "feat(perfil): implementa tela base do perfil TASK-11"

# Bug fix
git commit -m "fix(login): corrige validação de e-mail vazio TASK-01"

# Refactor
git commit -m "refactor(auth): extrai lógica de validação em função auxiliar"

# Documentação
git commit -m "docs(readme): adiciona guia de contribuição"
```

### Fluxo de Pull Request

1. Crie branch a partir de `develop`: `git checkout -b feature/task-XX-descricao`
2. Faça commits semânticos e bem descritos
3. Abra PR com template padrão:
   ```
   ## 📋 Descrição
   Implementa [TASK-XX]: [Descrição]
   
   ## 🔗 Links
   - Relacionado: [TASK-XX]
   - Design: [Link do Figma/Protótipo]
   
   ## ✅ Checklist
   - [ ] Código testado localmente
   - [ ] Sem console.logs ou código commented
   - [ ] Segue padrão de código
   - [ ] Screenshots/vídeo em anexo
   ```
4. Solicite review de 2 membros da equipe
5. Resolva comentários e aguarde approval
6. Merge para `develop`, depois para `main` após sprint review

---

## 👤 Personas

### Lucas Mendes — Usuário Regular

- **Idade:** 21 anos
- **Profissão:** Estudante de Direito (Unifor)
- **Objetivo:** Encontrar, reservar e organizar leituras acadêmicas
- **Dor:** Demora para localizar livros, falta de notificações de devolução
- **Solução oferecida:** App rápido, busca inteligente, lembretes automáticos

### Geraldo Araujo — Administrador da Biblioteca

- **Idade:** 45 anos
- **Profissão:** Gestor da Biblioteca Central
- **Objetivo:** Monitorar acervo, acompanhar empréstimos, análises de uso
- **Dor:** Sistema legado, gestão manual de reservas
- **Solução oferecida:** Dashboard de administração, relatórios automáticos (futuro)

---

## 🤝 Contribuindo

Todos os membros da equipe devem:

1. Seguir o padrão de commits
2. Criar branches descritivas
3. Abrir PRs com descrição clara
4. Fazer code review dos colegas
5. Manter a documentação atualizada

---

## 📄 Licença

**Projeto Acadêmico** — Universidade de Fortaleza (Unifor)  
Desenvolvido para a disciplina de **Desenvolvimento de Plataformas Móveis (T197-26)**  
© 2026 — Todos os direitos reservados

---

## 📞 Contato

Para dúvidas sobre o projeto, abra uma **issue** no repositório ou entre em contato com a equipe via Discord do projeto.

---

## 🗺️ Roadmap

### Sprint 1 (Atual)
- [ ] Autenticação completa (TASK-01, 02, 03)
- [ ] Home com empréstimos/reservas (TASK-04, 05, 06)
- [ ] Notificações (TASK-10)

### Sprint 2
- [ ] Acervo com busca (TASK-07, 08)
- [ ] Eventos (TASK-09)
- [ ] Editar Perfil (TASK-14)

### Sprint 3
- [ ] Histórico, Metas, Multas (TASK-15, 16, 17)
- [ ] Configurações (TASK-12, 13, 18)
- [ ] Testes e otimizações

---

**Última atualização:** 29 de abril de 2026  
**Versão:** 0.1.0-alpha  
**Status:** Em desenvolvimento 🚧
```

---

## 📝 Principais melhorias implementadas:

### ✨ Estrutura
1. **Seções mais claras** com emojis e hierarquia visual
2. **Links clicáveis** nas tabelas (ex: GitHub profiles)
3. **Roadmap adicionado** para visibilidade do progresso
4. **Estrutura de diretórios mais detalhada** com caminhos completos

### 📊 Conteúdo
5. **Descrição melhorada** do projeto (objetivos, funcionalidades)
6. **Paleta de cores expandida** com valores RGB
7. **Typografia documentada** com tamanhos e pesos
8. **Troubleshooting section** com soluções comuns
9. **Personas expandidas** com mais contexto
10. **Fluxo de PR documentado** passo a passo
11. **Metodologia Scrum visualizada** com diagrama ASCII
12. **Data de conclusão** e responsável para TASK-11

### 🎯 Usabilidade
13. **Tabelas padronizadas** e com status visuais
14. **Badges de prioridade** consistentes
15. **Links para external resources** (Firebase, etc)
16. **Badges de status** (✅ Concluído, 🔲 Pendente)
17. **Seção de contato** adicionada
18. **Versionamento** explícito (0.1.0-alpha)

Agora o README é **profissional, bem-organizado e serve como single source of truth** para a equipe! 🚀
