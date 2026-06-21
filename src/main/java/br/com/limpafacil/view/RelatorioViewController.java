package br.com.limpafacil.view;

import br.com.limpafacil.controller.ControladorNavegacao;
import br.com.limpafacil.model.control.ControladoraRelatorio;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

public class RelatorioViewController {
    @FXML private DatePicker dataInicialPicker;
    @FXML private DatePicker dataFinalPicker;
    @FXML private TextField clienteField;
    @FXML private TextArea resultadoArea;
    @FXML private Label mensagemLabel;

    private final ControladoraRelatorio controladoraRelatorio = new ControladoraRelatorio();

    @FXML
    private void initialize() {
        dataInicialPicker.setValue(LocalDate.now().minusMonths(1));
        dataFinalPicker.setValue(LocalDate.now());
    }

    @FXML private void vendasPeriodo() { executar(() -> controladoraRelatorio.vendasPorPeriodo(dataInicialPicker.getValue(), dataFinalPicker.getValue())); }
    @FXML private void produtosMaisVendidos() { executar(controladoraRelatorio::produtosMaisVendidos); }
    @FXML private void estoque() { executar(controladoraRelatorio::estoque); }
    @FXML private void historicoCliente() { executar(() -> controladoraRelatorio.historicoCliente(clienteField.getText())); }
    @FXML private void fluxoCaixa() { executar(() -> controladoraRelatorio.fluxoCaixa(dataInicialPicker.getValue(), dataFinalPicker.getValue())); }
    @FXML private void voltarDashboard() { ControladorNavegacao.abrirDashboard(); }

    private void exibir(List<String> linhas) {
        if (linhas.isEmpty()) {
            resultadoArea.setText("Nenhum dado encontrado para o filtro informado.");
        } else {
            resultadoArea.setText(String.join(System.lineSeparator(), linhas));
        }
        mensagemLabel.setText("Relatório gerado.");
    }

    private void executar(java.util.function.Supplier<List<String>> acao) {
        try {
            exibir(acao.get());
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }
}
