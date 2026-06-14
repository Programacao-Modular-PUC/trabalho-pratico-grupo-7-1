package com.marau.hospedagem.model;

import com.marau.hospedagem.exception.RecursoNaoPermitidoException;
import com.marau.hospedagem.model.enums.TipoCamaCasal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regras de berço (Sprint 3): berço só é permitido no Quarto Duplo.
 * Solicitar berço em quarto Individual ou Família lança
 * RecursoNaoPermitidoException.
 */
@DisplayName("Regras de berço por tipo de quarto")
class BercoRegraTest {

    @Test
    @DisplayName("Berço em quarto INDIVIDUAL não é permitido")
    void bercoIndividualNaoPermitido() {
        QuartoIndividual quarto = new QuartoIndividual("101", 100.0, false, false, 0.0, 0.0, 1, 0.0);
        assertThrows(RecursoNaoPermitidoException.class, quarto::solicitarBerco);
    }

    @Test
    @DisplayName("Berço em quarto FAMÍLIA não é permitido")
    void bercoFamiliaNaoPermitido() {
        QuartoFamilia quarto = new QuartoFamilia("301", 300.0, false, false, 0.0, 0.0,
                2, 1, 0, 1, 0.10);
        assertThrows(RecursoNaoPermitidoException.class, quarto::solicitarBerco);
    }

    @Test
    @DisplayName("Berço em quarto DUPLO é permitido e aumenta a capacidade")
    void bercoDuploPermitido() {
        QuartoDuplo quarto = new QuartoDuplo("201", 200.0, false, false, 0.0, 0.0,
                TipoCamaCasal.CASAL_COMUM, 50.0, 30.0);
        assertDoesNotThrow(quarto::solicitarBerco);
        assertTrue(quarto.getPossuiBerco());
        assertEquals(3, quarto.getCapacidadeMaxima());
    }
}
