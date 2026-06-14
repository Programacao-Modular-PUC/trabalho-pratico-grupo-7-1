package com.marau.hospedagem.model;

import com.marau.hospedagem.model.enums.TipoCamaCasal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de cálculo de diária, berço e capacidade do Quarto Duplo.
 * (Sprint 3 - "cálculo de diária", "regras de berço" e "limites de hóspedes")
 */
@DisplayName("QuartoDuplo - diária, berço e capacidade")
class QuartoDuploTest {

    private QuartoDuplo novoDuplo() {
        return new QuartoDuplo("201", 200.0, false, false, 0.0, 0.0,
                TipoCamaCasal.CASAL_COMUM, 50.0, 30.0);
    }

    @Test
    @DisplayName("Diária = base + adicional de conforto (sem berço)")
    void diariaSemBerco() {
        QuartoDuplo quarto = novoDuplo();
        // 200 + 30 (conforto) = 230
        assertEquals(230.0, quarto.calcularValorDiaria(), 0.001);
    }

    @Test
    @DisplayName("Diária soma a taxa de berço quando solicitado")
    void diariaComBerco() {
        QuartoDuplo quarto = novoDuplo();
        quarto.solicitarBerco();
        // 200 + 30 (conforto) + 50 (berço) = 280
        assertEquals(280.0, quarto.calcularValorDiaria(), 0.001);
    }

    @Test
    @DisplayName("Diária soma ar, hidromassagem e conforto")
    void diariaComArHidroConforto() {
        QuartoDuplo quarto = new QuartoDuplo("202", 200.0, true, true, 20.0, 40.0,
                TipoCamaCasal.QUEEN, 50.0, 30.0);
        // 200 + 20 (ar) + 40 (hidro) + 30 (conforto) = 290
        assertEquals(290.0, quarto.calcularValorDiaria(), 0.001);
    }

    @Test
    @DisplayName("Capacidade é 2 sem berço e 3 com berço")
    void capacidadeComESemBerco() {
        QuartoDuplo quarto = novoDuplo();
        assertEquals(2, quarto.getCapacidadeMaxima());
        quarto.solicitarBerco();
        assertEquals(3, quarto.getCapacidadeMaxima());
        assertTrue(quarto.getPossuiBerco());
    }

    @Test
    @DisplayName("Remover berço volta a capacidade para 2")
    void removerBercoVoltaCapacidade() {
        QuartoDuplo quarto = novoDuplo();
        quarto.solicitarBerco();
        quarto.removerBerco();
        assertEquals(2, quarto.getCapacidadeMaxima());
    }
}
