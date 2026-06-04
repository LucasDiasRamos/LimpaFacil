# LimpaFácil

Aplicação desktop JavaFX para gerenciamento inicial de uma loja de produtos de limpeza.

## Primeira iteração implementada

- Login de usuários ativos.
- Dashboard com usuário logado, perfil, total de produtos e alertas de estoque mínimo.
- CRUD de categorias.
- Cadastro, edição, busca e inativação de produtos.
- Integração com PostgreSQL via JDBC.
- Registro de venda com baixa automática de estoque e comprovante.

## Requisitos

Para executar com Docker:

- Docker
- Docker Compose

Para executar sem Docker:

- Java 17 ou superior.
- Maven 3.8 ou superior.
- PostgreSQL.

## Banco de dados

O script inicial está em `src/main/resources/db/schema.sql`.

Execute com um usuário que tenha permissão para criar banco:

```bash
psql -U postgres -f src/main/resources/db/schema.sql
```

Configuração padrão usada pela aplicação:

```text
URL: jdbc:postgresql://localhost:5432/limpafacil
Usuário: postgres
Senha: postgres
```

Também é possível configurar por variáveis de ambiente:

```bash
export LIMPAFACIL_DB_URL="jdbc:postgresql://localhost:5432/limpafacil"
export LIMPAFACIL_DB_USER="postgres"
export LIMPAFACIL_DB_PASSWORD="postgres"
```

## Executar com Docker

Na primeira execução, ou sempre que houver alteração no `Dockerfile`:

```bash
docker compose up --build
```

Nas próximas execuções:

```bash
docker compose up
```

Liberar a tela para o JavaFX no Linux, se necessário:

```bash
xhost +local:docker
```

Para executar em segundo plano:

```bash
docker compose up -d
```

Para parar:

```bash
docker compose down
```

O container da aplicação usa estas configurações automaticamente:

```text
LIMPAFACIL_DB_URL=jdbc:postgresql://db:5432/limpafacil
LIMPAFACIL_DB_USER=postgres
LIMPAFACIL_DB_PASSWORD=postgres
```

Para recriar o banco do zero:

```bash
docker compose down -v
docker compose up --build
```

## Executar sem Docker

Execute o script inicial do banco:

```bash
psql -U postgres -f src/main/resources/db/schema.sql
```

Depois rode a aplicação:

```bash
mvn javafx:run
```

## Usuários de teste

```text
admin@limpafacil.com / 123456
funcionario@limpafacil.com / 123456
```
