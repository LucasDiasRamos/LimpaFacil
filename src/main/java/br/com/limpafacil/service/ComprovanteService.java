package br.com.limpafacil.service;

import br.com.limpafacil.model.ItemVenda;
import br.com.limpafacil.model.Venda;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ComprovanteService {
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public String gerarComprovante(Venda venda, List<ItemVenda> itens) {
        StringBuilder comprovante = new StringBuilder();
        comprovante.append("LimpaFácil\n");
        comprovante.append("Comprovante de venda\n\n");
        comprovante.append("Código: ").append(venda.getCodigoVenda()).append("\n");
        comprovante.append("Data: ").append(venda.getDataHora().format(DATA_FORMATTER)).append("\n");
        comprovante.append("Funcionário: ").append(venda.getFuncionario().getNome()).append("\n");
        comprovante.append("Pagamento: ").append(venda.getFormaPagamento()).append("\n\n");
        comprovante.append("Itens\n");

        for (ItemVenda item : itens) {
            comprovante.append("- ")
                    .append(item.getProduto().getNome())
                    .append(" | Qtd: ").append(item.getQuantidade())
                    .append(" | Unitário: ").append(MOEDA.format(item.getPrecoUnitario()))
                    .append(" | Subtotal: ").append(MOEDA.format(item.getSubtotal()))
                    .append("\n");
        }

        comprovante.append("\nTotal: ").append(MOEDA.format(venda.getValorTotal()));
        return comprovante.toString();
    }
}
