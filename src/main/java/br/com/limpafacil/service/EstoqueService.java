package br.com.limpafacil.service;

import br.com.limpafacil.model.Produto;

import java.util.List;

public class EstoqueService {
    private final ProdutoService produtoService = new ProdutoService();

    public List<Produto> listarProdutosComEstoqueMinimo() {
        return produtoService.listarEstoqueMinimo();
    }

    public int contarProdutosComEstoqueMinimo() {
        return listarProdutosComEstoqueMinimo().size();
    }
}
