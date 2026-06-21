package br.com.limpafacil.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class ControladorNavegacao {
    private static Stage stagePrincipal;

    private ControladorNavegacao() {
    }

    public static void configurarStage(Stage stage) {
        stagePrincipal = stage;
        stagePrincipal.setTitle("LimpaFácil");
        stagePrincipal.setMinWidth(960);
        stagePrincipal.setMinHeight(640);
    }

    public static void abrirLogin() {
        trocarCena("login.fxml");
        stagePrincipal.setWidth(480);
        stagePrincipal.setHeight(420);
        stagePrincipal.centerOnScreen();
    }

    public static void abrirDashboard() {
        trocarCena("dashboard.fxml");
        stagePrincipal.setWidth(1100);
        stagePrincipal.setHeight(720);
        stagePrincipal.centerOnScreen();
    }

    public static void abrirCategorias() {
        trocarCena("categorias.fxml");
    }

    public static void abrirProdutos() {
        trocarCena("produtos.fxml");
    }

    public static void abrirVenda() {
        trocarCena("venda.fxml");
    }

    public static void abrirClientes() {
        trocarCena("clientes.fxml");
    }

    public static void abrirFornecedores() {
        trocarCena("fornecedores.fxml");
    }

    public static void abrirFuncionarios() {
        trocarCena("funcionarios.fxml");
    }

    public static void abrirOrcamentos() {
        trocarCena("orcamentos.fxml");
    }

    public static void abrirDevolucoes() {
        trocarCena("devolucoes.fxml");
    }

    public static void abrirRelatorios() {
        trocarCena("relatorios.fxml");
    }

    private static void trocarCena(String arquivoFxml) {
        try {
            Parent root = FXMLLoader.load(ControladorNavegacao.class.getResource("/br/com/limpafacil/view/" + arquivoFxml));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(ControladorNavegacao.class.getResource("/br/com/limpafacil/view/style.css").toExternalForm());
            stagePrincipal.setScene(scene);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Não foi possível abrir a tela " + arquivoFxml, e);
        }
    }
}
