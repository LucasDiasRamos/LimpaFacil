# Implementação da Feature — Registrar Venda

## 1. Objetivo da feature

A feature **Registrar Venda** tem como objetivo permitir que um funcionário registre uma venda de produtos de limpeza no sistema LimpaFácil.

Essa funcionalidade deve permitir:

```text
Buscar produtos cadastrados
Adicionar produtos à venda
Informar quantidades
Validar estoque disponível
Calcular subtotal e valor total
Selecionar forma de pagamento
Confirmar a venda
Salvar a venda no PostgreSQL
Salvar os itens da venda
Baixar automaticamente o estoque
Exibir comprovante simples da venda
```

Essa feature faz parte da **1ª iteração**, conforme o planejamento do documento de casos de uso.

---

## 2. Pré-condições

Antes de implementar ou executar a feature, o sistema deve possuir:

```text
Produtos cadastrados
Categorias cadastradas
Conexão com PostgreSQL funcionando
Usuário logado ou funcionário padrão definido
Tela principal funcionando
```

Caso o login ainda não esteja completo, pode ser usado um funcionário padrão para fins acadêmicos, desde que isso seja explicado no vídeo da entrega.

---

## 3. Pós-condições

Após confirmar uma venda:

```text
A venda deve ser salva no banco de dados
Os itens da venda devem ser salvos no banco de dados
O estoque dos produtos vendidos deve ser atualizado
A venda deve ficar com status REGISTRADA
O comprovante deve ser exibido
```

---

## 4. Regras de negócio

## RN01 — Disponibilidade de estoque

O sistema não deve permitir adicionar à venda uma quantidade maior do que o estoque disponível.

Exemplo:

```text
Produto: Água Sanitária
Estoque atual: 3
Quantidade solicitada: 5
Resultado: venda não permitida para esse item
```

Mensagem sugerida:

```text
Estoque insuficiente. Quantidade disponível: 3.
```

---

## RN02 — Preço vigente

O preço usado no item da venda deve ser o preço cadastrado no produto no momento em que o produto é adicionado à venda.

Exemplo:

```text
Produto: Detergente
Preço atual: R$ 3,50
Preço salvo no item da venda: R$ 3,50
```

Mesmo que o preço do produto seja alterado depois, o item da venda deve manter o preço utilizado no momento da venda.

---

## RN03 — Funcionário responsável

Toda venda deve estar associada ao funcionário que realizou a operação.

O funcionário pode ser obtido de duas formas:

```text
Usuário logado na sessão
Funcionário padrão para demonstração acadêmica
```

---

## RN04 — Pagamento integral

A venda só deve ser confirmada após a seleção da forma de pagamento.

Formas de pagamento previstas:

```text
DINHEIRO
PIX
CARTAO_CREDITO
CARTAO_DEBITO
```

Para a primeira implementação, PIX e cartão podem ser simulados.

---

## RN05 — Baixa automática no estoque

Após a confirmação da venda, o sistema deve reduzir a quantidade em estoque de cada produto vendido.

Exemplo:

```text
Produto: Detergente Neutro
Estoque antes: 20
Quantidade vendida: 2
Estoque depois: 18
```

---

## RN06 — Imutabilidade da venda

Após confirmada, a venda não deve ser editada nem excluída.

Para a primeira entrega, basta não disponibilizar botões de edição ou exclusão para vendas registradas.

---

# 5. Fluxo principal da feature

```text
1. O usuário acessa a tela de Registrar Venda.
2. O sistema carrega a tela de venda.
3. O usuário busca um produto por código ou nome.
4. O sistema exibe o produto encontrado.
5. O usuário informa a quantidade desejada.
6. O sistema valida se existe estoque suficiente.
7. O sistema adiciona o produto à lista da venda.
8. O sistema calcula o subtotal do item.
9. O sistema atualiza o valor total da venda.
10. O usuário seleciona a forma de pagamento.
11. Se for dinheiro, o usuário informa o valor recebido.
12. O sistema calcula o troco.
13. O usuário confirma a venda.
14. O sistema salva a venda no banco.
15. O sistema salva os itens da venda.
16. O sistema baixa o estoque dos produtos.
17. O sistema exibe o comprovante.
```

---

# 6. Fluxos alternativos

## Produto não encontrado

```text
1. O usuário busca um produto por código ou nome.
2. O sistema não encontra produto correspondente.
3. O sistema exibe mensagem de erro.
4. O usuário pode realizar uma nova busca.
```

Mensagem sugerida:

```text
Produto não encontrado.
```

---

## Estoque insuficiente

```text
1. O usuário informa uma quantidade maior do que o estoque disponível.
2. O sistema bloqueia a inclusão do produto na venda.
3. O sistema exibe a quantidade disponível.
4. O usuário pode informar uma nova quantidade.
```

Mensagem sugerida:

```text
Estoque insuficiente. Quantidade disponível: X.
```

---

## Pagamento em dinheiro com valor insuficiente

```text
1. O usuário seleciona pagamento em dinheiro.
2. O usuário informa valor recebido menor que o total da venda.
3. O sistema bloqueia a confirmação.
4. O sistema solicita um valor válido.
```

Mensagem sugerida:

```text
Valor recebido menor que o valor total da venda.
```

---

## Cancelar venda

```text
1. O usuário clica em cancelar venda.
2. O sistema limpa a lista de itens.
3. O sistema zera o valor total.
4. A venda não é salva no banco.
```

---

# 7. Estrutura de banco de dados

## Tabela vendas

```sql
CREATE TABLE vendas (
    id SERIAL PRIMARY KEY,
    codigo_venda VARCHAR(30) UNIQUE NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    funcionario_id INTEGER REFERENCES usuarios(id),
    valor_total NUMERIC(10,2) NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL
);
```

---

## Tabela itens_venda

```sql
CREATE TABLE itens_venda (
    id SERIAL PRIMARY KEY,
    venda_id INTEGER NOT NULL REFERENCES vendas(id),
    produto_id INTEGER NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL
);
```

---

# 8. Models necessários

## Venda.java

```java
package br.com.limpafacil.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venda {

    private Integer id;
    private String codigoVenda;
    private LocalDateTime dataHora;
    private Usuario funcionario;
    private BigDecimal valorTotal;
    private FormaPagamento formaPagamento;
    private StatusVenda status;

    public Venda() {
    }

    public Venda(Integer id, String codigoVenda, LocalDateTime dataHora, Usuario funcionario,
                 BigDecimal valorTotal, FormaPagamento formaPagamento, StatusVenda status) {
        this.id = id;
        this.codigoVenda = codigoVenda;
        this.dataHora = dataHora;
        this.funcionario = funcionario;
        this.valorTotal = valorTotal;
        this.formaPagamento = formaPagamento;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoVenda() {
        return codigoVenda;
    }

    public void setCodigoVenda(String codigoVenda) {
        this.codigoVenda = codigoVenda;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Usuario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Usuario funcionario) {
        this.funcionario = funcionario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }
}
```

---

## ItemVenda.java

```java
package br.com.limpafacil.model;

import java.math.BigDecimal;

public class ItemVenda {

    private Integer id;
    private Venda venda;
    private Produto produto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public ItemVenda() {
    }

    public ItemVenda(Integer id, Venda venda, Produto produto, Integer quantidade,
                     BigDecimal precoUnitario, BigDecimal subtotal) {
        this.id = id;
        this.venda = venda;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
```

---

## FormaPagamento.java

```java
package br.com.limpafacil.model;

public enum FormaPagamento {
    DINHEIRO,
    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO
}
```

---

## StatusVenda.java

```java
package br.com.limpafacil.model;

public enum StatusVenda {
    INICIADA,
    REGISTRADA,
    CANCELADA
}
```

---

# 9. DAOs necessários

## VendaDAO.java

Responsável por salvar e consultar vendas.

Métodos sugeridos:

```java
public class VendaDAO {

    public Integer inserir(Venda venda) {
        // Insere a venda no banco
        // Retorna o ID gerado
    }

    public Venda buscarPorId(Integer id) {
        // Busca venda por ID
    }

    public Venda buscarPorCodigo(String codigoVenda) {
        // Busca venda pelo código
    }

    public List<Venda> listarTodos() {
        // Lista vendas registradas
    }
}
```

---

## ItemVendaDAO.java

Responsável por salvar e consultar os itens da venda.

Métodos sugeridos:

```java
public class ItemVendaDAO {

    public void inserir(ItemVenda itemVenda) {
        // Insere item da venda
    }

    public List<ItemVenda> listarPorVenda(Integer vendaId) {
        // Lista itens vinculados a uma venda
    }
}
```

---

## ProdutoDAO — métodos adicionais

Caso ainda não existam, adicionar:

```java
public Produto buscarPorCodigo(String codigoProduto);

public List<Produto> buscarPorNomeOuCodigo(String termo);

public void atualizarEstoque(Integer produtoId, Integer novaQuantidade);
```

---

# 10. Services necessários

## VendaService.java

Responsável pela regra principal de registro de venda.

Método principal sugerido:

```java
public Venda registrarVenda(List<ItemVenda> itens, FormaPagamento formaPagamento, BigDecimal valorRecebido) {
    // 1. Validar itens
    // 2. Validar estoque
    // 3. Calcular total
    // 4. Validar pagamento
    // 5. Criar venda
    // 6. Salvar venda
    // 7. Salvar itens
    // 8. Baixar estoque
    // 9. Retornar venda registrada
}
```

Responsabilidades:

```text
Validar lista de itens
Validar estoque
Calcular subtotal
Calcular total
Validar forma de pagamento
Salvar venda
Salvar itens
Baixar estoque
Retornar venda registrada
```

---

## PagamentoService.java

Responsável por validar o pagamento.

Métodos sugeridos:

```java
public boolean confirmarPagamento(FormaPagamento formaPagamento, BigDecimal valorTotal, BigDecimal valorRecebido) {
    // Valida pagamento conforme a forma escolhida
}

public BigDecimal calcularTroco(BigDecimal valorTotal, BigDecimal valorRecebido) {
    // Calcula troco para pagamento em dinheiro
}
```

Regras:

```text
DINHEIRO: valor recebido deve ser maior ou igual ao total
PIX: confirmação simulada
CARTAO_CREDITO: confirmação simulada
CARTAO_DEBITO: confirmação simulada
```

---

## ComprovanteService.java

Responsável por montar o comprovante simples.

Método sugerido:

```java
public String gerarComprovante(Venda venda, List<ItemVenda> itens) {
    // Retorna texto formatado com os dados da venda
}
```

Informações do comprovante:

```text
Código da venda
Data e hora
Funcionário
Produtos
Quantidade
Preço unitário
Subtotal
Valor total
Forma de pagamento
```

---

# 11. Controller da tela de venda

## VendaController.java

Responsabilidades:

```text
Carregar tela de venda
Buscar produto
Adicionar item na venda
Remover item da venda
Calcular total
Selecionar forma de pagamento
Confirmar venda
Cancelar venda
Exibir comprovante
Exibir mensagens de erro
```

Métodos sugeridos:

```java
private void buscarProduto();

private void adicionarProduto();

private void removerProduto();

private void calcularTotal();

private void confirmarVenda();

private void cancelarVenda();

private void abrirComprovante(Venda venda);
```

---

# 12. Tela FXML da venda

## vendas.fxml

Componentes recomendados:

```text
TextField txtBuscaProduto
Button btnBuscarProduto

TableView tabelaProdutosEncontrados
TableColumn colunaCodigoProduto
TableColumn colunaNomeProduto
TableColumn colunaMarcaProduto
TableColumn colunaPrecoProduto
TableColumn colunaEstoqueProduto

TextField txtQuantidade
Button btnAdicionarItem

TableView tabelaItensVenda
TableColumn colunaItemProduto
TableColumn colunaItemQuantidade
TableColumn colunaItemPreco
TableColumn colunaItemSubtotal
Button btnRemoverItem

ComboBox comboFormaPagamento
TextField txtValorRecebido
Label lblTroco
Label lblTotalVenda

Button btnConfirmarVenda
Button btnCancelarVenda
```

---

# 13. Tela de comprovante

## comprovante.fxml

Componentes recomendados:

```text
TextArea txtComprovante
Button btnFechar
Button btnImprimir
```

Para a primeira entrega, o botão imprimir pode ser opcional. O principal é exibir o comprovante.

---

# 14. Fluxo de implementação recomendado

## Etapa 1 — Banco de dados

```text
Criar tabela vendas
Criar tabela itens_venda
Verificar relacionamento com produtos
Verificar relacionamento com usuarios
```

---

## Etapa 2 — Models

```text
Criar Venda.java
Criar ItemVenda.java
Criar FormaPagamento.java
Criar StatusVenda.java
```

---

## Etapa 3 — DAOs

```text
Criar VendaDAO
Criar ItemVendaDAO
Adicionar métodos de busca e atualização de estoque no ProdutoDAO
```

---

## Etapa 4 — Services

```text
Criar PagamentoService
Criar ComprovanteService
Criar VendaService
```

---

## Etapa 5 — Tela JavaFX

```text
Criar vendas.fxml
Criar comprovante.fxml
Criar VendaController
Criar ComprovanteController
Adicionar botão ou menu para abrir Registrar Venda
```

---

## Etapa 6 — Integração

```text
Conectar tela com ProdutoDAO/ProdutoService
Adicionar produtos na tabela de itens
Calcular subtotal
Calcular total
Validar pagamento
Chamar VendaService para confirmar venda
Exibir comprovante
Atualizar estoque
```

---

## Etapa 7 — Testes

```text
Testar produto existente
Testar produto inexistente
Testar estoque suficiente
Testar estoque insuficiente
Testar pagamento em dinheiro
Testar pagamento PIX
Testar pagamento cartão
Testar baixa no estoque
Testar comprovante
Testar persistência no PostgreSQL
```

---

# 15. Exemplo de pseudocódigo do registro de venda

```java
public Venda registrarVenda(List<ItemVenda> itens, FormaPagamento formaPagamento, BigDecimal valorRecebido) {

    if (itens == null || itens.isEmpty()) {
        throw new RuntimeException("A venda deve possuir pelo menos um item.");
    }

    BigDecimal total = BigDecimal.ZERO;

    for (ItemVenda item : itens) {
        Produto produto = produtoDAO.buscarPorId(item.getProduto().getId());

        if (produto == null) {
            throw new RuntimeException("Produto não encontrado.");
        }

        if (item.getQuantidade() > produto.getQuantidadeEstoque()) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        BigDecimal precoUnitario = produto.getPrecoVenda();
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(item.getQuantidade()));

        item.setPrecoUnitario(precoUnitario);
        item.setSubtotal(subtotal);

        total = total.add(subtotal);
    }

    boolean pagamentoConfirmado = pagamentoService.confirmarPagamento(formaPagamento, total, valorRecebido);

    if (!pagamentoConfirmado) {
        throw new RuntimeException("Pagamento não confirmado.");
    }

    Venda venda = new Venda();
    venda.setCodigoVenda(gerarCodigoVenda());
    venda.setDataHora(LocalDateTime.now());
    venda.setFuncionario(SessaoUsuario.getUsuarioLogado());
    venda.setValorTotal(total);
    venda.setFormaPagamento(formaPagamento);
    venda.setStatus(StatusVenda.REGISTRADA);

    Integer vendaId = vendaDAO.inserir(venda);
    venda.setId(vendaId);

    for (ItemVenda item : itens) {
        item.setVenda(venda);
        itemVendaDAO.inserir(item);

        Produto produto = item.getProduto();
        Integer novoEstoque = produto.getQuantidadeEstoque() - item.getQuantidade();

        produtoDAO.atualizarEstoque(produto.getId(), novoEstoque);
    }

    return venda;
}
```

---

# 16. Checklist da feature

```text
[ ] Criar tabela vendas
[ ] Criar tabela itens_venda
[ ] Criar model Venda
[ ] Criar model ItemVenda
[ ] Criar enum FormaPagamento
[ ] Criar enum StatusVenda
[ ] Criar VendaDAO
[ ] Criar ItemVendaDAO
[ ] Criar PagamentoService
[ ] Criar ComprovanteService
[ ] Criar VendaService
[ ] Criar vendas.fxml
[ ] Criar VendaController
[ ] Criar comprovante.fxml
[ ] Criar ComprovanteController
[ ] Adicionar menu para abrir tela de venda
[ ] Implementar busca de produto
[ ] Implementar adicionar produto à venda
[ ] Implementar remover produto da venda
[ ] Implementar cálculo de subtotal
[ ] Implementar cálculo de total
[ ] Implementar seleção de pagamento
[ ] Implementar cálculo de troco
[ ] Implementar confirmação da venda
[ ] Implementar baixa no estoque
[ ] Implementar comprovante
[ ] Testar persistência no PostgreSQL
```

---

# 18. Observação final

Esta feature deve ser implementada de forma simples e objetiva, pois faz parte de um trabalho acadêmico. O foco principal é demonstrar corretamente o fluxo do caso de uso **Registrar Venda**, aplicando as regras de estoque, preço vigente, pagamento e baixa automática no PostgreSQL.

Não é necessário implementar integração real com banco, maquininha de cartão ou QR Code Pix. Essas operações podem ser simuladas, desde que o fluxo principal da venda esteja funcionando.
