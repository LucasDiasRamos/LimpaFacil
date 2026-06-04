package br.com.limpafacil.dao;

import br.com.limpafacil.config.ConexaoPostgres;
import br.com.limpafacil.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public void inserir(Categoria categoria) {
        String sql = "INSERT INTO categorias (codigo, nome) VALUES (?, ?)";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, categoria.getCodigo());
            stmt.setString(2, categoria.getNome());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir categoria.", e);
        }
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET codigo = ?, nome = ? WHERE id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, categoria.getCodigo());
            stmt.setString(2, categoria.getNome());
            stmt.setInt(3, categoria.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar categoria.", e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir categoria. Verifique se há produtos vinculados.", e);
        }
    }

    public Categoria buscarPorId(Integer id) {
        String sql = "SELECT id, codigo, nome FROM categorias WHERE id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar categoria.", e);
        }
    }

    public List<Categoria> listarTodos() {
        String sql = "SELECT id, codigo, nome FROM categorias ORDER BY nome";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Categoria> categorias = new ArrayList<>();
            while (rs.next()) {
                categorias.add(mapear(rs));
            }
            return categorias;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar categorias.", e);
        }
    }

    public List<Categoria> buscar(String termo) {
        String sql = """
                SELECT id, codigo, nome
                FROM categorias
                WHERE lower(codigo) LIKE lower(?) OR lower(nome) LIKE lower(?)
                ORDER BY nome
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String filtro = "%" + termo + "%";
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Categoria> categorias = new ArrayList<>();
                while (rs.next()) {
                    categorias.add(mapear(rs));
                }
                return categorias;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar categorias.", e);
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("codigo"), rs.getString("nome"));
    }
}
