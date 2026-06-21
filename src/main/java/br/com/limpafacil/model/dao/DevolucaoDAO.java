package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.entity.Devolucao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class DevolucaoDAO {
    public Integer inserir(Connection conexao, Devolucao devolucao) throws SQLException {
        String sql = """
                INSERT INTO devolucoes
                (codigo_devolucao, venda_id, cliente_id, funcionario_id, data_hora, motivo, valor_total)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, devolucao.getCodigoDevolucao());
            stmt.setInt(2, devolucao.getVenda().getId());
            if (devolucao.getCliente() == null || devolucao.getCliente().getId() == null) {
                stmt.setObject(3, null);
            } else {
                stmt.setInt(3, devolucao.getCliente().getId());
            }
            stmt.setInt(4, devolucao.getFuncionario().getId());
            stmt.setTimestamp(5, Timestamp.valueOf(devolucao.getDataHora()));
            stmt.setString(6, devolucao.getMotivo());
            stmt.setBigDecimal(7, devolucao.getValorTotal());
            stmt.executeUpdate();
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("ID da devolução não foi gerado.");
            }
        }
    }
}
