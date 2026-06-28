package com.marau.hospedagem.model.tarifa;

/**
 * Desconto para clientes frequentes: a partir de um número mínimo de
 * hospedagens anteriores, concede desconto percentual sobre a diária.
 *
 * <p>É aplicada por último (prioridade alta) para incidir sobre o valor já
 * ajustado por temporada/promoções.</p>
 */
public class TarifaClienteFrequente implements RegraTarifa {

    private final int hospedagensMinimas;
    private final double percentualDesconto;

    public TarifaClienteFrequente() {
        this(5, 0.10);
    }

    public TarifaClienteFrequente(int hospedagensMinimas, double percentualDesconto) {
        this.hospedagensMinimas = hospedagensMinimas;
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public String getNome() {
        return "CLIENTE_FREQUENTE";
    }

    @Override
    public int getPrioridade() {
        return 30;
    }

    @Override
    public boolean isAplicavel(ContextoTarifa contexto) {
        return contexto.getTotalHospedagensCliente() >= hospedagensMinimas;
    }

    @Override
    public double aplicar(ContextoTarifa contexto, double valorDiariaAtual) {
        return valorDiariaAtual * (1 - percentualDesconto);
    }
}
