package br.com.limpafacil.view;

import br.com.limpafacil.model.control.ControladoraAutenticacao;
import br.com.limpafacil.controller.ControladorNavegacao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private Label mensagemLabel;

    private final ControladoraAutenticacao authService = new ControladoraAutenticacao();

    @FXML
    private void entrar() {
        try {
            mensagemLabel.setText("");
            authService.login(emailField.getText(), senhaField.getText());
            ControladorNavegacao.abrirDashboard();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }
}
