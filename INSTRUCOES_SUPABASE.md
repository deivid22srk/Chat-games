# 📋 Instruções para Configuração do Supabase

## 🔧 Configuração Inicial

### 1. Acesse seu Projeto Supabase
- URL: https://uwauhtopwnzrofyeojbu.supabase.co
- Faça login no [Supabase Dashboard](https://app.supabase.com)

### 2. Execute o Script SQL

1. No dashboard do Supabase, clique em **SQL Editor** no menu lateral
2. Clique em **New Query**
3. Copie todo o conteúdo do arquivo `supabase_setup.sql`
4. Cole no editor SQL
5. Clique em **Run** ou pressione `Ctrl+Enter`

### 3. Verifique as Tabelas Criadas

Navegue até **Table Editor** e você deverá ver:
- ✅ Tabela `users` - Para armazenar usuários
- ✅ Tabela `messages` - Para armazenar mensagens

### 4. Configuração do Realtime

O script já habilita o Realtime automaticamente, mas você pode verificar:

1. Vá em **Database** → **Replication**
2. Certifique-se de que a tabela `messages` está marcada para replicação

## 🔐 Credenciais Já Configuradas no App

As seguintes credenciais já estão configuradas no arquivo `SupabaseClient.kt`:

```
Project URL: https://uwauhtopwnzrofyeojbu.supabase.co
API Key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV3YXVodG9wd256cm9meWVvamJ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1MzM5NDAsImV4cCI6MjA4MDEwOTk0MH0.AyHyeNPvzqc8tPw31o1HTGRRu7AEFaeBZLsXLLVunZo
```

⚠️ **Importante**: Esta é a chave `anon` pública. Nunca exponha a chave `service_role`!

## 📊 Estrutura das Tabelas

### Tabela: users
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | UUID | ID único do usuário (auto-gerado) |
| username | TEXT | Nome de usuário (único) |
| password_hash | TEXT | Hash SHA-256 da senha |
| created_at | TIMESTAMP | Data de criação |

### Tabela: messages
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | UUID | ID único da mensagem (auto-gerado) |
| user_id | UUID | ID do usuário que enviou |
| username | TEXT | Nome do usuário |
| content | TEXT | Conteúdo da mensagem |
| created_at | TIMESTAMP | Data de envio |

## 🔒 Segurança (RLS - Row Level Security)

O script configura políticas de segurança que permitem:
- ✅ Qualquer usuário pode criar uma conta (INSERT em users)
- ✅ Qualquer usuário pode fazer login (SELECT em users)
- ✅ Qualquer usuário pode enviar mensagens (INSERT em messages)
- ✅ Qualquer usuário pode ler mensagens (SELECT em messages)

Estas políticas são adequadas para um chat público. Se você quiser restringir o acesso, modifique as políticas RLS no Supabase Dashboard.

## 🧪 Testando a Configuração

Após executar o script, você pode testar inserindo dados manualmente:

```sql
-- Inserir um usuário de teste
INSERT INTO users (username, password_hash)
VALUES ('testuser', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8');

-- Inserir uma mensagem de teste
INSERT INTO messages (user_id, username, content)
SELECT id, 'testuser', 'Olá, pessoal! Alguém jogando agora?'
FROM users WHERE username = 'testuser';

-- Ver todas as mensagens
SELECT * FROM messages ORDER BY created_at DESC;
```

## 🚀 Próximos Passos

1. ✅ Execute o script SQL no Supabase
2. ✅ Compile o app Android
3. ✅ Instale no seu dispositivo
4. ✅ Crie uma conta e comece a conversar!

## 🆘 Problemas Comuns

### Erro: "relation already exists"
- **Causa**: As tabelas já foram criadas
- **Solução**: Está tudo OK! O script usa `CREATE TABLE IF NOT EXISTS`

### Erro de permissão ao inserir dados
- **Causa**: Políticas RLS não aplicadas corretamente
- **Solução**: Execute novamente as políticas (CREATE POLICY) do script

### Mensagens não aparecem em tempo real
- **Causa**: Realtime não habilitado para a tabela
- **Solução**: Execute `ALTER PUBLICATION supabase_realtime ADD TABLE messages;`

## 📞 Suporte

Para mais informações sobre o Supabase:
- [Documentação Oficial](https://supabase.com/docs)
- [Guia de Realtime](https://supabase.com/docs/guides/realtime)
- [Guia de RLS](https://supabase.com/docs/guides/auth/row-level-security)
