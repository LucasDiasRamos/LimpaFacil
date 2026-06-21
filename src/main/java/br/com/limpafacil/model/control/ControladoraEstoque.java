package br.com.limpafacil.model.control;

import br.com.limpafacil.model.entity.Produto;

import java.util.List;

public class ControladoraEstoque {
    private final ControladoraProduto produtoService = new ControladoraProduto();

    public List<Produto> listarProdutosComEstoqueMinimo() {
        return produtoService.listarEstoqueMinimo();
    }

    public int contarProdutosComEstoqueMinimo() {
        return listarProdutosComEstoqueMinimo().size();
    }

    public void ajustarEstoque(Produto produto, Integer quantidade, Integer nivelMinimo) {
        produtoService.ajustarEstoque(produto, quantidade, nivelMinimo);
    }
}
