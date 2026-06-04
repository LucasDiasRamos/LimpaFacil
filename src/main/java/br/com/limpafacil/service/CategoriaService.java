package br.com.limpafacil.service;

import br.com.limpafacil.dao.CategoriaDAO;
import br.com.limpafacil.model.Categoria;

import java.util.List;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public void salvar(Categoria categoria) {
        validar(categoria);
        categoria.setCodigo(categoria.getCodigo().trim().toUpperCase());
        categoria.setNome(categoria.getNome().trim());

        if (categoria.getId() == null) {
            categoriaDAO.inserir(categoria);
        } else {
            categoriaDAO.atualizar(categoria);
        }
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione uma categoria para excluir.");
        }
        categoriaDAO.excluir(id);
    }

    public List<Categoria> listarTodos() {
        return categoriaDAO.listarTodos();
    }

    public List<Categoria> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return categoriaDAO.buscar(termo.trim());
    }

    private void validar(Categoria categoria) {
        if (categoria.getCodigo() == null || categoria.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Informe o código da categoria.");
        }
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome da categoria.");
        }
    }
}
