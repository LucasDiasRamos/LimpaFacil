package br.com.limpafacil.controller;

import br.com.limpafacil.model.Categoria;
import br.com.limpafacil.model.Produto;
import br.com.limpafacil.service.AuthService;
import br.com.limpafacil.service.CategoriaService;
import br.com.limpafacil.service.ProdutoService;
import br.com.limpafacil.util.AlertaUtil;
import br.com.limpafacil.util.NavegacaoUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class ProdutoController {
    @FXML
    private TextField codigoField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField marcaField;
    @FXML
    private ComboBox<Categoria> categoriaComboBox;
    @FXML
    private TextField precoField;
    @FXML
    private TextField quantidadeField;
    @FXML
    private TextField nivelMinimoField;
    @FXML
    private CheckBox ativoCheckBox;
    @FXML
    private TextField buscaField;
    @FXML
    private Label mensagemLabel;
    @FXML
    private Label alertasLabel;
    @FXML
    private TableView<Produto> produtosTable;
    @FXML
    private TableColumn<Produto, String> codigoColumn;
    @FXML
    private TableColumn<Produto, String> nomeColumn;
    @FXML
    private TableColumn<Produto, String> marcaColumn;
    @FXML
    private TableColumn<Produto, String> categoriaColumn;
    @FXML
    private TableColumn<Produto, BigDecimal> precoColumn;
    @FXML
    private TableColumn<Produto, Integer> quantidadeColumn;
    @FXML
    private TableColumn<Produto, Integer> minimoColumn;
    @FXML
    private TableColumn<Produto, Boolean> ativoColumn;

    private final ProdutoService produtoService = new ProdutoService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final AuthService authService = new AuthService();
    private Produto produtoSelecionado;

    @FXML
    private void initialize() {
        configurarTabela();
        carregarCategorias();
        carregarTabela();
        limpar();
    }

    @FXML
    private void salvar() {
        try {
            Produto produto = produtoSelecionado == null ? new Produto() : produtoSelecionado;
            produto.setCodigoProduto(codigoField.getText());
            produto.setNome(nomeField.getText());
            produto.setMarca(marcaField.getText());
            produto.setCategoria(categoriaComboBox.getValue());
            produto.setPrecoVenda(parseBigDecimal(precoField.getText()));
            produto.setQuantidadeEstoque(parseInteger(quantidadeField.getText(), "quantidade em estoque"));
            produto.setNivelMinimo(parseInteger(nivelMinimoField.getText(), "nível mínimo"));
            produto.setAtivo(ativoCheckBox.isSelected());
            produtoService.salvar(produto);
            mensagemLabel.setText("Produto salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        try {
            if (produtoSelecionado == null) {
                mensagemLabel.setText("Selecione um produto para inativar.");
                return;
            }
            if (AlertaUtil.confirmar("Deseja inativar o produto selecionado?")) {
                produtoService.excluir(produtoSelecionado.getId());
                mensagemLabel.setText("Produto inativado com sucesso.");
                limpar();
                carregarTabela();
            }
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void buscar() {
        produtosTable.setItems(FXCollections.observableArrayList(produtoService.buscar(buscaField.getText())));
        atualizarAlertas();
    }

    @FXML
    private void limpar() {
        produtoSelecionado = null;
        produtosTable.getSelectionModel().clearSelection();
        codigoField.clear();
        nomeField.clear();
        marcaField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        precoField.clear();
        quantidadeField.clear();
        nivelMinimoField.clear();
        ativoCheckBox.setSelected(true);
    }

    @FXML
    private void voltarDashboard() {
        NavegacaoUtil.abrirDashboard();
    }

    @FXML
    private void abrirCategorias() {
        NavegacaoUtil.abrirCategorias();
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

    private void configurarTabela() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigoProduto"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        marcaColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));
        categoriaColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoria().getNome()));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        minimoColumn.setCellValueFactory(new PropertyValueFactory<>("nivelMinimo"));
        ativoColumn.setCellValueFactory(new PropertyValueFactory<>("ativo"));

        produtosTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> selecionar(novo));
        produtosTable.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(Produto produto, boolean empty) {
                super.updateItem(produto, empty);
                getStyleClass().remove("estoque-minimo");
                if (!empty && produto != null && produto.isEstoqueMinimo()) {
                    getStyleClass().add("estoque-minimo");
                }
            }
        });
    }

    private void carregarCategorias() {
        categoriaComboBox.setItems(FXCollections.observableArrayList(categoriaService.listarTodos()));
    }

    private void carregarTabela() {
        produtosTable.setItems(FXCollections.observableArrayList(produtoService.listarTodos()));
        atualizarAlertas();
    }

    private void atualizarAlertas() {
        int totalAlertas = produtoService.listarEstoqueMinimo().size();
        alertasLabel.setText(totalAlertas + " produto(s) em estoque mínimo");
    }

    private void selecionar(Produto produto) {
        produtoSelecionado = produto;
        if (produto != null) {
            codigoField.setText(produto.getCodigoProduto());
            nomeField.setText(produto.getNome());
            marcaField.setText(produto.getMarca());
            categoriaComboBox.setValue(produto.getCategoria());
            precoField.setText(produto.getPrecoVenda().toPlainString());
            quantidadeField.setText(String.valueOf(produto.getQuantidadeEstoque()));
            nivelMinimoField.setText(String.valueOf(produto.getNivelMinimo()));
            ativoCheckBox.setSelected(Boolean.TRUE.equals(produto.getAtivo()));
        }
    }

    private BigDecimal parseBigDecimal(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe o preço de venda.");
        }
        try {
            return new BigDecimal(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um preço de venda válido.");
        }
    }

    private Integer parseInteger(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe " + campo + ".");
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe " + campo + " válido.");
        }
    }
}
