package com.marau.hospedagem.service.tarifa;

import com.marau.hospedagem.model.tarifa.ContextoTarifa;
import com.marau.hospedagem.model.tarifa.RegraTarifa;
import com.marau.hospedagem.model.tarifa.ResultadoTarifa;
import com.marau.hospedagem.model.tarifa.TarifaAltaTemporada;
import com.marau.hospedagem.model.tarifa.TarifaBaixaTemporada;
import com.marau.hospedagem.model.tarifa.TarifaClienteFrequente;
import com.marau.hospedagem.model.tarifa.TarifaFeriado;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Padrão <b>Singleton</b> — catálogo/gerenciador global de regras de tarifação.
 *
 * <p><b>Por que Singleton?</b> As regras de tarifa representam uma política de
 * preços <i>única e global</i> da hospedagem. Não faria sentido cada reserva ou
 * cada serviço manter sua própria tabela de regras: isso geraria inconsistência
 * de preços. Centralizar em uma única instância garante que toda a aplicação
 * (criação de aluguel, cotação, relatórios) enxergue exatamente o mesmo conjunto
 * de regras vigentes, e que a alteração de uma política (ex.: cadastrar uma
 * promoção) reflita imediatamente em todo o sistema.</p>
 *
 * <p>Combina com o padrão Strategy: o Singleton é o contexto que armazena e
 * orquestra as estratégias {@link RegraTarifa}.</p>
 *
 * <p>Implementado com o idioma <i>initialization-on-demand holder</i>, que é
 * thread-safe e faz inicialização tardia sem custo de sincronização.</p>
 */
public final class GerenciadorTarifas {

    /** Lista thread-safe — o catálogo pode ser lido/alterado concorrentemente. */
    private final List<RegraTarifa> regras = new CopyOnWriteArrayList<>();

    private GerenciadorTarifas() {
        // Regras padrão de fábrica. Promoções/feriados são cadastrados em runtime.
        registrarRegra(new TarifaAltaTemporada());
        registrarRegra(new TarifaBaixaTemporada());
        registrarRegra(new TarifaFeriado());
        registrarRegra(new TarifaClienteFrequente());
    }

    private static class Holder {
        private static final GerenciadorTarifas INSTANCIA = new GerenciadorTarifas();
    }

    public static GerenciadorTarifas getInstance() {
        return Holder.INSTANCIA;
    }

    /** Registra uma nova regra (extensibilidade em tempo de execução). */
    public void registrarRegra(RegraTarifa regra) {
        regras.add(regra);
    }

    /** Remove uma regra pelo nome. Retorna true se removeu. */
    public boolean removerRegra(String nome) {
        return regras.removeIf(r -> r.getNome().equals(nome));
    }

    /** Lista imutável das regras atualmente cadastradas. */
    public List<RegraTarifa> listarRegras() {
        return List.copyOf(regras);
    }

    /**
     * Calcula o valor da diária aplicando, em ordem de prioridade, todas as
     * regras aplicáveis ao contexto. Retorna um resultado detalhado com o
     * histórico das regras que incidiram.
     */
    public ResultadoTarifa calcular(ContextoTarifa contexto) {
        ResultadoTarifa resultado =
                new ResultadoTarifa(contexto.getValorDiariaBase(), contexto.getQuantidadeDiarias());

        double valorAtual = contexto.getValorDiariaBase();
        List<RegraTarifa> aplicaveis = regras.stream()
                .filter(r -> r.isAplicavel(contexto))
                .sorted(Comparator.comparingInt(RegraTarifa::getPrioridade))
                .toList();

        for (RegraTarifa regra : aplicaveis) {
            valorAtual = regra.aplicar(contexto, valorAtual);
            resultado.registrarRegra(regra.getNome(), valorAtual);
        }
        return resultado;
    }

    /** Atalho que devolve apenas o valor final da diária. */
    public double calcularValorDiaria(ContextoTarifa contexto) {
        return calcular(contexto).getValorDiariaFinal();
    }
}
