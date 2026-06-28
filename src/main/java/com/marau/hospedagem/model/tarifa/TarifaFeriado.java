package com.marau.hospedagem.model.tarifa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Regra de feriado: aplica acréscimo percentual quando a data de referência
 * está cadastrada como feriado.
 *
 * <p>Os feriados são configuráveis em tempo de execução, permitindo cadastrar
 * datas novas sem alterar o código (ex.: feriados móveis).</p>
 */
public class TarifaFeriado implements RegraTarifa {

    private final Set<LocalDate> feriados;
    private final double percentualAcrescimo;

    public TarifaFeriado() {
        this(new HashSet<>(), 0.20);
    }

    public TarifaFeriado(Set<LocalDate> feriados, double percentualAcrescimo) {
        this.feriados = new HashSet<>(feriados);
        this.percentualAcrescimo = percentualAcrescimo;
    }

    /** Cadastra um novo feriado em tempo de execução. */
    public void adicionarFeriado(LocalDate data) {
        feriados.add(data);
    }

    @Override
    public String getNome() {
        return "FERIADO";
    }

    @Override
    public int getPrioridade() {
        return 5;
    }

    @Override
    public boolean isAplicavel(ContextoTarifa contexto) {
        return feriados.contains(contexto.getDataReferencia());
    }

    @Override
    public double aplicar(ContextoTarifa contexto, double valorDiariaAtual) {
        return valorDiariaAtual * (1 + percentualAcrescimo);
    }
}
