package com.marau.hospedagem.model.tarifa;

import java.time.Month;
import java.util.Set;

/**
 * Regra de baixa temporada: aplica um desconto percentual sobre a diária
 * quando a data de referência cai em um dos meses de menor procura.
 *
 * <p>Por padrão considera março, abril e maio com desconto de 15%.</p>
 */
public class TarifaBaixaTemporada implements RegraTarifa {

    private final Set<Month> meses;
    private final double percentualDesconto;

    public TarifaBaixaTemporada() {
        this(Set.of(Month.MARCH, Month.APRIL, Month.MAY), 0.15);
    }

    public TarifaBaixaTemporada(Set<Month> meses, double percentualDesconto) {
        this.meses = meses;
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public String getNome() {
        return "BAIXA_TEMPORADA";
    }

    @Override
    public int getPrioridade() {
        return 10;
    }

    @Override
    public boolean isAplicavel(ContextoTarifa contexto) {
        return meses.contains(contexto.getDataReferencia().getMonth());
    }

    @Override
    public double aplicar(ContextoTarifa contexto, double valorDiariaAtual) {
        return valorDiariaAtual * (1 - percentualDesconto);
    }
}
