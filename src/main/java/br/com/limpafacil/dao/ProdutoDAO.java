package br.com.limpafacil.dao;

import br.com.limpafacil.config.ConexaoPostgres;
import br.com.limpafacil.model.Categoria;
import br.com.limpafacil.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    public void inserir(Produto produto) {
        String sql = """
                INSERT INTO produtos
                (codigo_produto, nome, marca, categoria_id, preco_venda, quantidade_estoque, nivel_minimo, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherStatement(produto, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir produto.", e);
        }
    }

    public void atualizar(Produto produto) {
        String sql = """
                UPDATE produtos
                SET codigo_produto = ?, nome = ?, marca = ?, categoria_id = ?, preco_venda = ?,
                    quantidade_estoque = ?, nivel_minimo = ?, ativo = ?
                WHERE id = ?
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherStatement(produto, stmt);
            stmt.setInt(9, produto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar produto.", e);
        }
    }

    public void excluir(Integer id) {
        String sql = "UPDATE produtos SET ativo = false WHERE id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inativar produto.", e);
        }
    }

    public Produto buscarPorId(Integer id) {
        String sql = sqlBase() + " WHERE p.id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar produto.", e);
        }
    }

    public Produto buscarPorCodigo(String codigo) {
        String sql = sqlBase() + " WHERE lower(p.codigo_produto) = lower(?)";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar produto por código.", e);
        }
    }

    public Produto buscarPorId(Connection conexao, Integer id) throws SQLException {
        String sql = sqlBase() + " WHERE p.id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Produto> listarTodos() {
        String sql = sqlBase() + " ORDER BY p.nome";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar produtos.", e);
        }
    }

    public List<Produto> buscar(String termo) {
        String sql = sqlBase() + """
                 WHERE lower(p.codigo_produto) LIKE lower(?) OR lower(p.nome) LIKE lower(?)
                 ORDER BY p.nome
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String filtro = "%" + termo + "%";
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapearLista(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar produtos.", e);
        }
    }

    public void atualizarEstoque(Connection conexao, Integer produtoId, Integer novaQuantidade) throws SQLException {
        String sql = "UPDATE produtos SET quantidade_estoque = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, produtoId);
            stmt.executeUpdate();
        }
    }

    public List<Produto> listarEstoqueMinimo() {
        String sql = sqlBase() + """
                 WHERE p.ativo = true AND p.quantidade_estoque <= p.nivel_minimo
                 ORDER BY p.quantidade_estoque, p.nome
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar produtos com estoque mínimo.", e);
        }
    }

    public int contarProdutos() {
        String sql = "SELECT count(*) FROM produtos WHERE ativo = true";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao contar produtos.", e);
        }
    }

    private void preencherStatement(Produto produto, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, produto.getCodigoProduto());
        stmt.setString(2, produto.getNome());
        stmt.setString(3, produto.getMarca());
        stmt.setInt(4, produto.getCategoria().getId());
        stmt.setBigDecimal(5, produto.getPrecoVenda());
        stmt.setInt(6, produto.getQuantidadeEstoque());
        stmt.setInt(7, produto.getNivelMinimo());
        stmt.setBoolean(8, Boolean.TRUE.equals(produto.getAtivo()));
    }

    private String sqlBase() {
        return """
                SELECT p.id, p.codigo_produto, p.nome, p.marca, p.preco_venda,
                       p.quantidade_estoque, p.nivel_minimo, p.ativo,
                       c.id AS categoria_id, c.codigo AS categoria_codigo, c.nome AS categoria_nome
                FROM produtos p
                JOIN categorias c ON c.id = p.categoria_id
                """;
    }

    private List<Produto> mapearLista(ResultSet rs) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        while (rs.next()) {
            produtos.add(mapear(rs));
        }
        return produtos;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("categoria_id"),
                rs.getString("categoria_codigo"),
                rs.getString("categoria_nome")
        );

        Produto produto = new Produto();
        produto.setId(rs.getInt("id"));
        produto.setCodigoProduto(rs.getString("codigo_produto"));
        produto.setNome(rs.getString("nome"));
        produto.setMarca(rs.getString("marca"));
        produto.setCategoria(categoria);
        produto.setPrecoVenda(rs.getBigDecimal("preco_venda"));
        produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        produto.setNivelMinimo(rs.getInt("nivel_minimo"));
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }
}
