package br.com.limpafacil.model.control;

import br.com.limpafacil.model.entity.PerfilUsuario;
import br.com.limpafacil.model.entity.Usuario;
import br.com.limpafacil.model.util.SessaoUsuario;

public class ControladoraPermissao {
    public boolean usuarioAdministrador() {
        Usuario usuario = SessaoUsuario.getUsuarioLogado();
        return usuario != null && usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR;
    }

    public void exigirAdministrador(String acao) {
        if (!usuarioAdministrador()) {
            throw new IllegalArgumentException("Acesso negado. Somente administrador pode " + acao + ".");
        }
    }
}
