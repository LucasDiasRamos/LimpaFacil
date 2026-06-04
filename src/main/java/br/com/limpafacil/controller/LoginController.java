package br.com.limpafacil.controller;

import br.com.limpafacil.service.AuthService;
import br.com.limpafacil.util.NavegacaoUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private Label mensagemLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void entrar() {
        try {
            mensagemLabel.setText("");
            authService.login(emailField.getText(), senhaField.getText());
            NavegacaoUtil.abrirDashboard();
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }
}
