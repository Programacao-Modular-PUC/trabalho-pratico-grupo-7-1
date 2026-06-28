package com.marau.hospedagem.model.fidelidade;

/**
 * Dados de entrada para o cálculo de benefícios de fidelidade de um cliente.
 */
public class ContextoFidelidade {

    private final int totalHospedagens;
    private final double valorHospedagemAtual;
    private final int diariasAtual;

    public ContextoFidelidade(int totalHospedagens, double valorHospedagemAtual, int diariasAtual) {
        this.totalHospedagens = totalHospedagens;
        this.valorHospedagemAtual = valorHospedagemAtual;
        this.diariasAtual = diariasAtual;
    }

    public int getTotalHospedagens() { return totalHospedagens; }
    public double getValorHospedagemAtual() { return valorHospedagemAtual; }
    public int getDiariasAtual() { return diariasAtual; }
}
