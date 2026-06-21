package br.com.limpafacil.model.control;

import br.com.limpafacil.model.dao.FornecedorDAO;
import br.com.limpafacil.model.entity.Fornecedor;

import java.util.List;

public class ControladoraFornecedor {
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final ControladoraPermissao permissao = new ControladoraPermissao();

    public void salvar(Fornecedor fornecedor) {
        permissao.exigirAdministrador("gerenciar fornecedores");
        validar(fornecedor);
        fornecedor.setCodigo(fornecedor.getCodigo().trim().toUpperCase());
        fornecedor.setNome(fornecedor.getNome().trim());
        fornecedor.setCpfCnpj(fornecedor.getCpfCnpj().trim());
        fornecedor.setEstado(fornecedor.getEstado() == null ? null : fornecedor.getEstado().trim().toUpperCase());
        if (fornecedor.getAtivo() == null) {
            fornecedor.setAtivo(true);
        }
        if (fornecedor.getId() == null) {
            fornecedorDAO.inserir(fornecedor);
        } else {
            fornecedorDAO.atualizar(fornecedor);
        }
    }

    public void inativar(Integer id) {
        permissao.exigirAdministrador("gerenciar fornecedores");
        if (id == null) {
            throw new IllegalArgumentException("Selecione um fornecedor para inativar.");
        }
        fornecedorDAO.inativar(id);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorDAO.listarTodos();
    }

    public List<Fornecedor> buscar(String termo) {
        return termo == null || termo.isBlank() ? listarTodos() : fornecedorDAO.buscar(termo.trim());
    }

    private void validar(Fornecedor fornecedor) {
        if (fornecedor.getCodigo() == null || fornecedor.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Informe o código do fornecedor.");
        }
        if (fornecedor.getNome() == null || fornecedor.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do fornecedor.");
        }
        if (fornecedor.getCpfCnpj() == null || fornecedor.getCpfCnpj().isBlank()) {
            throw new IllegalArgumentException("Informe o CPF/CNPJ do fornecedor.");
        }
    }
}
