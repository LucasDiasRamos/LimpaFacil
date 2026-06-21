package br.com.limpafacil.model.control;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.dao.DevolucaoDAO;
import br.com.limpafacil.model.dao.ItemDevolucaoDAO;
import br.com.limpafacil.model.dao.ProdutoDAO;
import br.com.limpafacil.model.dao.VendaDAO;
import br.com.limpafacil.model.entity.Cliente;
import br.com.limpafacil.model.entity.Devolucao;
import br.com.limpafacil.model.entity.ItemDevolucao;
import br.com.limpafacil.model.entity.Produto;
import br.com.limpafacil.model.entity.Usuario;
import br.com.limpafacil.model.entity.Venda;
import br.com.limpafacil.model.util.GeradorCodigo;
import br.com.limpafacil.model.util.SessaoUsuario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ControladoraDevolucao {
    private final VendaDAO vendaDAO = new VendaDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final DevolucaoDAO devolucaoDAO = new DevolucaoDAO();
    private final ItemDevolucaoDAO itemDevolucaoDAO = new ItemDevolucaoDAO();

    public Venda buscarVendaPorCodigo(String codigoVenda) {
        if (codigoVenda == null || codigoVenda.isBlank()) {
            throw new IllegalArgumentException("Informe o código da venda.");
        }
        Venda venda = vendaDAO.buscarPorCodigo(codigoVenda.trim());
        if (venda == null) {
            throw new IllegalArgumentException("Venda não encontrada.");
        }
        return venda;
    }

    public Devolucao registrarDevolucao(Venda venda, Produto produto, Integer quantidade, String motivo, Cliente cliente) {
        if (venda == null || venda.getId() == null) {
            throw new IllegalArgumentException("Selecione uma venda.");
        }
        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException("Selecione um produto vendido.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Informe uma quantidade válida para devolução.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo da devolução.");
        }
        Usuario funcionario = SessaoUsuario.getUsuarioLogado();
        if (funcionario == null || funcionario.getId() == null) {
            throw new IllegalArgumentException("Nenhum usuário logado para registrar devolução.");
        }

        BigDecimal valorUnitario = produto.getPrecoVenda();
        BigDecimal subtotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));

        Devolucao devolucao = new Devolucao();
        devolucao.setCodigoDevolucao(GeradorCodigo.gerar("DEV"));
        devolucao.setVenda(venda);
        devolucao.setCliente(cliente);
        devolucao.setFuncionario(funcionario);
        devolucao.setDataHora(LocalDateTime.now());
        devolucao.setMotivo(motivo.trim());
        devolucao.setValorTotal(subtotal);

        ItemDevolucao item = new ItemDevolucao();
        item.setDevolucao(devolucao);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        item.setSubtotal(subtotal);

        try (Connection conexao = ConexaoPostgres.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                devolucao.setId(devolucaoDAO.inserir(conexao, devolucao));
                itemDevolucaoDAO.inserir(conexao, item);
                Produto produtoAtual = produtoDAO.buscarPorId(conexao, produto.getId());
                produtoDAO.atualizarEstoque(conexao, produto.getId(), produtoAtual.getQuantidadeEstoque() + quantidade);
                conexao.commit();
                return devolucao;
            } catch (RuntimeException | SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao registrar devolução.", e);
        }
    }
}
