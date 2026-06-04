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
