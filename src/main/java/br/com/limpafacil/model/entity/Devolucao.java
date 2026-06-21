package br.com.limpafacil.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Devolucao {
    private Integer id;
    private String codigoDevolucao;
    private Venda venda;
    private Cliente cliente;
    private Usuario funcionario;
    private LocalDateTime dataHora;
    private String motivo;
    private BigDecimal valorTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoDevolucao() {
        return codigoDevolucao;
    }

    public void setCodigoDevolucao(String codigoDevolucao) {
        this.codigoDevolucao = codigoDevolucao;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Usuario funcionario) {
        this.funcionario = funcionario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
