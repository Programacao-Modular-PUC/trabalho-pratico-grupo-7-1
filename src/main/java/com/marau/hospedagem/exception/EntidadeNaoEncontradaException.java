package com.marau.hospedagem.exception;

/**
 * Lançada quando uma entidade (Quarto, Cliente, Residência, Aluguel)
 * não é encontrada pelo identificador informado.
 */
public class EntidadeNaoEncontradaException extends HospedagemException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public EntidadeNaoEncontradaException(String entidade, Object id) {
        super(entidade + " não encontrado(a) para o identificador: " + id);
    }
}
