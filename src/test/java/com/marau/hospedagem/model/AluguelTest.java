package com.marau.hospedagem.model;

import com.marau.hospedagem.exception.CapacidadeExcedidaException;
import com.marau.hospedagem.exception.DataInvalidaException;
import com.marau.hospedagem.model.enums.StatusAluguel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes das regras do Aluguel: cálculo de diárias (regra das 12h),
 * valor final, validação de datas/capacidade e transições de status.
 * (Sprint 3 - "cálculo de diária", "limites de hóspedes", tratamento de exceções)
 */
@DisplayName("Aluguel - diárias, validações e status")
class AluguelTest {

    /** Quarto individual com diária de 100 e capacidade 2. */
    private QuartoIndividual quartoPadrao() {
        return new QuartoIndividual("101", 100.0, false, false, 0.0, 0.0, 2, 0.0);
    }

    @Test
    @DisplayName("Calcula 2 diárias quando saída ocorre às 12h (sem diária extra)")
    void calculaDiariasBasico() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 10, 12, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 1, 12, 12, 0);
        Aluguel aluguel = new Aluguel(null, quartoPadrao(), null, entrada, saida, 1);

        assertEquals(2, aluguel.getQuantidadeDiarias());
        assertEquals(200.0, aluguel.getValorFinal(), 0.001);
    }

    @Test
    @DisplayName("Saída após as 12h adiciona uma diária extra")
    void saidaAposMeioDiaAdicionaDiaria() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 10, 12, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 1, 12, 13, 0);
        Aluguel aluguel = new Aluguel(null, quartoPadrao(), null, entrada, saida, 1);

        assertEquals(3, aluguel.getQuantidadeDiarias());
        assertEquals(300.0, aluguel.getValorFinal(), 0.001);
    }

    @Test
    @DisplayName("Data de saída anterior à entrada lança DataInvalidaException")
    void saidaAntesDaEntradaLancaExcecao() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 10, 12, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 1, 9, 12, 0);
        assertThrows(DataInvalidaException.class,
                () -> new Aluguel(null, quartoPadrao(), null, entrada, saida, 1));
    }

    @Test
    @DisplayName("Datas nulas lançam DataInvalidaException")
    void datasNulasLancamExcecao() {
        LocalDateTime saida = LocalDateTime.of(2025, 1, 12, 12, 0);
        assertThrows(DataInvalidaException.class,
                () -> new Aluguel(null, quartoPadrao(), null, null, saida, 1));
    }

    @Test
    @DisplayName("Número de hóspedes acima da capacidade lança CapacidadeExcedidaException")
    void hospedesAcimaDaCapacidadeLancaExcecao() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 10, 12, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 1, 12, 12, 0);
        // quarto com capacidade 2, solicitando 3 hóspedes
        assertThrows(CapacidadeExcedidaException.class,
                () -> new Aluguel(null, quartoPadrao(), null, entrada, saida, 3));
    }

    @Test
    @DisplayName("Fluxo de status: RESERVADO -> ATIVO -> ENCERRADO")
    void fluxoDeStatus() {
        Aluguel aluguel = aluguelValido();
        assertEquals(StatusAluguel.RESERVADO, aluguel.getStatus());

        aluguel.confirmar();
        assertEquals(StatusAluguel.ATIVO, aluguel.getStatus());

        aluguel.encerrar();
        assertEquals(StatusAluguel.ENCERRADO, aluguel.getStatus());
    }

    @Test
    @DisplayName("Cancelar um aluguel reservado o coloca como CANCELADO")
    void cancelarReservado() {
        Aluguel aluguel = aluguelValido();
        aluguel.cancelar();
        assertEquals(StatusAluguel.CANCELADO, aluguel.getStatus());
    }

    @Test
    @DisplayName("Encerrar um aluguel não ativo lança IllegalStateException")
    void encerrarNaoAtivoLancaExcecao() {
        Aluguel aluguel = aluguelValido(); // RESERVADO
        assertThrows(IllegalStateException.class, aluguel::encerrar);
    }

    @Test
    @DisplayName("Cancelar um aluguel já encerrado lança IllegalStateException")
    void cancelarEncerradoLancaExcecao() {
        Aluguel aluguel = aluguelValido();
        aluguel.confirmar();
        aluguel.encerrar();
        assertThrows(IllegalStateException.class, aluguel::cancelar);
    }

    private Aluguel aluguelValido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 10, 12, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 1, 12, 12, 0);
        return new Aluguel(null, quartoPadrao(), null, entrada, saida, 1);
    }
}
