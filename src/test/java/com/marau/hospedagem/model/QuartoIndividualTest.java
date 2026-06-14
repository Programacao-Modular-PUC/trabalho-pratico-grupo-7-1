package com.marau.hospedagem.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes de cálculo de diária e capacidade do Quarto Individual.
 * (Sprint 3 - "cálculo de diária por tipo de quarto" e "limites de hóspedes")
 */
@DisplayName("QuartoIndividual - diária e capacidade")
class QuartoIndividualTest {

    @Test
    @DisplayName("Diária com 1 cama é igual ao valor base")
    void diariaComUmaCama() {
        QuartoIndividual quarto = new QuartoIndividual("101", 100.0, false, false, 0.0, 0.0, 1, 0.0);
        assertEquals(100.0, quarto.calcularValorDiaria(), 0.001);
    }

    @Test
    @DisplayName("Diária soma o adicional de ar-condicionado quando habilitado")
    void diariaComArCondicionado() {
        QuartoIndividual quarto = new QuartoIndividual("102", 100.0, true, false, 20.0, 0.0, 1, 0.0);
        assertEquals(120.0, quarto.calcularValorDiaria(), 0.001);
    }

    @Test
    @DisplayName("Diária soma adicional por cama extra a partir da 2ª cama")
    void diariaComCamasExtras() {
        QuartoIndividual quarto = new QuartoIndividual("103", 100.0, false, false, 0.0, 0.0, 3, 25.0);
        // 100 + 25 * (3 - 1) = 150
        assertEquals(150.0, quarto.calcularValorDiaria(), 0.001);
    }

    @Test
    @DisplayName("Capacidade máxima é igual ao número de camas")
    void capacidadeIgualNumeroDeCamas() {
        QuartoIndividual quarto = new QuartoIndividual("104", 100.0, false, false, 0.0, 0.0, 2, 10.0);
        assertEquals(2, quarto.getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Tipo do quarto é INDIVIDUAL")
    void tipoIndividual() {
        QuartoIndividual quarto = new QuartoIndividual("105", 100.0, false, false, 0.0, 0.0, 1, 0.0);
        assertEquals("INDIVIDUAL", quarto.getTipo());
    }
}
