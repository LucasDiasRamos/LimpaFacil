package br.com.limpafacil.model.dao;

import br.com.limpafacil.model.config.ConexaoPostgres;
import br.com.limpafacil.model.entity.Cliente;
import br.com.limpafacil.model.entity.FormaPagamento;
import br.com.limpafacil.model.entity.PerfilUsuario;
import br.com.limpafacil.model.entity.StatusVenda;
import br.com.limpafacil.model.entity.Usuario;
import br.com.limpafacil.model.entity.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {
    public Integer inserir(Connection conexao, Venda venda) throws SQLException {
        String sql = """
                INSERT INTO vendas (codigo_venda, data_hora, cliente_id, funcionario_id, valor_total, forma_pagamento, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, venda.getCodigoVenda());
            stmt.setTimestamp(2, Timestamp.valueOf(venda.getDataHora()));
            if (venda.getCliente() == null || venda.getCliente().getId() == null) {
                stmt.setObject(3, null);
            } else {
                stmt.setInt(3, venda.getCliente().getId());
            }
            stmt.setInt(4, venda.getFuncionario().getId());
            stmt.setBigDecimal(5, venda.getValorTotal());
            stmt.setString(6, venda.getFormaPagamento().name());
            stmt.setString(7, venda.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("ID da venda não foi gerado.");
            }
        }
    }

    public Venda buscarPorId(Integer id) {
        String sql = sqlBase() + " WHERE v.id = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar venda.", e);
        }
    }

    public Venda buscarPorCodigo(String codigoVenda) {
        String sql = sqlBase() + " WHERE v.codigo_venda = ?";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, codigoVenda);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar venda por código.", e);
        }
    }

    public List<Venda> listarTodos() {
        String sql = sqlBase() + " ORDER BY v.data_hora DESC";

        try (Connection conexao = ConexaoPostgres.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Venda> vendas = new ArrayList<>();
            while (rs.next()) {
                vendas.add(mapear(rs));
            }
            return vendas;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar vendas.", e);
        }
    }

    private String sqlBase() {
        return """
                SELECT v.id, v.codigo_venda, v.data_hora, v.valor_total, v.forma_pagamento, v.status,
                       cl.id AS cliente_id, cl.codigo AS cliente_codigo, cl.nome AS cliente_nome,
                       cl.cpf_cnpj AS cliente_cpf_cnpj, cl.ativo AS cliente_ativo,
                       u.id AS usuario_id, u.nome AS usuario_nome, u.email AS usuario_email,
                       u.senha AS usuario_senha, u.perfil AS usuario_perfil, u.ativo AS usuario_ativo
                FROM vendas v
                LEFT JOIN clientes cl ON cl.id = v.cliente_id
                JOIN usuarios u ON u.id = v.funcionario_id
                """;
    }

    private Venda mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("usuario_id"));
        usuario.setNome(rs.getString("usuario_nome"));
        usuario.setEmail(rs.getString("usuario_email"));
        usuario.setSenha(rs.getString("usuario_senha"));
        usuario.setPerfil(PerfilUsuario.valueOf(rs.getString("usuario_perfil")));
        usuario.setAtivo(rs.getBoolean("usuario_ativo"));

        Venda venda = new Venda();
        venda.setId(rs.getInt("id"));
        venda.setCodigoVenda(rs.getString("codigo_venda"));
        venda.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        int clienteId = rs.getInt("cliente_id");
        if (!rs.wasNull()) {
            Cliente cliente = new Cliente();
            cliente.setId(clienteId);
            cliente.setCodigo(rs.getString("cliente_codigo"));
            cliente.setNome(rs.getString("cliente_nome"));
            cliente.setCpfCnpj(rs.getString("cliente_cpf_cnpj"));
            cliente.setAtivo(rs.getBoolean("cliente_ativo"));
            venda.setCliente(cliente);
        }
        venda.setFuncionario(usuario);
        venda.setValorTotal(rs.getBigDecimal("valor_total"));
        venda.setFormaPagamento(FormaPagamento.valueOf(rs.getString("forma_pagamento")));
        venda.setStatus(StatusVenda.valueOf(rs.getString("status")));
        return venda;
    }
}
