package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.entity.PerfilUsuario;
import br.com.limpafacil.model.entity.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha, perfil, ativo FROM usuarios WHERE lower(email) = lower(?)";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário por e-mail.", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPerfil(PerfilUsuario.valueOf(rs.getString("perfil")));
        usuario.setAtivo(rs.getBoolean("ativo"));
        return usuario;
    }
}
