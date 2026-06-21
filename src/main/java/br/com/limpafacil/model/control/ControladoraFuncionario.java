package br.com.limpafacil.model.control;

import br.com.limpafacil.model.dao.FuncionarioDAO;
import br.com.limpafacil.model.entity.Funcionario;

import java.util.List;

public class ControladoraFuncionario {
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final ControladoraPermissao permissao = new ControladoraPermissao();

    public void salvar(Funcionario funcionario) {
        permissao.exigirAdministrador("gerenciar funcionários e permissões");
        validar(funcionario);
        funcionario.setCodigo(funcionario.getCodigo().trim().toUpperCase());
        funcionario.setNome(funcionario.getNome().trim());
        funcionario.setEstado(funcionario.getEstado() == null ? null : funcionario.getEstado().trim().toUpperCase());
        if (funcionario.getAtivo() == null) {
            funcionario.setAtivo(true);
        }
        if (funcionario.getId() == null) {
            funcionarioDAO.inserir(funcionario);
        } else {
            funcionarioDAO.atualizar(funcionario);
        }
    }

    public void inativar(Integer id) {
        permissao.exigirAdministrador("gerenciar funcionários");
        if (id == null) {
            throw new IllegalArgumentException("Selecione um funcionário para inativar.");
        }
        funcionarioDAO.inativar(id);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioDAO.listarTodos();
    }

    public List<Funcionario> buscar(String termo) {
        return termo == null || termo.isBlank() ? listarTodos() : funcionarioDAO.buscar(termo.trim());
    }

    private void validar(Funcionario funcionario) {
        if (funcionario.getCodigo() == null || funcionario.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Informe o código do funcionário.");
        }
        if (funcionario.getNome() == null || funcionario.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do funcionário.");
        }
        if (funcionario.getEmail() == null || funcionario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail do funcionário.");
        }
        if (funcionario.getPerfil() == null) {
            throw new IllegalArgumentException("Selecione o perfil do funcionário.");
        }
    }
}
