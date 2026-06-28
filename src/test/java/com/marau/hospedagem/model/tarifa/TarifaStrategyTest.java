package com.marau.hospedagem.model.tarifa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes das estratégias de tarifação (padrão Strategy), de forma isolada.
 */
@DisplayName("Tarifação Flexível - estratégias (Strategy)")
class TarifaStrategyTest {

    private ContextoTarifa contexto(LocalDate data, double base, int diarias, int hospedagens) {
        return ContextoTarifa.builder()
                .dataReferencia(data)
                .valorDiariaBase(base)
                .quantidadeDiarias(diarias)
                .totalHospedagensCliente(hospedagens)
                .build();
    }

    @Test
    @DisplayName("Alta temporada acrescenta 30% nos meses configurados")
    void altaTemporada() {
        TarifaAltaTemporada regra = new TarifaAltaTemporada();
        ContextoTarifa janeiro = contexto(LocalDate.of(2025, 1, 15), 100.0, 1, 0);
        ContextoTarifa setembro = contexto(LocalDate.of(2025, 9, 15), 100.0, 1, 0);

        assertTrue(regra.isAplicavel(janeiro));
        assertFalse(regra.isAplicavel(setembro));
        assertEquals(130.0, regra.aplicar(janeiro, 100.0), 0.001);
    }

    @Test
    @DisplayName("Baixa temporada concede 15% de desconto")
    void baixaTemporada() {
        TarifaBaixaTemporada regra = new TarifaBaixaTemporada();
        ContextoTarifa abril = contexto(LocalDate.of(2025, 4, 10), 100.0, 1, 0);

        assertTrue(regra.isAplicavel(abril));
        assertEquals(85.0, regra.aplicar(abril, 100.0), 0.001);
    }

    @Test
    @DisplayName("Feriado cadastrado em runtime acrescenta 20%")
    void feriado() {
        TarifaFeriado regra = new TarifaFeriado();
        LocalDate natal = LocalDate.of(2025, 12, 25);
        ContextoTarifa ctx = contexto(natal, 200.0, 1, 0);

        assertFalse(regra.isAplicavel(ctx));
        regra.adicionarFeriado(natal);
        assertTrue(regra.isAplicavel(ctx));
        assertEquals(240.0, regra.aplicar(ctx, 200.0), 0.001);
    }

    @Test
    @DisplayName("Promoção incide apenas dentro do período")
    void promocao() {
        TarifaPromocional regra = new TarifaPromocional(
                "BLACKFRIDAY", LocalDate.of(2025, 11, 20), LocalDate.of(2025, 11, 30), 0.25);
        ContextoTarifa dentro = contexto(LocalDate.of(2025, 11, 25), 100.0, 1, 0);
        ContextoTarifa fora = contexto(LocalDate.of(2025, 12, 1), 100.0, 1, 0);

        assertTrue(regra.isAplicavel(dentro));
        assertFalse(regra.isAplicavel(fora));
        assertEquals(75.0, regra.aplicar(dentro, 100.0), 0.001);
        assertEquals("PROMOCAO_BLACKFRIDAY", regra.getNome());
    }

    @Test
    @DisplayName("Cliente frequente recebe desconto a partir do limiar de hospedagens")
    void clienteFrequente() {
        TarifaClienteFrequente regra = new TarifaClienteFrequente(5, 0.10);
        ContextoTarifa novato = contexto(LocalDate.of(2025, 9, 1), 100.0, 1, 4);
        ContextoTarifa frequente = contexto(LocalDate.of(2025, 9, 1), 100.0, 1, 5);

        assertFalse(regra.isAplicavel(novato));
        assertTrue(regra.isAplicavel(frequente));
        assertEquals(90.0, regra.aplicar(frequente, 100.0), 0.001);
    }

    @Test
    @DisplayName("Alta temporada aceita meses e percentuais customizados")
    void altaTemporadaCustomizada() {
        TarifaAltaTemporada regra = new TarifaAltaTemporada(Set.of(Month.FEBRUARY), 0.50);
        ContextoTarifa fevereiro = contexto(LocalDate.of(2025, 2, 10), 100.0, 1, 0);

        assertTrue(regra.isAplicavel(fevereiro));
        assertEquals(150.0, regra.aplicar(fevereiro, 100.0), 0.001);
    }
}
