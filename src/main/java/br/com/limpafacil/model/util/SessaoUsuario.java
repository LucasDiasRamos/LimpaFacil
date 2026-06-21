package br.com.limpafacil.model.util;

import br.com.limpafacil.model.entity.Usuario;

public final class SessaoUsuario {
    private static Usuario usuarioLogado;

    private SessaoUsuario() {
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static void limpar() {
        usuarioLogado = null;
    }
}
