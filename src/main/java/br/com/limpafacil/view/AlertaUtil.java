package br.com.limpafacil.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class AlertaUtil {
    private AlertaUtil() {
    }

    public static void sucesso(String mensagem) {
        exibir(Alert.AlertType.INFORMATION, "Sucesso", mensagem);
    }

    public static void erro(String mensagem) {
        exibir(Alert.AlertType.ERROR, "Erro", mensagem);
    }

    public static boolean confirmar(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private static void exibir(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
