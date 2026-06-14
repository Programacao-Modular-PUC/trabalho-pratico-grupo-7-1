package com.marau.hospedagem.model;

import jakarta.persistence.*;

/**
 * Quarto Individual:
 * - Pode ter 1 ou mais camas de solteiro
 * - Não permite berço
 * - Valor da diária = valorBase + adicionalPorCama * (qtdCamas - 1) + ar + hidro
 *   Se houver apenas 1 cama, não tem adicional por cama
 * - Capacidade máxima = número de camas
 */
@Entity
@DiscriminatorValue("INDIVIDUAL")
public class QuartoIndividual extends Quarto {

    @Column(name = "qtd_camas_solteiro")
    private Integer quantidadeCamas = 1;

    @Column(name = "adicional_por_cama")
    private Double adicionalPorCama = 0.0;

    public QuartoIndividual() {}

    public QuartoIndividual(String identificacao, Double valorBase, Boolean possuiAr,
                            Boolean possuiHidro, Double adicionalAr, Double adicionalHidro,
                            Integer quantidadeCamas, Double adicionalPorCama) {
        super(identificacao, valorBase, possuiAr, possuiHidro, adicionalAr, adicionalHidro);
        this.quantidadeCamas = quantidadeCamas != null ? quantidadeCamas : 1;
        this.adicionalPorCama = adicionalPorCama != null ? adicionalPorCama : 0.0;
    }

    @Override
    public double calcularValorDiaria() {
        double valor = calcularBaseComAdicionais();
        // Adicional por cama extra (a partir da 2ª cama)
        if (quantidadeCamas > 1) {
            valor += adicionalPorCama * (quantidadeCamas - 1);
        }
        return valor;
    }

    @Override
    public int getCapacidadeMaxima() {
        return quantidadeCamas;
    }

    @Override
    public String getTipo() {
        return "INDIVIDUAL";
    }

    // Getters e Setters
    public Integer getQuantidadeCamas() { return quantidadeCamas; }
    public void setQuantidadeCamas(Integer quantidadeCamas) { this.quantidadeCamas = quantidadeCamas; }

    public Double getAdicionalPorCama() { return adicionalPorCama; }
    public void setAdicionalPorCama(Double adicionalPorCama) { this.adicionalPorCama = adicionalPorCama; }
}
