package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {
    public List<String> vendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        String sql = """
                SELECT v.codigo_venda, v.data_hora, u.nome AS funcionario, v.forma_pagamento, v.valor_total
                FROM vendas v
                JOIN usuarios u ON u.id = v.funcionario_id
                WHERE v.data_hora::date BETWEEN ? AND ?
                ORDER BY v.data_hora
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> linhas = new ArrayList<>();
                while (rs.next()) {
                    linhas.add(rs.getString("codigo_venda") + " | " + rs.getTimestamp("data_hora")
                            + " | " + rs.getString("funcionario")
                            + " | " + rs.getString("forma_pagamento")
                            + " | R$ " + rs.getBigDecimal("valor_total"));
                }
                return linhas;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar relatório de vendas.", e);
        }
    }

    public List<String> produtosMaisVendidos() {
        String sql = """
                SELECT p.nome, p.marca, c.nome AS categoria, sum(iv.quantidade) AS quantidade,
                       sum(iv.subtotal) AS total
                FROM itens_venda iv
                JOIN produtos p ON p.id = iv.produto_id
                JOIN categorias c ON c.id = p.categoria_id
                GROUP BY p.nome, p.marca, c.nome
                ORDER BY quantidade DESC, total DESC
                """;
        return consultarLinhas(sql, "nome", "marca", "categoria", "quantidade", "total");
    }

    public List<String> estoque() {
        String sql = """
                SELECT c.nome AS categoria, p.nome, p.marca, p.preco_venda, p.quantidade_estoque, p.nivel_minimo,
                       CASE WHEN p.quantidade_estoque <= p.nivel_minimo THEN 'MÍNIMO' ELSE 'OK' END AS status
                FROM produtos p
                JOIN categorias c ON c.id = p.categoria_id
                ORDER BY c.nome, p.nome
                """;
        return consultarLinhas(sql, "categoria", "nome", "marca", "preco_venda", "quantidade_estoque", "nivel_minimo", "status");
    }

    public List<String> historicoCliente(String termoCliente) {
        String sql = """
                SELECT COALESCE(cl.nome, 'Cliente não informado') AS cliente, v.data_hora,
                       string_agg(p.nome || ' x' || iv.quantidade, ', ') AS produtos,
                       v.valor_total, v.forma_pagamento
                FROM vendas v
                LEFT JOIN clientes cl ON cl.id = v.cliente_id
                JOIN itens_venda iv ON iv.venda_id = v.id
                JOIN produtos p ON p.id = iv.produto_id
                WHERE ? = '' OR lower(cl.nome) LIKE lower(?) OR lower(cl.cpf_cnpj) LIKE lower(?)
                GROUP BY cl.nome, v.data_hora, v.valor_total, v.forma_pagamento
                ORDER BY v.data_hora DESC
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String termo = termoCliente == null ? "" : termoCliente.trim();
            String filtro = "%" + termo + "%";
            stmt.setString(1, termo);
            stmt.setString(2, filtro);
            stmt.setString(3, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> linhas = new ArrayList<>();
                while (rs.next()) {
                    linhas.add(rs.getString("cliente") + " | " + rs.getTimestamp("data_hora")
                            + " | " + rs.getString("produtos")
                            + " | R$ " + rs.getBigDecimal("valor_total")
                            + " | " + rs.getString("forma_pagamento"));
                }
                return linhas;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar histórico do cliente.", e);
        }
    }

    public List<String> fluxoCaixa(LocalDate inicio, LocalDate fim) {
        String sql = """
                SELECT data, tipo, descricao, valor
                FROM (
                    SELECT v.data_hora::date AS data, 'ENTRADA' AS tipo, v.codigo_venda AS descricao, v.valor_total AS valor
                    FROM vendas v
                    WHERE v.data_hora::date BETWEEN ? AND ?
                    UNION ALL
                    SELECT d.data_hora::date AS data, 'SAÍDA' AS tipo, d.codigo_devolucao AS descricao, -d.valor_total AS valor
                    FROM devolucoes d
                    WHERE d.data_hora::date BETWEEN ? AND ?
                ) movimento
                ORDER BY data, tipo
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));
            stmt.setDate(3, Date.valueOf(inicio));
            stmt.setDate(4, Date.valueOf(fim));
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> linhas = new ArrayList<>();
                var saldo = java.math.BigDecimal.ZERO;
                while (rs.next()) {
                    saldo = saldo.add(rs.getBigDecimal("valor"));
                    linhas.add(rs.getDate("data") + " | " + rs.getString("tipo")
                            + " | " + rs.getString("descricao")
                            + " | R$ " + rs.getBigDecimal("valor")
                            + " | Saldo: R$ " + saldo);
                }
                return linhas;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar fluxo de caixa.", e);
        }
    }

    private List<String> consultarLinhas(String sql, String... colunas) {
        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<String> linhas = new ArrayList<>();
            while (rs.next()) {
                List<String> valores = new ArrayList<>();
                for (String coluna : colunas) {
                    valores.add(String.valueOf(rs.getObject(coluna)));
                }
                linhas.add(String.join(" | ", valores));
            }
            return linhas;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar relatório.", e);
        }
    }
}
