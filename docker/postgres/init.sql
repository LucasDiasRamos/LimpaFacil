CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categorias (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS produtos (
    id SERIAL PRIMARY KEY,
    codigo_produto VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    categoria_id INTEGER NOT NULL REFERENCES categorias(id),
    preco_venda NUMERIC(10,2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL,
    nivel_minimo INTEGER NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS vendas (
    id SERIAL PRIMARY KEY,
    codigo_venda VARCHAR(30) UNIQUE NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    funcionario_id INTEGER NOT NULL REFERENCES usuarios(id),
    valor_total NUMERIC(10,2) NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS itens_venda (
    id SERIAL PRIMARY KEY,
    venda_id INTEGER NOT NULL REFERENCES vendas(id),
    produto_id INTEGER NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS clientes (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE NOT NULL,
    endereco VARCHAR(150),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    telefone VARCHAR(20),
    email VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fornecedores (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    nome_fantasia VARCHAR(100),
    cpf_cnpj VARCHAR(20) UNIQUE NOT NULL,
    endereco VARCHAR(150),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    telefone VARCHAR(20),
    email VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS funcionarios (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(150),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    telefone VARCHAR(20),
    email VARCHAR(100) UNIQUE NOT NULL,
    data_contratacao DATE,
    cargo VARCHAR(80),
    perfil VARCHAR(20) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

ALTER TABLE vendas
ADD COLUMN IF NOT EXISTS cliente_id INTEGER REFERENCES clientes(id);

CREATE TABLE IF NOT EXISTS orcamentos (
    id SERIAL PRIMARY KEY,
    codigo_orcamento VARCHAR(30) UNIQUE NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    cliente_id INTEGER REFERENCES clientes(id),
    funcionario_id INTEGER REFERENCES usuarios(id),
    valor_total NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS itens_orcamento (
    id SERIAL PRIMARY KEY,
    orcamento_id INTEGER NOT NULL REFERENCES orcamentos(id),
    produto_id INTEGER NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS devolucoes (
    id SERIAL PRIMARY KEY,
    codigo_devolucao VARCHAR(30) UNIQUE NOT NULL,
    venda_id INTEGER NOT NULL REFERENCES vendas(id),
    cliente_id INTEGER REFERENCES clientes(id),
    funcionario_id INTEGER REFERENCES usuarios(id),
    data_hora TIMESTAMP NOT NULL,
    motivo TEXT NOT NULL,
    valor_total NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS itens_devolucao (
    id SERIAL PRIMARY KEY,
    devolucao_id INTEGER NOT NULL REFERENCES devolucoes(id),
    produto_id INTEGER NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL,
    valor_unitario NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL
);

INSERT INTO usuarios (nome, email, senha, perfil, ativo)
VALUES
('Administrador', 'admin@limpafacil.com', '123456', 'ADMINISTRADOR', true),
('Funcionário', 'funcionario@limpafacil.com', '123456', 'FUNCIONARIO', true)
ON CONFLICT (email) DO NOTHING;

INSERT INTO categorias (codigo, nome)
VALUES
('CAT001', 'Limpeza Geral'),
('CAT002', 'Higiene'),
('CAT003', 'Desinfetantes')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO produtos
(codigo_produto, nome, marca, categoria_id, preco_venda, quantidade_estoque, nivel_minimo, ativo)
SELECT 'PROD001', 'Detergente Neutro', 'Ypê', c.id, 3.50, 20, 5, true
FROM categorias c
WHERE c.codigo = 'CAT001'
ON CONFLICT (codigo_produto) DO NOTHING;

INSERT INTO produtos
(codigo_produto, nome, marca, categoria_id, preco_venda, quantidade_estoque, nivel_minimo, ativo)
SELECT 'PROD002', 'Água Sanitária', 'Qboa', c.id, 6.90, 3, 5, true
FROM categorias c
WHERE c.codigo = 'CAT001'
ON CONFLICT (codigo_produto) DO NOTHING;

INSERT INTO produtos
(codigo_produto, nome, marca, categoria_id, preco_venda, quantidade_estoque, nivel_minimo, ativo)
SELECT 'PROD003', 'Desinfetante Lavanda', 'Veja', c.id, 8.99, 10, 4, true
FROM categorias c
WHERE c.codigo = 'CAT003'
ON CONFLICT (codigo_produto) DO NOTHING;
