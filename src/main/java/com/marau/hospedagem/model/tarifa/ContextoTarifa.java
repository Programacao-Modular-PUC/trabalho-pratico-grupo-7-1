package com.marau.hospedagem.model.tarifa;

import java.time.LocalDate;

/**
 * Objeto de contexto que carrega todas as informações necessárias para que as
 * regras de tarifação (Strategy) decidam se são aplicáveis e como ajustam o
 * valor da diária.
 *
 * <p>É um objeto imutável e desacoplado das entidades JPA, justamente para que
 * novas regras possam ser criadas sem depender da estrutura de persistência.</p>
 */
public class ContextoTarifa {

    private final LocalDate dataReferencia;
    private final double valorDiariaBase;
    private final int quantidadeDiarias;
    private final int totalHospedagensCliente;
    private final String tipoQuarto;

    private ContextoTarifa(Builder builder) {
        this.dataReferencia = builder.dataReferencia;
        this.valorDiariaBase = builder.valorDiariaBase;
        this.quantidadeDiarias = builder.quantidadeDiarias;
        this.totalHospedagensCliente = builder.totalHospedagensCliente;
        this.tipoQuarto = builder.tipoQuarto;
    }

    public LocalDate getDataReferencia() { return dataReferencia; }
    public double getValorDiariaBase() { return valorDiariaBase; }
    public int getQuantidadeDiarias() { return quantidadeDiarias; }
    public int getTotalHospedagensCliente() { return totalHospedagensCliente; }
    public String getTipoQuarto() { return tipoQuarto; }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder simples para montar o contexto de forma legível.
     */
    public static class Builder {
        private LocalDate dataReferencia = LocalDate.now();
        private double valorDiariaBase = 0.0;
        private int quantidadeDiarias = 1;
        private int totalHospedagensCliente = 0;
        private String tipoQuarto = "";

        public Builder dataReferencia(LocalDate dataReferencia) {
            this.dataReferencia = dataReferencia;
            return this;
        }

        public Builder valorDiariaBase(double valorDiariaBase) {
            this.valorDiariaBase = valorDiariaBase;
            return this;
        }

        public Builder quantidadeDiarias(int quantidadeDiarias) {
            this.quantidadeDiarias = quantidadeDiarias;
            return this;
        }

        public Builder totalHospedagensCliente(int totalHospedagensCliente) {
            this.totalHospedagensCliente = totalHospedagensCliente;
            return this;
        }

        public Builder tipoQuarto(String tipoQuarto) {
            this.tipoQuarto = tipoQuarto;
            return this;
        }

        public ContextoTarifa build() {
            return new ContextoTarifa(this);
        }
    }
}
