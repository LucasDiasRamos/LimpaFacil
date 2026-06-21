package br.com.limpafacil.view;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.control.ControladoraCliente;
import br.com.limpafacil.model.control.ControladoraOrcamento;
import br.com.limpafacil.model.control.ControladoraProduto;
import br.com.limpafacil.model.entity.Cliente;
import br.com.limpafacil.model.entity.ItemOrcamento;
import br.com.limpafacil.model.entity.Orcamento;
import br.com.limpafacil.model.entity.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class OrcamentoViewController {
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML private ComboBox<Cliente> clienteComboBox;
    @FXML private TextField buscaProdutoField;
    @FXML private TextField quantidadeField;
    @FXML private Label totalLabel;
    @FXML private Label mensagemLabel;
    @FXML private TableView<Produto> produtosTable;
    @FXML private TableColumn<Produto, String> produtoCodigoColumn;
    @FXML private TableColumn<Produto, String> produtoNomeColumn;
    @FXML private TableColumn<Produto, BigDecimal> produtoPrecoColumn;
    @FXML private TableView<ItemOrcamento> itensTable;
    @FXML private TableColumn<ItemOrcamento, String> itemProdutoColumn;
    @FXML private TableColumn<ItemOrcamento, Integer> itemQuantidadeColumn;
    @FXML private TableColumn<ItemOrcamento, BigDecimal> itemSubtotalColumn;

    private final ControladoraProduto controladoraProduto = new ControladoraProduto();
    private final ControladoraCliente controladoraCliente = new ControladoraCliente();
    private final ControladoraOrcamento controladoraOrcamento = new ControladoraOrcamento();
    private final ObservableList<ItemOrcamento> itens = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        clienteComboBox.setItems(FXCollections.observableArrayList(controladoraCliente.listarTodos()));
        produtoCodigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigoProduto"));
        produtoNomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        produtoPrecoColumn.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        itemProdutoColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduto().getNome()));
        itemQuantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        itemSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        itensTable.setItems(itens);
        buscarProdutos();
        atualizarTotal();
    }

    @FXML
    private void buscarProdutos() {
        produtosTable.setItems(FXCollections.observableArrayList(controladoraProduto.buscar(buscaProdutoField.getText())));
    }

    @FXML
    private void adicionarItem() {
        try {
            Produto produto = produtosTable.getSelectionModel().getSelectedItem();
            if (produto == null) {
                mensagemLabel.setText("Selecione um produto.");
                return;
            }
            int quantidade = parseQuantidade();
            ItemOrcamento item = new ItemOrcamento();
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPrecoVenda());
            item.setSubtotal(produto.getPrecoVenda().multiply(BigDecimal.valueOf(quantidade)));
            itens.add(item);
            quantidadeField.clear();
            mensagemLabel.setText("Item adicionado ao orçamento.");
            atualizarTotal();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void removerItem() {
        ItemOrcamento item = itensTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            itens.remove(item);
            atualizarTotal();
        }
    }

    @FXML
    private void registrarOrcamento() {
        try {
            Orcamento orcamento = controladoraOrcamento.registrarOrcamento(itens, clienteComboBox.getValue());
            mensagemLabel.setText("Orçamento registrado: " + orcamento.getCodigoOrcamento());
            itens.clear();
            clienteComboBox.getSelectionModel().clearSelection();
            atualizarTotal();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML private void voltarDashboard() { ControladorNavegacao.abrirDashboard(); }

    private void atualizarTotal() {
        totalLabel.setText(MOEDA.format(controladoraOrcamento.calcularTotal(itens)));
    }

    private int parseQuantidade() {
        try {
            int quantidade = Integer.parseInt(quantidadeField.getText().trim());
            if (quantidade <= 0) {
                throw new NumberFormatException();
            }
            return quantidade;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Informe uma quantidade válida.");
        }
    }
}
