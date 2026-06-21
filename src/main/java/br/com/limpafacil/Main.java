package br.com.limpafacil;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.config.ConexaoPostgres;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        ConexaoPostgres.inicializarSchema();
        ControladorNavegacao.configurarStage(stage);
        ControladorNavegacao.abrirLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
