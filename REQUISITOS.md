# 📄 Requisitos do Projeto — Biblioteca Digital

Este documento detalha os Requisitos Funcionais (RF) e Não Funcionais (RNF) do aplicativo **Biblioteca Digital**, servindo como guia para o desenvolvimento e validação das funcionalidades.

---

## 📋 Requisitos Funcionais (RF)

Os Requisitos Funcionais descrevem as ações que o sistema deve ser capaz de realizar.

### 1. Autenticação e Gestão de Acesso
- **RF01**: O sistema deve permitir o cadastro de novos usuários com Nome, E-mail e Senha.
- **RF02**: O sistema deve realizar a autenticação de usuários via e-mail e senha.
- **RF03**: O sistema deve possibilitar a recuperação de senha enviando um e-mail diretamente da tela de login.
- **RF04**: O sistema deve permitir que o usuário encerre sua sessão (Logout) com confirmação.

### 2. Gestão de Acervo (Digital)
- **RF05**: O sistema deve listar todas as obras disponíveis para consulta.
- **RF06**: O sistema deve permitir a busca de livros por Título, Autor ou ISBN.
- **RF07**: O sistema deve oferecer filtragem por categorias (Ex: Direito, Fantasia, Ficção) com destaque visual da categoria selecionada.
- **RF08**: O sistema deve exibir detalhes da obra (Sinopse, Editora, Ano, ISBN e Prazo de Devolução).

### 3. Empréstimos e Reservas
- **RF09**: O sistema deve permitir o empréstimo de livros com status "Disponível".
- **RF10**: O sistema deve calcular automaticamente a data de devolução (15 dias a partir da data atual).
- **RF11**: O sistema deve permitir a reserva de livros com status "Indisponível".
- **RF12**: O sistema deve permitir a devolução de livros através da tela de detalhes, atualizando o estoque em tempo real.
- **RF13**: O sistema deve impedir que o usuário realize empréstimos ou reservas duplicadas do mesmo título simultaneamente.
- **RF14**: O sistema deve exibir uma tela de sucesso detalhada imediatamente após a confirmação de um empréstimo.

### 4. Perfil e Engajamento
- **RF15**: O sistema deve permitir a edição do perfil (Nome e Curso) através de um diálogo integrado.
- **RF16**: O sistema deve exibir contadores de livros emprestados, devolvidos e reservados na seção "Minha Biblioteca".
- **RF17**: O sistema deve possibilitar a criação e acompanhamento de "Metas de Leitura".
- **RF18**: O sistema deve listar notificações sobre o status da conta, prazos e reservas.

### 5. Administração (Painel Admin)
- **RF19**: O sistema deve exibir um dashboard com estatísticas globais para usuários administradores.
- **RF20**: Usuários administradores devem ter permissão para adicionar, editar ou excluir livros do acervo.

---

## ⚙️ Requisitos Não Funcionais (RNF)

Os Requisitos Não Funcionais descrevem as qualidades e restrições técnicas do sistema.

### 1. Interface e Experiência (UI/UX)
- **RNF01**: O aplicativo deve operar exclusivamente em **Tema Claro**.
- **RNF02**: A identidade visual deve seguir a cor primária **Azul Royal (#1A56DB)**.
- **RNF03**: O layout deve utilizar componentes do **Material Design 3**, com cantos arredondados (24dp/32dp) para cards e cabeçalhos.
- **RNF04**: A navegação principal deve ser feita via **Bottom Navigation Bar**.

### 2. Tecnologia e Arquitetura
- **RNF05**: O projeto deve seguir a arquitetura **MVVM** (Model-View-ViewModel).
- **RNF06**: A persistência de dados de negócio deve ser feita via **Firebase Firestore**.
- **RNF07**: A autenticação deve ser gerida pelo **Firebase Authentication**.
- **RNF08**: O carregamento de imagens de capas deve ser feito de forma assíncrona (Biblioteca **Coil**).

### 3. Performance e Disponibilidade
- **RNF09**: O sistema deve atualizar as informações na interface em **tempo real** via Snapshot Listeners do Firestore.
- **RNF10**: O aplicativo deve suportar cache local para permitir a visualização de dados previamente carregados mesmo sem conexão.
- **RNF11**: O sistema deve monitorar o estado da rede e alertar o usuário em caso de queda de conexão.

---
**Documento atualizado em:** Junho de 2026
**Versão:** 1.0.0
