package com.marau.hospedagem.model.tarifa;

import java.time.LocalDate;

/**
 * Promoção temporária: aplica um desconto percentual enquanto a data de
 * referência estiver dentro do período promocional [inicio, fim].
 */
public class TarifaPromocional implements RegraTarifa {

    private final String nome;
    private final LocalDate inicio;
    private final LocalDate fim;
    private final double percentualDesconto;

    public TarifaPromocional(String nome, LocalDate inicio, LocalDate fim, double percentualDesconto) {
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public String getNome() {
        return "PROMOCAO_" + nome;
    }

    @Override
    public int getPrioridade() {
        return 20;
    }

    @Override
    public boolean isAplicavel(ContextoTarifa contexto) {
        LocalDate data = contexto.getDataReferencia();
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    @Override
    public double aplicar(ContextoTarifa contexto, double valorDiariaAtual) {
        return valorDiariaAtual * (1 - percentualDesconto);
    }
}
