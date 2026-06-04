package br.com.limpafacil.service;

import br.com.limpafacil.dao.UsuarioDAO;
import br.com.limpafacil.model.Usuario;
import br.com.limpafacil.util.SessaoUsuario;

public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Informe e-mail e senha.");
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new IllegalArgumentException("Usuário inativo.");
        }

        SessaoUsuario.setUsuarioLogado(usuario);
        return usuario;
    }

    public void logout() {
        SessaoUsuario.limpar();
    }

    public Usuario getUsuarioLogado() {
        return SessaoUsuario.getUsuarioLogado();
    }
}
