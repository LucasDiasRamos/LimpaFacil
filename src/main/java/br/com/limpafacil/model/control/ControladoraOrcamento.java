package br.com.limpafacil.model.control;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.dao.ItemOrcamentoDAO;
import br.com.limpafacil.model.dao.OrcamentoDAO;
import br.com.limpafacil.model.entity.Cliente;
import br.com.limpafacil.model.entity.ItemOrcamento;
import br.com.limpafacil.model.entity.Orcamento;
import br.com.limpafacil.model.entity.StatusOrcamento;
import br.com.limpafacil.model.entity.Usuario;
import br.com.limpafacil.model.util.GeradorCodigo;
import br.com.limpafacil.model.util.SessaoUsuario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ControladoraOrcamento {
    private final OrcamentoDAO orcamentoDAO = new OrcamentoDAO();
    private final ItemOrcamentoDAO itemOrcamentoDAO = new ItemOrcamentoDAO();

    public Orcamento registrarOrcamento(List<ItemOrcamento> itens, Cliente cliente) {
        validarItens(itens);
        Usuario funcionario = SessaoUsuario.getUsuarioLogado();
        if (funcionario == null || funcionario.getId() == null) {
            throw new IllegalArgumentException("Nenhum usuário logado para registrar orçamento.");
        }

        Orcamento orcamento = new Orcamento();
        orcamento.setCodigoOrcamento(GeradorCodigo.gerar("ORC"));
        orcamento.setDataHora(LocalDateTime.now());
        orcamento.setCliente(cliente);
        orcamento.setFuncionario(funcionario);
        orcamento.setValorTotal(calcularTotal(itens));
        orcamento.setStatus(StatusOrcamento.ABERTO);
        orcamento.setItens(itens);

        try (Connection conexao = ConexaoPostgres.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                orcamento.setId(orcamentoDAO.inserir(conexao, orcamento));
                for (ItemOrcamento item : itens) {
                    item.setOrcamento(orcamento);
                    itemOrcamentoDAO.inserir(conexao, item);
                }
                conexao.commit();
                return orcamento;
            } catch (RuntimeException | SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao registrar orçamento.", e);
        }
    }

    public BigDecimal calcularTotal(List<ItemOrcamento> itens) {
        if (itens == null) {
            return BigDecimal.ZERO;
        }
        return itens.stream().map(ItemOrcamento::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarItens(List<ItemOrcamento> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item ao orçamento.");
        }
        for (ItemOrcamento item : itens) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new IllegalArgumentException("Item de orçamento sem produto.");
            }
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Informe uma quantidade válida para " + item.getProduto().getNome() + ".");
            }
        }
    }
}
