package com.marau.hospedagem.exception;

/**
 * Exceção base para as regras de negócio do sistema de hospedagem.
 *
 * <p>Todas as exceções personalizadas da aplicação herdam desta classe,
 * o que permite capturá-las de forma uniforme (por exemplo, em um
 * {@code @RestControllerAdvice}) e diferenciá-las das exceções genéricas
 * do Java.</p>
 */
public class HospedagemException extends RuntimeException {

    public HospedagemException(String mensagem) {
        super(mensagem);
    }

    public HospedagemException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
