package com.marau.hospedagem.dto;

import java.time.LocalDate;

/**
 * Requisição para cotar o valor de uma diária aplicando as regras de
 * tarifação vigentes, sem necessidade de criar um aluguel.
 */
public class CotacaoTarifaDTO {

    private LocalDate dataReferencia;
    private double valorDiariaBase;
    private int quantidadeDiarias = 1;
    private int totalHospedagensCliente = 0;
    private String tipoQuarto = "";

    public LocalDate getDataReferencia() { return dataReferencia; }
    public void setDataReferencia(LocalDate dataReferencia) { this.dataReferencia = dataReferencia; }

    public double getValorDiariaBase() { return valorDiariaBase; }
    public void setValorDiariaBase(double valorDiariaBase) { this.valorDiariaBase = valorDiariaBase; }

    public int getQuantidadeDiarias() { return quantidadeDiarias; }
    public void setQuantidadeDiarias(int quantidadeDiarias) { this.quantidadeDiarias = quantidadeDiarias; }

    public int getTotalHospedagensCliente() { return totalHospedagensCliente; }
    public void setTotalHospedagensCliente(int totalHospedagensCliente) {
        this.totalHospedagensCliente = totalHospedagensCliente;
    }

    public String getTipoQuarto() { return tipoQuarto; }
    public void setTipoQuarto(String tipoQuarto) { this.tipoQuarto = tipoQuarto; }
}
