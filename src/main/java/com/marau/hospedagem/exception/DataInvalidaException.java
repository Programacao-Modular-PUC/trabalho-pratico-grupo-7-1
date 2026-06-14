package com.marau.hospedagem.exception;

/**
 * Lançada quando as datas de entrada/saída de um aluguel são inválidas
 * (nulas, ou data de saída anterior/igual à data de entrada).
 */
public class DataInvalidaException extends HospedagemException {

    public DataInvalidaException(String mensagem) {
        super(mensagem);
    }
}
