package com.marau.hospedagem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Tratamento centralizado de exceções da API (Sprint 3).
 *
 * <p>Converte tanto as exceções personalizadas de regra de negócio quanto as
 * exceções genéricas do Java em respostas HTTP padronizadas
 * ({@link ProblemDetail}), com o código de status apropriado.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Entidade não encontrada -> 404 NOT FOUND. */
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleNaoEncontrada(EntidadeNaoEncontradaException ex) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Quarto indisponível para o período -> 409 CONFLICT. */
    @ExceptionHandler(QuartoIndisponivelException.class)
    public ProblemDetail handleQuartoIndisponivel(QuartoIndisponivelException ex) {
        return montar(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Capacidade excedida ou recurso não permitido -> 422 UNPROCESSABLE ENTITY. */
    @ExceptionHandler({CapacidadeExcedidaException.class, RecursoNaoPermitidoException.class})
    public ProblemDetail handleRegraQuarto(HospedagemException ex) {
        return montar(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /** Datas inválidas -> 400 BAD REQUEST. */
    @ExceptionHandler(DataInvalidaException.class)
    public ProblemDetail handleDataInvalida(DataInvalidaException ex) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Demais regras de negócio -> 400 BAD REQUEST. */
    @ExceptionHandler(HospedagemException.class)
    public ProblemDetail handleHospedagem(HospedagemException ex) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Exceções genéricas do Java (ex.: argumento inválido) -> 400 BAD REQUEST. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Qualquer outra exceção não prevista -> 500 INTERNAL SERVER ERROR. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerica(Exception ex) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado: " + ex.getMessage());
    }

    private ProblemDetail montar(HttpStatus status, String detalhe) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle("Erro no Sistema de Hospedagem");
        problema.setProperty("timestamp", LocalDateTime.now().toString());
        return problema;
    }
}
