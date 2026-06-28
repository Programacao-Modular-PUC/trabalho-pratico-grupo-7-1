package com.marau.hospedagem.model.tarifa;

import java.time.Month;
import java.util.Set;

/**
 * Regra de alta temporada: aplica um acréscimo percentual sobre a diária
 * quando a data de referência cai em um dos meses configurados.
 *
 * <p>Por padrão considera dezembro, janeiro e julho (férias) com acréscimo de 30%.</p>
 */
public class TarifaAltaTemporada implements RegraTarifa {

    private final Set<Month> meses;
    private final double percentualAcrescimo;

    public TarifaAltaTemporada() {
        this(Set.of(Month.DECEMBER, Month.JANUARY, Month.JULY), 0.30);
    }

    public TarifaAltaTemporada(Set<Month> meses, double percentualAcrescimo) {
        this.meses = meses;
        this.percentualAcrescimo = percentualAcrescimo;
    }

    @Override
    public String getNome() {
        return "ALTA_TEMPORADA";
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
        return valorDiariaAtual * (1 + percentualAcrescimo);
    }
}
