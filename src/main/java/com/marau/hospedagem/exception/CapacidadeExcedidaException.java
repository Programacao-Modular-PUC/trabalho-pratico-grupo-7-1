package com.marau.hospedagem.exception;

/**
 * Lançada quando o número de hóspedes informado ultrapassa a
 * capacidade máxima do quarto.
 */
public class CapacidadeExcedidaException extends HospedagemException {

    public CapacidadeExcedidaException(String mensagem) {
        super(mensagem);
    }

    public CapacidadeExcedidaException(int solicitado, int maximo) {
        super("Número de hóspedes (" + solicitado + ") excede a capacidade máxima do quarto (" + maximo + ").");
    }
}
