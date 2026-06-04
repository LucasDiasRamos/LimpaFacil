package br.com.limpafacil.service;

import br.com.limpafacil.dao.ProdutoDAO;
import br.com.limpafacil.model.Produto;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoService {
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public void salvar(Produto produto) {
        validar(produto);
        produto.setCodigoProduto(produto.getCodigoProduto().trim().toUpperCase());
        produto.setNome(produto.getNome().trim());
        produto.setMarca(produto.getMarca().trim());

        if (produto.getAtivo() == null) {
            produto.setAtivo(true);
        }

        if (produto.getId() == null) {
            produtoDAO.inserir(produto);
        } else {
            produtoDAO.atualizar(produto);
        }
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um produto para inativar.");
        }
        produtoDAO.excluir(id);
    }

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public List<Produto> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return produtoDAO.buscar(termo.trim());
    }

    public List<Produto> listarEstoqueMinimo() {
        return produtoDAO.listarEstoqueMinimo();
    }

    public int contarProdutos() {
        return produtoDAO.contarProdutos();
    }

    private void validar(Produto produto) {
        if (produto.getCodigoProduto() == null || produto.getCodigoProduto().isBlank()) {
            throw new IllegalArgumentException("Informe o código do produto.");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do produto.");
        }
        if (produto.getMarca() == null || produto.getMarca().isBlank()) {
            throw new IllegalArgumentException("Informe a marca do produto.");
        }
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("Selecione a categoria.");
        }
        if (produto.getPrecoVenda() == null || produto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um preço de venda maior que zero.");
        }
        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Informe uma quantidade em estoque válida.");
        }
        if (produto.getNivelMinimo() == null || produto.getNivelMinimo() < 0) {
            throw new IllegalArgumentException("Informe um nível mínimo válido.");
        }
    }
}
