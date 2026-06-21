package br.com.limpafacil.view;

import br.com.limpafacil.model.entity.Categoria;
import br.com.limpafacil.model.control.ControladoraAutenticacao;
import br.com.limpafacil.model.control.ControladoraCategoria;
import br.com.limpafacil.view.AlertaUtil;
import br.com.limpafacil.controller.ControladorNavegacao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoriaViewController {
    @FXML
    private TextField codigoField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField buscaField;
    @FXML
    private Label mensagemLabel;
    @FXML
    private TableView<Categoria> categoriasTable;
    @FXML
    private TableColumn<Categoria, String> codigoColumn;
    @FXML
    private TableColumn<Categoria, String> nomeColumn;

    private final ControladoraCategoria categoriaService = new ControladoraCategoria();
    private final ControladoraAutenticacao authService = new ControladoraAutenticacao();
    private Categoria categoriaSelecionada;

    @FXML
    private void initialize() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        categoriasTable.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> selecionar(nova));
        carregarTabela();
    }

    @FXML
    private void salvar() {
        try {
            Categoria categoria = categoriaSelecionada == null ? new Categoria() : categoriaSelecionada;
            categoria.setCodigo(codigoField.getText());
            categoria.setNome(nomeField.getText());
            categoriaService.salvar(categoria);
            mensagemLabel.setText("Categoria salva com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        try {
            if (categoriaSelecionada == null) {
                mensagemLabel.setText("Selecione uma categoria para excluir.");
                return;
            }
            if (AlertaUtil.confirmar("Deseja excluir a categoria selecionada?")) {
                categoriaService.excluir(categoriaSelecionada.getId());
                mensagemLabel.setText("Categoria excluída com sucesso.");
                limpar();
                carregarTabela();
            }
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void buscar() {
        categoriasTable.setItems(FXCollections.observableArrayList(categoriaService.buscar(buscaField.getText())));
    }

    @FXML
    private void limpar() {
        categoriaSelecionada = null;
        categoriasTable.getSelectionModel().clearSelection();
        codigoField.clear();
        nomeField.clear();
    }

    @FXML
    private void voltarDashboard() {
        ControladorNavegacao.abrirDashboard();
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
    private void sair() {
        authService.logout();
        ControladorNavegacao.abrirLogin();
    }

    private void carregarTabela() {
        categoriasTable.setItems(FXCollections.observableArrayList(categoriaService.listarTodos()));
    }

    private void selecionar(Categoria categoria) {
        categoriaSelecionada = categoria;
        if (categoria != null) {
            codigoField.setText(categoria.getCodigo());
            nomeField.setText(categoria.getNome());
        }
    }
}
