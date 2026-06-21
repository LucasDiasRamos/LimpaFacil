package br.com.limpafacil.view;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.control.ControladoraFornecedor;
import br.com.limpafacil.model.entity.Fornecedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FornecedorViewController {
    @FXML private TextField codigoField;
    @FXML private TextField nomeField;
    @FXML private TextField fantasiaField;
    @FXML private TextField cpfCnpjField;
    @FXML private TextField enderecoField;
    @FXML private TextField cidadeField;
    @FXML private TextField estadoField;
    @FXML private TextField telefoneField;
    @FXML private TextField emailField;
    @FXML private TextField buscaField;
    @FXML private CheckBox ativoCheckBox;
    @FXML private Label mensagemLabel;
    @FXML private TableView<Fornecedor> fornecedoresTable;
    @FXML private TableColumn<Fornecedor, String> codigoColumn;
    @FXML private TableColumn<Fornecedor, String> nomeColumn;
    @FXML private TableColumn<Fornecedor, String> fantasiaColumn;
    @FXML private TableColumn<Fornecedor, String> documentoColumn;
    @FXML private TableColumn<Fornecedor, Boolean> ativoColumn;

    private final ControladoraFornecedor controladoraFornecedor = new ControladoraFornecedor();
    private Fornecedor fornecedorSelecionado;

    @FXML
    private void initialize() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        fantasiaColumn.setCellValueFactory(new PropertyValueFactory<>("nomeFantasia"));
        documentoColumn.setCellValueFactory(new PropertyValueFactory<>("cpfCnpj"));
        ativoColumn.setCellValueFactory(new PropertyValueFactory<>("ativo"));
        fornecedoresTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> selecionar(novo));
        carregarTabela();
        limpar();
    }

    @FXML
    private void salvar() {
        try {
            Fornecedor fornecedor = fornecedorSelecionado == null ? new Fornecedor() : fornecedorSelecionado;
            fornecedor.setCodigo(codigoField.getText());
            fornecedor.setNome(nomeField.getText());
            fornecedor.setNomeFantasia(fantasiaField.getText());
            fornecedor.setCpfCnpj(cpfCnpjField.getText());
            fornecedor.setEndereco(enderecoField.getText());
            fornecedor.setCidade(cidadeField.getText());
            fornecedor.setEstado(estadoField.getText());
            fornecedor.setTelefone(telefoneField.getText());
            fornecedor.setEmail(emailField.getText());
            fornecedor.setAtivo(ativoCheckBox.isSelected());
            controladoraFornecedor.salvar(fornecedor);
            mensagemLabel.setText("Fornecedor salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void inativar() {
        try {
            if (fornecedorSelecionado == null) {
                mensagemLabel.setText("Selecione um fornecedor para inativar.");
                return;
            }
            controladoraFornecedor.inativar(fornecedorSelecionado.getId());
            mensagemLabel.setText("Fornecedor inativado com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML private void buscar() { fornecedoresTable.setItems(FXCollections.observableArrayList(controladoraFornecedor.buscar(buscaField.getText()))); }
    @FXML private void voltarDashboard() { ControladorNavegacao.abrirDashboard(); }
    @FXML private void limpar() {
        fornecedorSelecionado = null;
        fornecedoresTable.getSelectionModel().clearSelection();
        codigoField.clear();
        nomeField.clear();
        fantasiaField.clear();
        cpfCnpjField.clear();
        enderecoField.clear();
        cidadeField.clear();
        estadoField.clear();
        telefoneField.clear();
        emailField.clear();
        ativoCheckBox.setSelected(true);
    }

    private void carregarTabela() {
        fornecedoresTable.setItems(FXCollections.observableArrayList(controladoraFornecedor.listarTodos()));
    }

    private void selecionar(Fornecedor fornecedor) {
        fornecedorSelecionado = fornecedor;
        if (fornecedor == null) {
            return;
        }
        codigoField.setText(fornecedor.getCodigo());
        nomeField.setText(fornecedor.getNome());
        fantasiaField.setText(fornecedor.getNomeFantasia());
        cpfCnpjField.setText(fornecedor.getCpfCnpj());
        enderecoField.setText(fornecedor.getEndereco());
        cidadeField.setText(fornecedor.getCidade());
        estadoField.setText(fornecedor.getEstado());
        telefoneField.setText(fornecedor.getTelefone());
        emailField.setText(fornecedor.getEmail());
        ativoCheckBox.setSelected(Boolean.TRUE.equals(fornecedor.getAtivo()));
    }
}
