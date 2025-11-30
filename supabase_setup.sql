-- Games Chat App - Supabase Database Setup
-- Execute este script no SQL Editor do Supabase Dashboard

-- Criar tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Criar índice para busca rápida por username
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Criar tabela de mensagens
CREATE TABLE IF NOT EXISTS messages (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    username TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Criar índice para ordenação por data
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at DESC);

-- Habilitar Row Level Security (RLS)
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

-- Políticas para a tabela users
-- Permitir que qualquer um possa criar um usuário (sign up)
CREATE POLICY "Allow insert for anyone" ON users
    FOR INSERT
    WITH CHECK (true);

-- Permitir que qualquer um possa ler usuários (para login)
CREATE POLICY "Allow select for anyone" ON users
    FOR SELECT
    USING (true);

-- Políticas para a tabela messages
-- Permitir que qualquer um possa inserir mensagens
CREATE POLICY "Allow insert for anyone" ON messages
    FOR INSERT
    WITH CHECK (true);

-- Permitir que qualquer um possa ler mensagens
CREATE POLICY "Allow select for anyone" ON messages
    FOR SELECT
    USING (true);

-- Habilitar Realtime para a tabela messages
ALTER PUBLICATION supabase_realtime ADD TABLE messages;

-- Função para limpar mensagens antigas (opcional - manter apenas últimas 1000 mensagens)
CREATE OR REPLACE FUNCTION cleanup_old_messages()
RETURNS void AS $$
BEGIN
    DELETE FROM messages
    WHERE id IN (
        SELECT id FROM messages
        ORDER BY created_at DESC
        OFFSET 1000
    );
END;
$$ LANGUAGE plpgsql;

-- Comentários descritivos
COMMENT ON TABLE users IS 'Tabela de usuários do Games Chat';
COMMENT ON TABLE messages IS 'Tabela de mensagens do chat de games';
COMMENT ON COLUMN users.username IS 'Nome de usuário único';
COMMENT ON COLUMN users.password_hash IS 'Hash SHA-256 da senha';
COMMENT ON COLUMN messages.user_id IS 'Referência ao usuário que enviou a mensagem';
COMMENT ON COLUMN messages.username IS 'Nome do usuário (desnormalizado para performance)';
COMMENT ON COLUMN messages.content IS 'Conteúdo da mensagem';
