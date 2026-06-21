package br.com.limpafacil.view;

import br.com.limpafacil.model.entity.Produto;
import br.com.limpafacil.model.entity.Usuario;
import br.com.limpafacil.model.control.ControladoraAutenticacao;
import br.com.limpafacil.model.control.ControladoraEstoque;
import br.com.limpafacil.model.control.ControladoraProduto;
import br.com.limpafacil.controller.ControladorNavegacao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class DashboardViewController {
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

    private final ControladoraAutenticacao authService = new ControladoraAutenticacao();
    private final ControladoraProduto produtoService = new ControladoraProduto();
    private final ControladoraEstoque estoqueService = new ControladoraEstoque();

    @FXML
    private void initialize() {
        carregarUsuario();
        carregarIndicadores();
    }

    @FXML
    private void abrirCategorias() {
        ControladorNavegacao.abrirCategorias();
    }

    @FXML
    private void abrirProdutos() {
        ControladorNavegacao.abrirProdutos();
    }

    @FXML
    private void abrirVenda() {
        ControladorNavegacao.abrirVenda();
    }

    @FXML
    private void abrirClientes() {
        ControladorNavegacao.abrirClientes();
    }

    @FXML
    private void abrirFornecedores() {
        ControladorNavegacao.abrirFornecedores();
    }

    @FXML
    private void abrirFuncionarios() {
        ControladorNavegacao.abrirFuncionarios();
    }

    @FXML
    private void abrirOrcamentos() {
        ControladorNavegacao.abrirOrcamentos();
    }

    @FXML
    private void abrirDevolucoes() {
        ControladorNavegacao.abrirDevolucoes();
    }

    @FXML
    private void abrirRelatorios() {
        ControladorNavegacao.abrirRelatorios();
    }

    @FXML
    private void sair() {
        authService.logout();
        ControladorNavegacao.abrirLogin();
    }

    private void carregarUsuario() {
        Usuario usuario = authService.getUsuarioLogado();
        if (usuario == null) {
            ControladorNavegacao.abrirLogin();
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
