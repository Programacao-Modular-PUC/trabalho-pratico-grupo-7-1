package com.marau.hospedagem.exception;

/**
 * Lançada quando se solicita um recurso não permitido para o tipo de quarto.
 * Exemplo do enunciado: solicitar berço em um quarto individual.
 */
public class RecursoNaoPermitidoException extends HospedagemException {

    public RecursoNaoPermitidoException(String mensagem) {
        super(mensagem);
    }
}
