package br.com.limpafacil.view;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.control.ControladoraCliente;
import br.com.limpafacil.model.entity.Cliente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClienteViewController {
    @FXML private TextField codigoField;
    @FXML private TextField nomeField;
    @FXML private TextField cpfCnpjField;
    @FXML private TextField enderecoField;
    @FXML private TextField cidadeField;
    @FXML private TextField estadoField;
    @FXML private TextField telefoneField;
    @FXML private TextField emailField;
    @FXML private TextField buscaField;
    @FXML private CheckBox ativoCheckBox;
    @FXML private Label mensagemLabel;
    @FXML private TableView<Cliente> clientesTable;
    @FXML private TableColumn<Cliente, String> codigoColumn;
    @FXML private TableColumn<Cliente, String> nomeColumn;
    @FXML private TableColumn<Cliente, String> documentoColumn;
    @FXML private TableColumn<Cliente, String> cidadeColumn;
    @FXML private TableColumn<Cliente, String> telefoneColumn;
    @FXML private TableColumn<Cliente, Boolean> ativoColumn;

    private final ControladoraCliente controladoraCliente = new ControladoraCliente();
    private Cliente clienteSelecionado;

    @FXML
    private void initialize() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        documentoColumn.setCellValueFactory(new PropertyValueFactory<>("cpfCnpj"));
        cidadeColumn.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        telefoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        ativoColumn.setCellValueFactory(new PropertyValueFactory<>("ativo"));
        clientesTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> selecionar(novo));
        carregarTabela();
        limpar();
    }

    @FXML
    private void salvar() {
        try {
            Cliente cliente = clienteSelecionado == null ? new Cliente() : clienteSelecionado;
            cliente.setCodigo(codigoField.getText());
            cliente.setNome(nomeField.getText());
            cliente.setCpfCnpj(cpfCnpjField.getText());
            cliente.setEndereco(enderecoField.getText());
            cliente.setCidade(cidadeField.getText());
            cliente.setEstado(estadoField.getText());
            cliente.setTelefone(telefoneField.getText());
            cliente.setEmail(emailField.getText());
            cliente.setAtivo(ativoCheckBox.isSelected());
            controladoraCliente.salvar(cliente);
            mensagemLabel.setText("Cliente salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void inativar() {
        try {
            if (clienteSelecionado == null) {
                mensagemLabel.setText("Selecione um cliente para inativar.");
                return;
            }
            controladoraCliente.inativar(clienteSelecionado.getId());
            mensagemLabel.setText("Cliente inativado com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void buscar() {
        clientesTable.setItems(FXCollections.observableArrayList(controladoraCliente.buscar(buscaField.getText())));
    }

    @FXML
    private void limpar() {
        clienteSelecionado = null;
        clientesTable.getSelectionModel().clearSelection();
        codigoField.clear();
        nomeField.clear();
        cpfCnpjField.clear();
        enderecoField.clear();
        cidadeField.clear();
        estadoField.clear();
        telefoneField.clear();
        emailField.clear();
        ativoCheckBox.setSelected(true);
    }

    @FXML private void voltarDashboard() { ControladorNavegacao.abrirDashboard(); }
    @FXML private void abrirProdutos() { ControladorNavegacao.abrirProdutos(); }
    @FXML private void abrirVenda() { ControladorNavegacao.abrirVenda(); }

    private void carregarTabela() {
        clientesTable.setItems(FXCollections.observableArrayList(controladoraCliente.listarTodos()));
    }

    private void selecionar(Cliente cliente) {
        clienteSelecionado = cliente;
        if (cliente == null) {
            return;
        }
        codigoField.setText(cliente.getCodigo());
        nomeField.setText(cliente.getNome());
        cpfCnpjField.setText(cliente.getCpfCnpj());
        enderecoField.setText(cliente.getEndereco());
        cidadeField.setText(cliente.getCidade());
        estadoField.setText(cliente.getEstado());
        telefoneField.setText(cliente.getTelefone());
        emailField.setText(cliente.getEmail());
        ativoCheckBox.setSelected(Boolean.TRUE.equals(cliente.getAtivo()));
    }
}
