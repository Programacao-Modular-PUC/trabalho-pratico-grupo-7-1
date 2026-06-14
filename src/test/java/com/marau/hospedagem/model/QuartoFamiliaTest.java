package com.marau.hospedagem.model;

import com.marau.hospedagem.exception.CapacidadeExcedidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes de cálculo por número de hóspedes, descontos progressivos e
 * capacidade do Quarto Família.
 * (Sprint 3 - "cálculo de diária", "limites de hóspedes")
 */
@DisplayName("QuartoFamilia - diária por hóspedes, descontos e capacidade")
class QuartoFamiliaTest {

    /** Cria um Quarto Família com base e configuração de camas informadas. */
    private QuartoFamilia familia(double base, int solteiro, int casal, int queenKing) {
        return new QuartoFamilia("301", base, false, false, 0.0, 0.0,
                solteiro, casal, queenKing, 1, 0.10);
    }

    @Test
    @DisplayName("Capacidade = solteiro + 2*casal + 2*queen/king")
    void capacidadePorCamas() {
        QuartoFamilia quarto = familia(300.0, 2, 2, 1); // 2 + 4 + 2 = 8
        assertEquals(8, quarto.getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Diária com 1 hóspede aplica o percentual por hóspede")
    void diariaUmHospede() {
        QuartoFamilia quarto = familia(300.0, 2, 2, 1);
        // 300 * (1 + 0.10*1) = 330, sem desconto
        assertEquals(330.0, quarto.calcularComHospedes(1), 0.001);
    }

    @Test
    @DisplayName("5 hóspedes aplicam 10% de desconto")
    void desconto10PorCento() {
        QuartoFamilia quarto = familia(300.0, 0, 3, 0); // capacidade 6
        // 300 * (1 + 0.10*5) = 450; desconto 10% -> 405
        assertEquals(405.0, quarto.calcularComHospedes(5), 0.001);
    }

    @Test
    @DisplayName("7 hóspedes aplicam 15% de desconto")
    void desconto15PorCento() {
        QuartoFamilia quarto = familia(300.0, 1, 3, 0); // capacidade 7
        // 300 * (1 + 0.10*7) = 510; desconto 15% -> 433.5
        assertEquals(433.5, quarto.calcularComHospedes(7), 0.001);
    }

    @Test
    @DisplayName("10 hóspedes aplicam 20% de desconto")
    void desconto20PorCento() {
        QuartoFamilia quarto = familia(300.0, 2, 4, 0); // capacidade 10
        // 300 * (1 + 0.10*10) = 600; desconto 20% -> 480
        assertEquals(480.0, quarto.calcularComHospedes(10), 0.001);
    }

    @Test
    @DisplayName("Faixas de desconto progressivo por número de hóspedes")
    void faixasDeDesconto() {
        QuartoFamilia quarto = familia(300.0, 2, 4, 0);
        assertEquals(0.00, quarto.calcularDesconto(4), 0.001);
        assertEquals(0.10, quarto.calcularDesconto(5), 0.001);
        assertEquals(0.10, quarto.calcularDesconto(6), 0.001);
        assertEquals(0.15, quarto.calcularDesconto(7), 0.001);
        assertEquals(0.15, quarto.calcularDesconto(9), 0.001);
        assertEquals(0.20, quarto.calcularDesconto(10), 0.001);
        assertEquals(0.20, quarto.calcularDesconto(12), 0.001);
    }

    @Test
    @DisplayName("Exceder a capacidade lança CapacidadeExcedidaException")
    void excederCapacidadeLancaExcecao() {
        QuartoFamilia quarto = familia(300.0, 0, 3, 0); // capacidade 6
        assertThrows(CapacidadeExcedidaException.class, () -> quarto.calcularComHospedes(7));
    }
}
