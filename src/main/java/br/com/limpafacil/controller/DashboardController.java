package br.com.limpafacil.controller;

import br.com.limpafacil.model.Produto;
import br.com.limpafacil.model.Usuario;
import br.com.limpafacil.service.AuthService;
import br.com.limpafacil.service.EstoqueService;
import br.com.limpafacil.service.ProdutoService;
import br.com.limpafacil.util.NavegacaoUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class DashboardController {
    @FXML
    private Label usuarioLabel;
    @FXML
    private Label perfilLabel;
    @FXML
    private Label totalProdutosLabel;
    @FXML
    private Label alertasLabel;
    @FXML
    private ListView<String> alertasListView;

    private final AuthService authService = new AuthService();
    private final ProdutoService produtoService = new ProdutoService();
    private final EstoqueService estoqueService = new EstoqueService();

    @FXML
    private void initialize() {
        carregarUsuario();
        carregarIndicadores();
    }

    @FXML
    private void abrirCategorias() {
        NavegacaoUtil.abrirCategorias();
    }

    @FXML
    private void abrirProdutos() {
        NavegacaoUtil.abrirProdutos();
    }

    @FXML
    private void abrirVenda() {
        NavegacaoUtil.abrirVenda();
    }

    @FXML
    private void sair() {
        authService.logout();
        NavegacaoUtil.abrirLogin();
    }

    private void carregarUsuario() {
        Usuario usuario = authService.getUsuarioLogado();
        if (usuario == null) {
            NavegacaoUtil.abrirLogin();
            return;
        }
        usuarioLabel.setText(usuario.getNome());
        perfilLabel.setText(usuario.getPerfil().name());
    }

    private void carregarIndicadores() {
        List<Produto> alertas = estoqueService.listarProdutosComEstoqueMinimo();
        totalProdutosLabel.setText(String.valueOf(produtoService.contarProdutos()));
        alertasLabel.setText(String.valueOf(alertas.size()));
        alertasListView.setItems(FXCollections.observableArrayList(
                alertas.stream()
                        .map(produto -> "Atenção: " + produto.getNome()
                                + " | Estoque atual: " + produto.getQuantidadeEstoque()
                                + " | Nível mínimo: " + produto.getNivelMinimo())
                        .toList()
        ));
    }
}
