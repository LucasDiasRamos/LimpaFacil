package br.com.limpafacil.service;

import br.com.limpafacil.model.FormaPagamento;

import java.math.BigDecimal;

public class PagamentoService {
    public boolean confirmarPagamento(FormaPagamento formaPagamento, BigDecimal valorTotal, BigDecimal valorRecebido) {
        if (formaPagamento == null) {
            throw new IllegalArgumentException("Selecione a forma de pagamento.");
        }

        if (formaPagamento == FormaPagamento.DINHEIRO) {
            if (valorRecebido == null) {
                throw new IllegalArgumentException("Informe o valor recebido.");
            }
            if (valorRecebido.compareTo(valorTotal) < 0) {
                throw new IllegalArgumentException("Valor recebido menor que o valor total da venda.");
            }
        }

        return true;
    }

    public BigDecimal calcularTroco(BigDecimal valorTotal, BigDecimal valorRecebido) {
        if (valorRecebido == null || valorTotal == null) {
            return BigDecimal.ZERO;
        }
        if (valorRecebido.compareTo(valorTotal) < 0) {
            return BigDecimal.ZERO;
        }
        return valorRecebido.subtract(valorTotal);
    }
}
