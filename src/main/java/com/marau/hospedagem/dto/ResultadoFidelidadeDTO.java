package com.marau.hospedagem.dto;

import com.marau.hospedagem.model.fidelidade.BeneficiosConcedidos;
import com.marau.hospedagem.model.fidelidade.CategoriaFidelidade;

import java.util.List;

/**
 * Resposta da consulta ao programa de fidelidade de um cliente.
 */
public class ResultadoFidelidadeDTO {

    private Long clienteId;
    private String nomeCliente;
    private int totalHospedagens;
    private String categoria;
    private double percentualDesconto;
    private boolean upgradeQuarto;
    private int horasCheckoutEstendido;
    private int diariasGratuitas;
    private List<String> beneficios;
    private Double valorOriginal;
    private Double valorComDesconto;

    public ResultadoFidelidadeDTO() {
    }

    public static ResultadoFidelidadeDTO de(Long clienteId, String nomeCliente, int totalHospedagens,
                                            CategoriaFidelidade categoria, BeneficiosConcedidos beneficios) {
        ResultadoFidelidadeDTO dto = new ResultadoFidelidadeDTO();
        dto.clienteId = clienteId;
        dto.nomeCliente = nomeCliente;
        dto.totalHospedagens = totalHospedagens;
        dto.categoria = categoria.name();
        dto.percentualDesconto = beneficios.getPercentualDesconto();
        dto.upgradeQuarto = beneficios.isUpgradeQuarto();
        dto.horasCheckoutEstendido = beneficios.getHorasCheckoutEstendido();
        dto.diariasGratuitas = beneficios.getDiariasGratuitas();
        dto.beneficios = beneficios.getDescricoes();
        return dto;
    }

    public void aplicarValores(double valorOriginal, double valorComDesconto) {
        this.valorOriginal = valorOriginal;
        this.valorComDesconto = valorComDesconto;
    }

    public Long getClienteId() { return clienteId; }
    public String getNomeCliente() { return nomeCliente; }
    public int getTotalHospedagens() { return totalHospedagens; }
    public String getCategoria() { return categoria; }
    public double getPercentualDesconto() { return percentualDesconto; }
    public boolean isUpgradeQuarto() { return upgradeQuarto; }
    public int getHorasCheckoutEstendido() { return horasCheckoutEstendido; }
    public int getDiariasGratuitas() { return diariasGratuitas; }
    public List<String> getBeneficios() { return beneficios; }
    public Double getValorOriginal() { return valorOriginal; }
    public Double getValorComDesconto() { return valorComDesconto; }
}
