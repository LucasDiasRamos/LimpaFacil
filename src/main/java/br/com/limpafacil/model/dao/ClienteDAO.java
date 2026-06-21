package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.entity.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    public void inserir(Cliente cliente) {
        String sql = """
                INSERT INTO clientes (codigo, nome, cpf_cnpj, endereco, cidade, estado, telefone, email, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(cliente, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir cliente.", e);
        }
    }

    public void atualizar(Cliente cliente) {
        String sql = """
                UPDATE clientes
                SET codigo = ?, nome = ?, cpf_cnpj = ?, endereco = ?, cidade = ?, estado = ?,
                    telefone = ?, email = ?, ativo = ?
                WHERE id = ?
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(cliente, stmt);
            stmt.setInt(10, cliente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar cliente.", e);
        }
    }

    public void inativar(Integer id) {
        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement("UPDATE clientes SET ativo = false WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inativar cliente.", e);
        }
    }

    public Cliente buscarPorId(Integer id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar cliente.", e);
        }
    }

    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM clientes ORDER BY nome";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar clientes.", e);
        }
    }

    public List<Cliente> buscar(String termo) {
        String sql = """
                SELECT * FROM clientes
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
            throw new IllegalStateException("Erro ao buscar clientes.", e);
        }
    }

    private void preencher(Cliente cliente, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, cliente.getCodigo());
        stmt.setString(2, cliente.getNome());
        stmt.setString(3, cliente.getCpfCnpj());
        stmt.setString(4, cliente.getEndereco());
        stmt.setString(5, cliente.getCidade());
        stmt.setString(6, cliente.getEstado());
        stmt.setString(7, cliente.getTelefone());
        stmt.setString(8, cliente.getEmail());
        stmt.setBoolean(9, Boolean.TRUE.equals(cliente.getAtivo()));
    }

    private List<Cliente> mapearLista(ResultSet rs) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        while (rs.next()) {
            clientes.add(mapear(rs));
        }
        return clientes;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setCodigo(rs.getString("codigo"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpfCnpj(rs.getString("cpf_cnpj"));
        cliente.setEndereco(rs.getString("endereco"));
        cliente.setCidade(rs.getString("cidade"));
        cliente.setEstado(rs.getString("estado"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEmail(rs.getString("email"));
        cliente.setAtivo(rs.getBoolean("ativo"));
        return cliente;
    }
}
