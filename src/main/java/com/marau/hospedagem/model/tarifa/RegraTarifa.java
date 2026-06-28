package com.marau.hospedagem.model.tarifa;

/**
 * Padrão <b>Strategy</b>.
 *
 * <p>Define o contrato de uma regra de cálculo de diária. Cada situação de
 * negócio (alta temporada, baixa temporada, feriado, promoção, cliente
 * frequente, etc.) é uma estratégia concreta e independente.</p>
 *
 * <p>Adicionar uma nova forma de tarifação consiste apenas em criar uma nova
 * implementação desta interface e registrá-la no {@code GerenciadorTarifas},
 * sem alterar nenhuma regra existente (princípio Aberto/Fechado).</p>
 */
public interface RegraTarifa {

    /**
     * Nome identificador da regra (usado em logs, relatórios e remoção).
     */
    String getNome();

    /**
     * Prioridade de aplicação. Regras com menor valor são aplicadas primeiro.
     * Permite, por exemplo, aplicar acréscimos de temporada antes de descontos.
     */
    int getPrioridade();

    /**
     * Indica se a regra deve incidir sobre o contexto informado.
     */
    boolean isAplicavel(ContextoTarifa contexto);

    /**
     * Aplica o ajuste sobre o valor da diária corrente e devolve o novo valor.
     *
     * @param contexto         dados da reserva/cliente
     * @param valorDiariaAtual valor da diária já ajustado pelas regras anteriores
     * @return novo valor da diária após esta regra
     */
    double aplicar(ContextoTarifa contexto, double valorDiariaAtual);
}
