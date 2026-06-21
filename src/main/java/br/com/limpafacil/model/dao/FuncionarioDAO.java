package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.entity.Funcionario;
import br.com.limpafacil.model.entity.PerfilUsuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    public void inserir(Funcionario funcionario) {
        String sql = """
                INSERT INTO funcionarios
                (codigo, nome, endereco, cidade, estado, telefone, email, data_contratacao, cargo, perfil, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(funcionario, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir funcionário.", e);
        }
    }

    public void atualizar(Funcionario funcionario) {
        String sql = """
                UPDATE funcionarios
                SET codigo = ?, nome = ?, endereco = ?, cidade = ?, estado = ?, telefone = ?,
                    email = ?, data_contratacao = ?, cargo = ?, perfil = ?, ativo = ?
                WHERE id = ?
                """;

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(funcionario, stmt);
            stmt.setInt(12, funcionario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar funcionário.", e);
        }
    }

    public void inativar(Integer id) {
        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement("UPDATE funcionarios SET ativo = false WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inativar funcionário.", e);
        }
    }

    public List<Funcionario> listarTodos() {
        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM funcionarios ORDER BY nome")) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar funcionários.", e);
        }
    }

    public List<Funcionario> buscar(String termo) {
        String sql = """
                SELECT * FROM funcionarios
                WHERE lower(codigo) LIKE lower(?) OR lower(nome) LIKE lower(?) OR lower(email) LIKE lower(?)
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
            throw new IllegalStateException("Erro ao buscar funcionários.", e);
        }
    }

    private void preencher(Funcionario funcionario, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, funcionario.getCodigo());
        stmt.setString(2, funcionario.getNome());
        stmt.setString(3, funcionario.getEndereco());
        stmt.setString(4, funcionario.getCidade());
        stmt.setString(5, funcionario.getEstado());
        stmt.setString(6, funcionario.getTelefone());
        stmt.setString(7, funcionario.getEmail());
        stmt.setDate(8, funcionario.getDataContratacao() == null ? null : Date.valueOf(funcionario.getDataContratacao()));
        stmt.setString(9, funcionario.getCargo());
        stmt.setString(10, funcionario.getPerfil().name());
        stmt.setBoolean(11, Boolean.TRUE.equals(funcionario.getAtivo()));
    }

    private List<Funcionario> mapearLista(ResultSet rs) throws SQLException {
        List<Funcionario> funcionarios = new ArrayList<>();
        while (rs.next()) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(rs.getInt("id"));
            funcionario.setCodigo(rs.getString("codigo"));
            funcionario.setNome(rs.getString("nome"));
            funcionario.setEndereco(rs.getString("endereco"));
            funcionario.setCidade(rs.getString("cidade"));
            funcionario.setEstado(rs.getString("estado"));
            funcionario.setTelefone(rs.getString("telefone"));
            funcionario.setEmail(rs.getString("email"));
            Date data = rs.getDate("data_contratacao");
            funcionario.setDataContratacao(data == null ? null : data.toLocalDate());
            funcionario.setCargo(rs.getString("cargo"));
            funcionario.setPerfil(PerfilUsuario.valueOf(rs.getString("perfil")));
            funcionario.setAtivo(rs.getBoolean("ativo"));
            funcionarios.add(funcionario);
        }
        return funcionarios;
    }
}
