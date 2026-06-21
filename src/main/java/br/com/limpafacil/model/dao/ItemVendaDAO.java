package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.entity.Categoria;
import br.com.limpafacil.model.entity.ItemVenda;
import br.com.limpafacil.model.entity.Produto;
import br.com.limpafacil.model.entity.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemVendaDAO {
    public void inserir(Connection conexao, ItemVenda itemVenda) throws SQLException {
        String sql = """
                INSERT INTO itens_venda (venda_id, produto_id, quantidade, preco_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, itemVenda.getVenda().getId());
            stmt.setInt(2, itemVenda.getProduto().getId());
            stmt.setInt(3, itemVenda.getQuantidade());
            stmt.setBigDecimal(4, itemVenda.getPrecoUnitario());
            stmt.setBigDecimal(5, itemVenda.getSubtotal());
            stmt.executeUpdate();
        }
    }

    public List<ItemVenda> listarPorVenda(Integer vendaId) {
        String sql = """
                SELECT iv.id, iv.quantidade, iv.preco_unitario, iv.subtotal,
                       p.id AS produto_id, p.codigo_produto, p.nome AS produto_nome, p.marca,
                       p.preco_venda, p.quantidade_estoque, p.nivel_minimo, p.ativo,
                       c.id AS categoria_id, c.codigo AS categoria_codigo, c.nome AS categoria_nome
                FROM itens_venda iv
                JOIN produtos p ON p.id = iv.produto_id
                JOIN categorias c ON c.id = p.categoria_id
                WHERE iv.venda_id = ?
                ORDER BY iv.id
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, vendaId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<ItemVenda> itens = new ArrayList<>();
                while (rs.next()) {
                    itens.add(mapear(rs));
                }
                return itens;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar itens da venda.", e);
        }
    }

    private ItemVenda mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("categoria_id"),
                rs.getString("categoria_codigo"),
                rs.getString("categoria_nome")
        );

        Produto produto = new Produto();
        produto.setId(rs.getInt("produto_id"));
        produto.setCodigoProduto(rs.getString("codigo_produto"));
        produto.setNome(rs.getString("produto_nome"));
        produto.setMarca(rs.getString("marca"));
        produto.setCategoria(categoria);
        produto.setPrecoVenda(rs.getBigDecimal("preco_venda"));
        produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        produto.setNivelMinimo(rs.getInt("nivel_minimo"));
        produto.setAtivo(rs.getBoolean("ativo"));

        ItemVenda item = new ItemVenda();
        item.setId(rs.getInt("id"));
        item.setProduto(produto);
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
        item.setSubtotal(rs.getBigDecimal("subtotal"));
        item.setVenda(new Venda());
        return item;
    }
}
