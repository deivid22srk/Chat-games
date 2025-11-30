# Games Chat App 🎮

Um aplicativo Android de chat em tempo real focado em conversas sobre games, desenvolvido com Kotlin, Jetpack Compose e Supabase.

## 🌟 Características

- **Chat em Tempo Real**: Mensagens instantâneas usando Supabase Realtime
- **Autenticação**: Sistema de login e registro com username/senha
- **Material You**: Design moderno seguindo as diretrizes do Material Design 3
- **Modo Flutuante**: Janela flutuante que sobrepõe outros apps
- **Histórico de Mensagens**: Visualize todas as mensagens antigas do chat

## 🚀 Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação principal
- **Jetpack Compose**: UI moderna e declarativa
- **Material 3**: Design system mais recente do Google
- **Supabase**: Backend-as-a-Service com PostgreSQL e Realtime
- **Coroutines**: Programação assíncrona
- **ViewModel**: Arquitetura MVVM

## 📦 Configuração do Supabase

1. Acesse o [Supabase Dashboard](https://app.supabase.com)
2. Navegue até o SQL Editor
3. Execute o script `supabase_setup.sql` fornecido no projeto
4. As tabelas `users` e `messages` serão criadas automaticamente

## 🔧 Como Compilar

### Pré-requisitos
- JDK 17 ou superior
- Android SDK com Android 14 (API 34)
- Android Studio Hedgehog ou superior (opcional)

### Compilar via linha de comando
```bash
./gradlew assembleDebug
```

### Compilar via GitHub Actions
O projeto inclui um workflow do GitHub Actions que compila automaticamente:
- Push ou Pull Request para branches: main, master, develop
- Manual via workflow_dispatch

## 🎯 Funcionalidades

### Login e Registro
- Crie uma conta com username e senha
- Faça login com suas credenciais
- Senhas são armazenadas com hash SHA-256

### Chat
- Envie e receba mensagens em tempo real
- Veja o histórico completo de mensagens
- Interface intuitiva com Material You

### Modo Flutuante
- Ative o modo flutuante para usar o chat sobre outros apps
- Arraste o ícone flutuante para qualquer posição
- Clique para expandir o chat
- Minimize para um pequeno ícone
- Feche o modo flutuante quando quiser

## 📱 Permissões

- `INTERNET`: Para comunicação com o Supabase
- `ACCESS_NETWORK_STATE`: Para verificar conectividade
- `SYSTEM_ALERT_WINDOW`: Para o modo de janela flutuante
- `FOREGROUND_SERVICE`: Para manter o chat flutuante ativo

## 🏗️ Estrutura do Projeto

```
app/
├── data/
│   ├── model/          # Modelos de dados (User, Message)
│   ├── repository/     # Repositórios (AuthRepository, ChatRepository)
│   └── SupabaseClient  # Cliente Supabase configurado
├── ui/
│   ├── theme/          # Tema Material You
│   ├── viewmodel/      # ViewModels (AuthViewModel, ChatViewModel)
│   ├── MainActivity    # Tela de login/registro
│   └── ChatActivity    # Tela de chat
└── service/
    └── FloatingChatService  # Serviço de janela flutuante
```

## 🔐 Segurança

- Senhas nunca são armazenadas em texto plano
- Comunicação HTTPS com Supabase
- Row Level Security (RLS) habilitado no Supabase
- Políticas de segurança configuradas nas tabelas

## 📄 Licença

Este projeto é livre para uso pessoal e educacional.

## 👥 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

## 🎮 Sobre

Games Chat é um espaço para gamers conversarem sobre seus jogos favoritos, compartilharem dicas, e fazerem novos amigos na comunidade gamer!
