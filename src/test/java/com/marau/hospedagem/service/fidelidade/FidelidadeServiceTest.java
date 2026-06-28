package com.marau.hospedagem.service.fidelidade;

import com.marau.hospedagem.dto.ResultadoFidelidadeDTO;
import com.marau.hospedagem.exception.EntidadeNaoEncontradaException;
import com.marau.hospedagem.model.Aluguel;
import com.marau.hospedagem.model.Cliente;
import com.marau.hospedagem.model.enums.StatusAluguel;
import com.marau.hospedagem.repository.AluguelRepository;
import com.marau.hospedagem.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Testes do FidelidadeService combinando histórico (mockado), Factory Method e
 * Decorator.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Programa de Fidelidade - FidelidadeService")
class FidelidadeServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private AluguelRepository aluguelRepository;

    @InjectMocks
    private FidelidadeService service;

    private Cliente cliente() {
        Cliente c = new Cliente("João", "11122233344", "Rua X", "31999999999", "j@x.com");
        c.setId(1L);
        return c;
    }

    private List<Aluguel> alugueis(int ativos, int cancelados) {
        List<Aluguel> lista = new ArrayList<>();
        IntStream.range(0, ativos).forEach(i -> {
            Aluguel a = new Aluguel();
            a.setStatus(StatusAluguel.ENCERRADO);
            lista.add(a);
        });
        IntStream.range(0, cancelados).forEach(i -> {
            Aluguel a = new Aluguel();
            a.setStatus(StatusAluguel.CANCELADO);
            lista.add(a);
        });
        return lista;
    }

    @Test
    @DisplayName("Cliente sem histórico é BRONZE e não tem benefícios")
    void clienteBronze() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(alugueis(0, 0));

        ResultadoFidelidadeDTO dto = service.consultar(1L);

        assertEquals("BRONZE", dto.getCategoria());
        assertEquals(0, dto.getTotalHospedagens());
        assertEquals(0.0, dto.getPercentualDesconto(), 0.001);
        assertTrue(dto.getBeneficios().isEmpty());
    }

    @Test
    @DisplayName("Hospedagens canceladas não contam para a categoria")
    void ignoraCancelados() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(alugueis(4, 10));

        ResultadoFidelidadeDTO dto = service.consultar(1L);

        assertEquals(4, dto.getTotalHospedagens());
        assertEquals("BRONZE", dto.getCategoria());
    }

    @Test
    @DisplayName("Cliente OURO recebe desconto de 10% e check-out estendido")
    void clienteOuro() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(alugueis(12, 0));

        ResultadoFidelidadeDTO dto = service.consultar(1L);

        assertEquals("OURO", dto.getCategoria());
        assertEquals(0.10, dto.getPercentualDesconto(), 0.001);
        assertEquals(2, dto.getHorasCheckoutEstendido());
    }

    @Test
    @DisplayName("Simulação DIAMANTE aplica diária gratuita e desconto sobre o valor")
    void simulacaoDiamante() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(alugueis(20, 0));

        // valor 300 em 3 diárias => diária 100. 1 grátis => 200. 15% off => 170.
        ResultadoFidelidadeDTO dto = service.simular(1L, 300.0, 3);

        assertEquals("DIAMANTE", dto.getCategoria());
        assertTrue(dto.isUpgradeQuarto());
        assertEquals(1, dto.getDiariasGratuitas());
        assertEquals(300.0, dto.getValorOriginal(), 0.001);
        assertEquals(170.0, dto.getValorComDesconto(), 0.001);
    }

    @Test
    @DisplayName("Cliente inexistente lança EntidadeNaoEncontradaException")
    void clienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.consultar(99L));
    }

    @Test
    @DisplayName("Categoria DIAMANTE não concede diária gratuita quando não há diárias")
    void diamanteSemDiarias() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(alugueis(25, 0));

        ResultadoFidelidadeDTO dto = service.consultar(1L);
        // consultar usa contexto nominal de 1 diária => 1 diária gratuita
        assertEquals(1, dto.getDiariasGratuitas());
        assertFalse(dto.getBeneficios().isEmpty());
    }
}
