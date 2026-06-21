package br.com.limpafacil.view;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.control.ControladoraCliente;
import br.com.limpafacil.model.control.ControladoraDevolucao;
import br.com.limpafacil.model.dao.ItemVendaDAO;
import br.com.limpafacil.model.entity.Cliente;
import br.com.limpafacil.model.entity.Devolucao;
import br.com.limpafacil.model.entity.ItemVenda;
import br.com.limpafacil.model.entity.Venda;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class DevolucaoViewController {
    @FXML private TextField codigoVendaField;
    @FXML private ComboBox<Cliente> clienteComboBox;
    @FXML private TextField quantidadeField;
    @FXML private TextArea motivoArea;
    @FXML private Label vendaLabel;
    @FXML private Label mensagemLabel;
    @FXML private TableView<ItemVenda> itensTable;
    @FXML private TableColumn<ItemVenda, String> produtoColumn;
    @FXML private TableColumn<ItemVenda, Integer> quantidadeColumn;
    @FXML private TableColumn<ItemVenda, BigDecimal> valorColumn;

    private final ControladoraDevolucao controladoraDevolucao = new ControladoraDevolucao();
    private final ControladoraCliente controladoraCliente = new ControladoraCliente();
    private final ItemVendaDAO itemVendaDAO = new ItemVendaDAO();
    private Venda vendaSelecionada;

    @FXML
    private void initialize() {
        clienteComboBox.setItems(FXCollections.observableArrayList(controladoraCliente.listarTodos()));
        produtoColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduto().getNome()));
        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
    }

    @FXML
    private void buscarVenda() {
        try {
            vendaSelecionada = controladoraDevolucao.buscarVendaPorCodigo(codigoVendaField.getText());
            vendaLabel.setText(vendaSelecionada.getCodigoVenda() + " | Total R$ " + vendaSelecionada.getValorTotal());
            itensTable.setItems(FXCollections.observableArrayList(itemVendaDAO.listarPorVenda(vendaSelecionada.getId())));
            mensagemLabel.setText("Venda carregada.");
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void registrarDevolucao() {
        try {
            ItemVenda item = itensTable.getSelectionModel().getSelectedItem();
            if (item == null) {
                mensagemLabel.setText("Selecione um item da venda.");
                return;
            }
            Devolucao devolucao = controladoraDevolucao.registrarDevolucao(
                    vendaSelecionada,
                    item.getProduto(),
                    parseQuantidade(),
                    motivoArea.getText(),
                    clienteComboBox.getValue()
            );
            mensagemLabel.setText("Devolução registrada: " + devolucao.getCodigoDevolucao());
            quantidadeField.clear();
            motivoArea.clear();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML private void voltarDashboard() { ControladorNavegacao.abrirDashboard(); }

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
