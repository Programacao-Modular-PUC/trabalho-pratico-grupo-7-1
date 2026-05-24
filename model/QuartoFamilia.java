package com.marau.hospedagem.model;

import jakarta.persistence.*;

/**
 * Quarto Família:
 * - Mistura de camas (solteiro, casal, queen/king)
 * - Capacidade máxima conforme configuração das camas
 * - Quantidade de ambientes distintos
 * - Cálculo por número de hóspedes (não de camas):
 *   valorDiaria = valorBase * (1 + percentualPorHospede * numHospedes) + ar + hidro
 * - Desconto progressivo para grupos:
 *   5+ hóspedes: 10% de desconto
 *   7+ hóspedes: 15% de desconto
 *   10+ hóspedes: 20% de desconto
 */
@Entity
@DiscriminatorValue("FAMILIA")
public class QuartoFamilia extends Quarto {

    @Column(name = "camas_solteiro_fam")
    private Integer camasSolteiro = 0;

    @Column(name = "camas_casal_fam")
    private Integer camasCasal = 0;

    @Column(name = "camas_queen_king")
    private Integer camasQueenKing = 0;

    @Column(name = "qtd_ambientes")
    private Integer quantidadeAmbientes = 1;

    @Column(name = "percentual_por_hospede")
    private Double percentualPorHospede = 0.10; // 10% a mais por hóspede

    public QuartoFamilia() {}

    public QuartoFamilia(String identificacao, Double valorBase, Boolean possuiAr,
                         Boolean possuiHidro, Double adicionalAr, Double adicionalHidro,
                         Integer camasSolteiro, Integer camasCasal, Integer camasQueenKing,
                         Integer quantidadeAmbientes, Double percentualPorHospede) {
        super(identificacao, valorBase, possuiAr, possuiHidro, adicionalAr, adicionalHidro);
        this.camasSolteiro = camasSolteiro != null ? camasSolteiro : 0;
        this.camasCasal = camasCasal != null ? camasCasal : 0;
        this.camasQueenKing = camasQueenKing != null ? camasQueenKing : 0;
        this.quantidadeAmbientes = quantidadeAmbientes != null ? quantidadeAmbientes : 1;
        this.percentualPorHospede = percentualPorHospede != null ? percentualPorHospede : 0.10;
    }

    /**
     * Calcula diária base (sem considerar número de hóspedes).
     * Para o cálculo real, usar calcularComHospedes(n).
     */
    @Override
    public double calcularValorDiaria() {
        return calcularComHospedes(1);
    }

    /**
     * Cálculo principal: valor base + percentual proporcional ao número de hóspedes.
     * valorFinal = (valorBase * (1 + percentualPorHospede * numHospedes)) + ar + hidro - desconto
     */
    public double calcularComHospedes(int numHospedes) {
        if (numHospedes < 1) numHospedes = 1;
        if (numHospedes > getCapacidadeMaxima()) {
            throw new IllegalArgumentException(
                "Número de hóspedes (" + numHospedes + ") excede capacidade máxima (" + getCapacidadeMaxima() + ")");
        }

        double base = calcularBaseComAdicionais();
        // Acréscimo percentual por número de hóspedes
        double valorComHospedes = base * (1 + percentualPorHospede * numHospedes);
        // Aplica desconto progressivo
        double desconto = calcularDesconto(numHospedes);
        return valorComHospedes * (1 - desconto);
    }

    /**
     * Desconto progressivo para grupos:
     * - 5 a 6 hóspedes: 10%
     * - 7 a 9 hóspedes: 15%
     * - 10+ hóspedes: 20%
     */
    public double calcularDesconto(int numHospedes) {
        if (numHospedes >= 10) return 0.20;
        if (numHospedes >= 7)  return 0.15;
        if (numHospedes >= 5)  return 0.10;
        return 0.0;
    }

    /**
     * Capacidade máxima:
     * - Cada cama solteiro = 1 pessoa
     * - Cada cama casal = 2 pessoas
     * - Cada cama queen/king = 2 pessoas
     */
    @Override
    public int getCapacidadeMaxima() {
        return camasSolteiro + (camasCasal * 2) + (camasQueenKing * 2);
    }

    @Override
    public String getTipo() {
        return "FAMILIA";
    }

    // Getters e Setters
    public Integer getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(Integer camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public Integer getCamasCasal() { return camasCasal; }
    public void setCamasCasal(Integer camasCasal) { this.camasCasal = camasCasal; }

    public Integer getCamasQueenKing() { return camasQueenKing; }
    public void setCamasQueenKing(Integer camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public Integer getQuantidadeAmbientes() { return quantidadeAmbientes; }
    public void setQuantidadeAmbientes(Integer quantidadeAmbientes) { this.quantidadeAmbientes = quantidadeAmbientes; }

    public Double getPercentualPorHospede() { return percentualPorHospede; }
    public void setPercentualPorHospede(Double percentualPorHospede) { this.percentualPorHospede = percentualPorHospede; }
}
