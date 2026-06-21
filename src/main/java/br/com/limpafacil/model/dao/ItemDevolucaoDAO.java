package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.entity.ItemDevolucao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemDevolucaoDAO {
    public void inserir(Connection conexao, ItemDevolucao item) throws SQLException {
        String sql = """
                INSERT INTO itens_devolucao (devolucao_id, produto_id, quantidade, valor_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, item.getDevolucao().getId());
            stmt.setInt(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setBigDecimal(4, item.getValorUnitario());
            stmt.setBigDecimal(5, item.getSubtotal());
            stmt.executeUpdate();
        }
    }
}
