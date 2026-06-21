package br.com.limpafacil.model.control;

import br.com.limpafacil.model.dao.RelatorioDAO;

import java.time.LocalDate;
import java.util.List;

public class ControladoraRelatorio {
    private final RelatorioDAO relatorioDAO = new RelatorioDAO();

    public List<String> vendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return relatorioDAO.vendasPorPeriodo(inicio, fim);
    }

    public List<String> produtosMaisVendidos() {
        return relatorioDAO.produtosMaisVendidos();
    }

    public List<String> estoque() {
        return relatorioDAO.estoque();
    }

    public List<String> historicoCliente(String termoCliente) {
        return relatorioDAO.historicoCliente(termoCliente);
    }

    public List<String> fluxoCaixa(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return relatorioDAO.fluxoCaixa(inicio, fim);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe data inicial e data final.");
        }
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final deve ser maior ou igual à data inicial.");
        }
    }
}
