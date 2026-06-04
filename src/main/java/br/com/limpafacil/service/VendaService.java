package br.com.limpafacil.service;

import br.com.limpafacil.config.ConexaoPostgres;
import br.com.limpafacil.dao.ItemVendaDAO;
import br.com.limpafacil.dao.ProdutoDAO;
import br.com.limpafacil.dao.VendaDAO;
import br.com.limpafacil.model.FormaPagamento;
import br.com.limpafacil.model.ItemVenda;
import br.com.limpafacil.model.Produto;
import br.com.limpafacil.model.StatusVenda;
import br.com.limpafacil.model.Usuario;
import br.com.limpafacil.model.Venda;
import br.com.limpafacil.util.SessaoUsuario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VendaService {
    private static final DateTimeFormatter CODIGO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final VendaDAO vendaDAO = new VendaDAO();
    private final ItemVendaDAO itemVendaDAO = new ItemVendaDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final PagamentoService pagamentoService = new PagamentoService();

    public Venda registrarVenda(List<ItemVenda> itens, FormaPagamento formaPagamento, BigDecimal valorRecebido) {
        validarItens(itens);
        BigDecimal valorTotal = calcularTotal(itens);
        pagamentoService.confirmarPagamento(formaPagamento, valorTotal, valorRecebido);

        Usuario funcionario = SessaoUsuario.getUsuarioLogado();
        if (funcionario == null || funcionario.getId() == null) {
            throw new IllegalArgumentException("Nenhum usuário logado para registrar a venda.");
        }

        Venda venda = new Venda();
        venda.setCodigoVenda(gerarCodigoVenda());
        venda.setDataHora(LocalDateTime.now());
        venda.setFuncionario(funcionario);
        venda.setValorTotal(valorTotal);
        venda.setFormaPagamento(formaPagamento);
        venda.setStatus(StatusVenda.REGISTRADA);

        try (Connection conexao = ConexaoPostgres.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                Integer vendaId = vendaDAO.inserir(conexao, venda);
                venda.setId(vendaId);

                for (ItemVenda item : itens) {
                    Produto produtoAtual = produtoDAO.buscarPorId(conexao, item.getProduto().getId());
                    if (produtoAtual == null || !Boolean.TRUE.equals(produtoAtual.getAtivo())) {
                        throw new IllegalArgumentException("Produto indisponível: " + item.getProduto().getNome());
                    }
                    if (item.getQuantidade() > produtoAtual.getQuantidadeEstoque()) {
                        throw new IllegalArgumentException("Estoque insuficiente para " + produtoAtual.getNome()
                                + ". Quantidade disponível: " + produtoAtual.getQuantidadeEstoque() + ".");
                    }

                    item.setVenda(venda);
                    item.setPrecoUnitario(item.getPrecoUnitario());
                    item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                    itemVendaDAO.inserir(conexao, item);

                    int novoEstoque = produtoAtual.getQuantidadeEstoque() - item.getQuantidade();
                    produtoDAO.atualizarEstoque(conexao, produtoAtual.getId(), novoEstoque);
                }

                conexao.commit();
                return venda;
            } catch (RuntimeException | SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao registrar venda.", e);
        }
    }

    public BigDecimal calcularTotal(List<ItemVenda> itens) {
        if (itens == null) {
            return BigDecimal.ZERO;
        }
        return itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarItens(List<ItemVenda> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item à venda.");
        }
        for (ItemVenda item : itens) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new IllegalArgumentException("Item de venda sem produto.");
            }
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Informe uma quantidade válida para " + item.getProduto().getNome() + ".");
            }
            if (item.getPrecoUnitario() == null || item.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Produto sem preço válido: " + item.getProduto().getNome() + ".");
            }
        }
    }

    private String gerarCodigoVenda() {
        return "VENDA-" + LocalDateTime.now().format(CODIGO_FORMATTER);
    }
}
