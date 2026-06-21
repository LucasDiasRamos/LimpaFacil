package br.com.limpafacil.model.control;

import br.com.limpafacil.model.dao.ClienteDAO;
import br.com.limpafacil.model.entity.Cliente;

import java.util.List;

public class ControladoraCliente {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public void salvar(Cliente cliente) {
        validar(cliente);
        cliente.setCodigo(cliente.getCodigo().trim().toUpperCase());
        cliente.setNome(cliente.getNome().trim());
        cliente.setCpfCnpj(cliente.getCpfCnpj().trim());
        cliente.setEstado(normalizarEstado(cliente.getEstado()));
        if (cliente.getAtivo() == null) {
            cliente.setAtivo(true);
        }
        if (cliente.getId() == null) {
            clienteDAO.inserir(cliente);
        } else {
            clienteDAO.atualizar(cliente);
        }
    }

    public void inativar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para inativar.");
        }
        clienteDAO.inativar(id);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public List<Cliente> buscar(String termo) {
        return termo == null || termo.isBlank() ? listarTodos() : clienteDAO.buscar(termo.trim());
    }

    public Cliente buscarPorId(Integer id) {
        return id == null ? null : clienteDAO.buscarPorId(id);
    }

    private void validar(Cliente cliente) {
        if (cliente.getCodigo() == null || cliente.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Informe o código do cliente.");
        }
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do cliente.");
        }
        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().isBlank()) {
            throw new IllegalArgumentException("Informe o CPF/CNPJ do cliente.");
        }
    }

    private String normalizarEstado(String estado) {
        return estado == null ? null : estado.trim().toUpperCase();
    }
}
