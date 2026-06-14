package com.marau.hospedagem.exception;

/**
 * Lançada quando se tenta alugar um quarto que não está disponível
 * (status diferente de LIVRE) para o período solicitado.
 */
public class QuartoIndisponivelException extends HospedagemException {

    public QuartoIndisponivelException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria a exceção com uma mensagem padrão a partir da identificação do quarto.
     */
    public static QuartoIndisponivelException paraQuarto(String identificacao) {
        return new QuartoIndisponivelException(
                "O quarto " + identificacao + " não está disponível no período solicitado.");
    }
}
