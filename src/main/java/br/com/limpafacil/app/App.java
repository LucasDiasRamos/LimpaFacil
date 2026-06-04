package br.com.limpafacil.app;

import br.com.limpafacil.util.NavegacaoUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        NavegacaoUtil.configurarStage(stage);
        NavegacaoUtil.abrirLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
