package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.entity.Fornecedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {
    public void inserir(Fornecedor fornecedor) {
        String sql = """
                INSERT INTO fornecedores
                (codigo, nome, nome_fantasia, cpf_cnpj, endereco, cidade, estado, telefone, email, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(fornecedor, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir fornecedor.", e);
        }
    }

    public void atualizar(Fornecedor fornecedor) {
        String sql = """
                UPDATE fornecedores
                SET codigo = ?, nome = ?, nome_fantasia = ?, cpf_cnpj = ?, endereco = ?, cidade = ?,
                    estado = ?, telefone = ?, email = ?, ativo = ?
                WHERE id = ?
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(fornecedor, stmt);
            stmt.setInt(11, fornecedor.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar fornecedor.", e);
        }
    }

    public void inativar(Integer id) {
        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement("UPDATE fornecedores SET ativo = false WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inativar fornecedor.", e);
        }
    }

    public List<Fornecedor> listarTodos() {
        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fornecedores ORDER BY nome")) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar fornecedores.", e);
        }
    }

    public List<Fornecedor> buscar(String termo) {
        String sql = """
                SELECT * FROM fornecedores
                WHERE lower(codigo) LIKE lower(?) OR lower(nome) LIKE lower(?) OR lower(cpf_cnpj) LIKE lower(?)
                ORDER BY nome
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String filtro = "%" + termo + "%";
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            stmt.setString(3, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapearLista(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar fornecedores.", e);
        }
    }

    private void preencher(Fornecedor fornecedor, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, fornecedor.getCodigo());
        stmt.setString(2, fornecedor.getNome());
        stmt.setString(3, fornecedor.getNomeFantasia());
        stmt.setString(4, fornecedor.getCpfCnpj());
        stmt.setString(5, fornecedor.getEndereco());
        stmt.setString(6, fornecedor.getCidade());
        stmt.setString(7, fornecedor.getEstado());
        stmt.setString(8, fornecedor.getTelefone());
        stmt.setString(9, fornecedor.getEmail());
        stmt.setBoolean(10, Boolean.TRUE.equals(fornecedor.getAtivo()));
    }

    private List<Fornecedor> mapearLista(ResultSet rs) throws SQLException {
        List<Fornecedor> fornecedores = new ArrayList<>();
        while (rs.next()) {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(rs.getInt("id"));
            fornecedor.setCodigo(rs.getString("codigo"));
            fornecedor.setNome(rs.getString("nome"));
            fornecedor.setNomeFantasia(rs.getString("nome_fantasia"));
            fornecedor.setCpfCnpj(rs.getString("cpf_cnpj"));
            fornecedor.setEndereco(rs.getString("endereco"));
            fornecedor.setCidade(rs.getString("cidade"));
            fornecedor.setEstado(rs.getString("estado"));
            fornecedor.setTelefone(rs.getString("telefone"));
            fornecedor.setEmail(rs.getString("email"));
            fornecedor.setAtivo(rs.getBoolean("ativo"));
            fornecedores.add(fornecedor);
        }
        return fornecedores;
    }
}
