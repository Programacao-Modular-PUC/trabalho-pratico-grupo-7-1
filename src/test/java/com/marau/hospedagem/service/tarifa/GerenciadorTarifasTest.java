package com.marau.hospedagem.service.tarifa;

import com.marau.hospedagem.model.tarifa.ContextoTarifa;
import com.marau.hospedagem.model.tarifa.ResultadoTarifa;
import com.marau.hospedagem.model.tarifa.TarifaPromocional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes do GerenciadorTarifas (Singleton + orquestração das estratégias).
 */
@DisplayName("Tarifação Flexível - GerenciadorTarifas (Singleton)")
class GerenciadorTarifasTest {

    private ContextoTarifa contexto(LocalDate data, double base, int diarias, int hospedagens) {
        return ContextoTarifa.builder()
                .dataReferencia(data)
                .valorDiariaBase(base)
                .quantidadeDiarias(diarias)
                .totalHospedagensCliente(hospedagens)
                .build();
    }

    @Test
    @DisplayName("getInstance retorna sempre a mesma instância")
    void singletonUnico() {
        assertSame(GerenciadorTarifas.getInstance(), GerenciadorTarifas.getInstance());
    }

    @Test
    @DisplayName("Aplica apenas a alta temporada quando só ela é aplicável")
    void apenasAltaTemporada() {
        GerenciadorTarifas gerenciador = GerenciadorTarifas.getInstance();
        ResultadoTarifa resultado = gerenciador.calcular(contexto(LocalDate.of(2025, 1, 10), 100.0, 2, 0));

        assertEquals(130.0, resultado.getValorDiariaFinal(), 0.001);
        assertEquals(260.0, resultado.getValorTotal(), 0.001);
        assertTrue(resultado.getRegrasAplicadas().contains("ALTA_TEMPORADA"));
    }

    @Test
    @DisplayName("Combina alta temporada e cliente frequente na ordem de prioridade")
    void combinaRegras() {
        GerenciadorTarifas gerenciador = GerenciadorTarifas.getInstance();
        ResultadoTarifa resultado = gerenciador.calcular(contexto(LocalDate.of(2025, 1, 10), 100.0, 1, 5));

        // 100 * 1.30 (alta) = 130 ; 130 * 0.90 (frequente) = 117
        assertEquals(117.0, resultado.getValorDiariaFinal(), 0.001);
        assertEquals(2, resultado.getRegrasAplicadas().size());
        assertEquals("ALTA_TEMPORADA", resultado.getRegrasAplicadas().get(0));
        assertEquals("CLIENTE_FREQUENTE", resultado.getRegrasAplicadas().get(1));
    }

    @Test
    @DisplayName("Registrar e remover uma promoção em tempo de execução")
    void registrarERemoverPromocao() {
        GerenciadorTarifas gerenciador = GerenciadorTarifas.getInstance();
        // Junho não é alta nem baixa temporada: isola o efeito da promoção.
        ContextoTarifa junho = contexto(LocalDate.of(2025, 6, 15), 100.0, 1, 0);

        assertEquals(100.0, gerenciador.calcularValorDiaria(junho), 0.001);

        TarifaPromocional promo = new TarifaPromocional(
                "TESTE", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30), 0.20);
        gerenciador.registrarRegra(promo);

        try {
            assertEquals(80.0, gerenciador.calcularValorDiaria(junho), 0.001);
        } finally {
            assertTrue(gerenciador.removerRegra("PROMOCAO_TESTE"));
        }

        assertEquals(100.0, gerenciador.calcularValorDiaria(junho), 0.001);
        assertFalse(gerenciador.removerRegra("PROMOCAO_TESTE"));
    }
}
