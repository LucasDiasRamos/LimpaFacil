package br.com.limpafacil.view;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.control.ControladoraFuncionario;
import br.com.limpafacil.model.entity.Funcionario;
import br.com.limpafacil.model.entity.PerfilUsuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FuncionarioViewController {
    @FXML private TextField codigoField;
    @FXML private TextField nomeField;
    @FXML private TextField enderecoField;
    @FXML private TextField cidadeField;
    @FXML private TextField estadoField;
    @FXML private TextField telefoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker contratacaoPicker;
    @FXML private TextField cargoField;
    @FXML private ComboBox<PerfilUsuario> perfilComboBox;
    @FXML private CheckBox ativoCheckBox;
    @FXML private TextField buscaField;
    @FXML private Label mensagemLabel;
    @FXML private TableView<Funcionario> funcionariosTable;
    @FXML private TableColumn<Funcionario, String> codigoColumn;
    @FXML private TableColumn<Funcionario, String> nomeColumn;
    @FXML private TableColumn<Funcionario, String> emailColumn;
    @FXML private TableColumn<Funcionario, String> cargoColumn;
    @FXML private TableColumn<Funcionario, PerfilUsuario> perfilColumn;

    private final ControladoraFuncionario controladoraFuncionario = new ControladoraFuncionario();
    private Funcionario funcionarioSelecionado;

    @FXML
    private void initialize() {
        perfilComboBox.setItems(FXCollections.observableArrayList(PerfilUsuario.values()));
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cargoColumn.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        perfilColumn.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        funcionariosTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> selecionar(novo));
        carregarTabela();
        limpar();
    }

    @FXML
    private void salvar() {
        try {
            Funcionario funcionario = funcionarioSelecionado == null ? new Funcionario() : funcionarioSelecionado;
            funcionario.setCodigo(codigoField.getText());
            funcionario.setNome(nomeField.getText());
            funcionario.setEndereco(enderecoField.getText());
            funcionario.setCidade(cidadeField.getText());
            funcionario.setEstado(estadoField.getText());
            funcionario.setTelefone(telefoneField.getText());
            funcionario.setEmail(emailField.getText());
            funcionario.setDataContratacao(contratacaoPicker.getValue());
            funcionario.setCargo(cargoField.getText());
            funcionario.setPerfil(perfilComboBox.getValue());
            funcionario.setAtivo(ativoCheckBox.isSelected());
            controladoraFuncionario.salvar(funcionario);
            mensagemLabel.setText("Funcionário salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void inativar() {
        try {
            if (funcionarioSelecionado == null) {
                mensagemLabel.setText("Selecione um funcionário para inativar.");
                return;
            }
            controladoraFuncionario.inativar(funcionarioSelecionado.getId());
            mensagemLabel.setText("Funcionário inativado com sucesso.");
            limpar();
            carregarTabela();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML private void buscar() { funcionariosTable.setItems(FXCollections.observableArrayList(controladoraFuncionario.buscar(buscaField.getText()))); }
    @FXML private void voltarDashboard() { ControladorNavegacao.abrirDashboard(); }
    @FXML private void limpar() {
        funcionarioSelecionado = null;
        funcionariosTable.getSelectionModel().clearSelection();
        codigoField.clear();
        nomeField.clear();
        enderecoField.clear();
        cidadeField.clear();
        estadoField.clear();
        telefoneField.clear();
        emailField.clear();
        contratacaoPicker.setValue(null);
        cargoField.clear();
        perfilComboBox.getSelectionModel().clearSelection();
        ativoCheckBox.setSelected(true);
    }

    private void carregarTabela() {
        funcionariosTable.setItems(FXCollections.observableArrayList(controladoraFuncionario.listarTodos()));
    }

    private void selecionar(Funcionario funcionario) {
        funcionarioSelecionado = funcionario;
        if (funcionario == null) {
            return;
        }
        codigoField.setText(funcionario.getCodigo());
        nomeField.setText(funcionario.getNome());
        enderecoField.setText(funcionario.getEndereco());
        cidadeField.setText(funcionario.getCidade());
        estadoField.setText(funcionario.getEstado());
        telefoneField.setText(funcionario.getTelefone());
        emailField.setText(funcionario.getEmail());
        contratacaoPicker.setValue(funcionario.getDataContratacao());
        cargoField.setText(funcionario.getCargo());
        perfilComboBox.setValue(funcionario.getPerfil());
        ativoCheckBox.setSelected(Boolean.TRUE.equals(funcionario.getAtivo()));
    }
}
