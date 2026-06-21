package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.entity.Orcamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class OrcamentoDAO {
    public Integer inserir(Connection conexao, Orcamento orcamento) throws SQLException {
        String sql = """
                INSERT INTO orcamentos
                (codigo_orcamento, data_hora, cliente_id, funcionario_id, valor_total, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, orcamento.getCodigoOrcamento());
            stmt.setTimestamp(2, Timestamp.valueOf(orcamento.getDataHora()));
            if (orcamento.getCliente() == null || orcamento.getCliente().getId() == null) {
                stmt.setObject(3, null);
            } else {
                stmt.setInt(3, orcamento.getCliente().getId());
            }
            stmt.setInt(4, orcamento.getFuncionario().getId());
            stmt.setBigDecimal(5, orcamento.getValorTotal());
            stmt.setString(6, orcamento.getStatus().name());
            stmt.executeUpdate();
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("ID do orçamento não foi gerado.");
            }
        }
    }
}
