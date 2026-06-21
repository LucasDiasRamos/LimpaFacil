package br.com.limpafacil.view;

import br.com.limpafacil.model.entity.FormaPagamento;
import br.com.limpafacil.model.entity.ItemVenda;
import br.com.limpafacil.model.entity.Produto;
import br.com.limpafacil.model.entity.Venda;
import br.com.limpafacil.model.control.ControladoraAutenticacao;
import br.com.limpafacil.model.control.ControladoraCliente;
import br.com.limpafacil.model.control.ControladoraComprovante;
import br.com.limpafacil.model.control.ControladoraPagamento;
import br.com.limpafacil.model.control.ControladoraProduto;
import br.com.limpafacil.model.control.ControladoraVenda;
import br.com.limpafacil.controller.ControladorNavegacao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class VendaViewController {
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private TextField buscaProdutoField;
    @FXML
    private TextField quantidadeField;
    @FXML
    private ComboBox<FormaPagamento> formaPagamentoComboBox;
    @FXML
    private ComboBox<br.com.limpafacil.model.entity.Cliente> clienteComboBox;
    @FXML
    private TextField valorRecebidoField;
    @FXML
    private Label totalLabel;
    @FXML
    private Label trocoLabel;
    @FXML
    private Label mensagemLabel;
    @FXML
    private TableView<Produto> produtosTable;
    @FXML
    private TableColumn<Produto, String> produtoCodigoColumn;
    @FXML
    private TableColumn<Produto, String> produtoNomeColumn;
    @FXML
    private TableColumn<Produto, BigDecimal> produtoPrecoColumn;
    @FXML
    private TableColumn<Produto, Integer> produtoEstoqueColumn;
    @FXML
    private TableView<ItemVenda> itensTable;
    @FXML
    private TableColumn<ItemVenda, String> itemCodigoColumn;
    @FXML
    private TableColumn<ItemVenda, String> itemNomeColumn;
    @FXML
    private TableColumn<ItemVenda, Integer> itemQuantidadeColumn;
    @FXML
    private TableColumn<ItemVenda, BigDecimal> itemPrecoColumn;
    @FXML
    private TableColumn<ItemVenda, BigDecimal> itemSubtotalColumn;

    private final ControladoraProduto produtoService = new ControladoraProduto();
    private final ControladoraCliente clienteService = new ControladoraCliente();
    private final ControladoraVenda vendaService = new ControladoraVenda();
    private final ControladoraPagamento pagamentoService = new ControladoraPagamento();
    private final ControladoraComprovante comprovanteService = new ControladoraComprovante();
    private final ControladoraAutenticacao authService = new ControladoraAutenticacao();
    private final ObservableList<ItemVenda> itens = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        configurarTabelas();
        clienteComboBox.setItems(FXCollections.observableArrayList(clienteService.listarTodos()));
        formaPagamentoComboBox.setItems(FXCollections.observableArrayList(FormaPagamento.values()));
        formaPagamentoComboBox.valueProperty().addListener((obs, antiga, nova) -> atualizarTroco());
        valorRecebidoField.textProperty().addListener((obs, antigo, novo) -> atualizarTroco());
        itensTable.setItems(itens);
        buscarProdutos();
        atualizarTotal();
    }

    @FXML
    private void buscarProdutos() {
        produtosTable.setItems(FXCollections.observableArrayList(produtoService.buscar(buscaProdutoField.getText())));
    }

    @FXML
    private void adicionarItem() {
        try {
            Produto produto = produtosTable.getSelectionModel().getSelectedItem();
            if (produto == null) {
                mensagemLabel.setText("Selecione um produto.");
                return;
            }
            if (!Boolean.TRUE.equals(produto.getAtivo())) {
                mensagemLabel.setText("Produto inativo não pode ser vendido.");
                return;
            }

            int quantidade = parseQuantidade();
            if (quantidade > produto.getQuantidadeEstoque()) {
                mensagemLabel.setText("Estoque insuficiente. Quantidade disponível: " + produto.getQuantidadeEstoque() + ".");
                return;
            }

            ItemVenda itemExistente = buscarItem(produto);
            if (itemExistente != null) {
                int novaQuantidade = itemExistente.getQuantidade() + quantidade;
                if (novaQuantidade > produto.getQuantidadeEstoque()) {
                    mensagemLabel.setText("Estoque insuficiente. Quantidade disponível: " + produto.getQuantidadeEstoque() + ".");
                    return;
                }
                itemExistente.setQuantidade(novaQuantidade);
                itemExistente.setSubtotal(itemExistente.getPrecoUnitario().multiply(BigDecimal.valueOf(novaQuantidade)));
                itensTable.refresh();
            } else {
                ItemVenda item = new ItemVenda();
                item.setProduto(produto);
                item.setQuantidade(quantidade);
                item.setPrecoUnitario(produto.getPrecoVenda());
                item.setSubtotal(produto.getPrecoVenda().multiply(BigDecimal.valueOf(quantidade)));
                itens.add(item);
            }

            quantidadeField.clear();
            mensagemLabel.setText("Item adicionado à venda.");
            atualizarTotal();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void removerItem() {
        ItemVenda item = itensTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            itens.remove(item);
            atualizarTotal();
            mensagemLabel.setText("Item removido.");
        }
    }

    @FXML
    private void confirmarVenda() {
        try {
            BigDecimal valorRecebido = parseValorRecebido();
            Venda venda = vendaService.registrarVenda(itens, formaPagamentoComboBox.getValue(), valorRecebido, clienteComboBox.getValue());
            String comprovante = comprovanteService.gerarComprovante(venda, itens);
            exibirComprovante(comprovante);
            cancelarVenda();
            buscarProdutos();
            mensagemLabel.setText("Venda registrada com sucesso.");
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void cancelarVenda() {
        itens.clear();
        quantidadeField.clear();
        valorRecebidoField.clear();
        formaPagamentoComboBox.getSelectionModel().clearSelection();
        clienteComboBox.getSelectionModel().clearSelection();
        produtosTable.getSelectionModel().clearSelection();
        atualizarTotal();
        mensagemLabel.setText("Venda cancelada.");
    }

    @FXML
    private void voltarDashboard() {
        ControladorNavegacao.abrirDashboard();
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
    private void sair() {
        authService.logout();
        ControladorNavegacao.abrirLogin();
    }

    private void configurarTabelas() {
        produtoCodigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigoProduto"));
        produtoNomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        produtoPrecoColumn.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        produtoEstoqueColumn.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));

        itemCodigoColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduto().getCodigoProduto()));
        itemNomeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduto().getNome()));
        itemQuantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        itemPrecoColumn.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        itemSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private ItemVenda buscarItem(Produto produto) {
        return itens.stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .findFirst()
                .orElse(null);
    }

    private int parseQuantidade() {
        if (quantidadeField.getText() == null || quantidadeField.getText().isBlank()) {
            throw new IllegalArgumentException("Informe a quantidade.");
        }
        try {
            int quantidade = Integer.parseInt(quantidadeField.getText().trim());
            if (quantidade <= 0) {
                throw new NumberFormatException();
            }
            return quantidade;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe uma quantidade válida.");
        }
    }

    private BigDecimal parseValorRecebido() {
        if (formaPagamentoComboBox.getValue() != FormaPagamento.DINHEIRO) {
            return null;
        }
        if (valorRecebidoField.getText() == null || valorRecebidoField.getText().isBlank()) {
            throw new IllegalArgumentException("Informe o valor recebido.");
        }
        try {
            return new BigDecimal(valorRecebidoField.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um valor recebido válido.");
        }
    }

    private void atualizarTotal() {
        BigDecimal total = vendaService.calcularTotal(itens);
        totalLabel.setText(MOEDA.format(total));
        atualizarTroco();
    }

    private void atualizarTroco() {
        if (trocoLabel == null || totalLabel == null) {
            return;
        }
        BigDecimal total = vendaService.calcularTotal(itens);
        BigDecimal recebido;
        try {
            recebido = valorRecebidoField.getText() == null || valorRecebidoField.getText().isBlank()
                    ? null
                    : new BigDecimal(valorRecebidoField.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            trocoLabel.setText(MOEDA.format(BigDecimal.ZERO));
            return;
        }
        trocoLabel.setText(MOEDA.format(pagamentoService.calcularTroco(total, recebido)));
    }

    private void exibirComprovante(String comprovante) {
        TextArea area = new TextArea(comprovante);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(520);
        area.setPrefHeight(360);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comprovante");
        alert.setHeaderText("Venda registrada");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }
}
