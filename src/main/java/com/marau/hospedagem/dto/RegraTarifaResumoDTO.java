package com.marau.hospedagem.dto;

import com.marau.hospedagem.model.tarifa.RegraTarifa;

/**
 * Resumo de uma regra de tarifa para listagem via API.
 */
public class RegraTarifaResumoDTO {

    private final String nome;
    private final int prioridade;

    public RegraTarifaResumoDTO(RegraTarifa regra) {
        this.nome = regra.getNome();
        this.prioridade = regra.getPrioridade();
    }

    public String getNome() { return nome; }
    public int getPrioridade() { return prioridade; }
}
