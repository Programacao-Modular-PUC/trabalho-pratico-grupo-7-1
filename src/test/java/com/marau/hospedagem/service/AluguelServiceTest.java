package com.marau.hospedagem.service;

import com.marau.hospedagem.dto.AluguelDTO;
import com.marau.hospedagem.exception.EntidadeNaoEncontradaException;
import com.marau.hospedagem.exception.QuartoIndisponivelException;
import com.marau.hospedagem.exception.RecursoNaoPermitidoException;
import com.marau.hospedagem.model.Aluguel;
import com.marau.hospedagem.model.Cliente;
import com.marau.hospedagem.model.Pagamento;
import com.marau.hospedagem.model.QuartoIndividual;
import com.marau.hospedagem.model.Residencia;
import com.marau.hospedagem.model.enums.StatusAluguel;
import com.marau.hospedagem.model.enums.StatusQuarto;
import com.marau.hospedagem.repository.AluguelRepository;
import com.marau.hospedagem.repository.ClienteRepository;
import com.marau.hospedagem.repository.QuartoRepository;
import com.marau.hospedagem.repository.ResidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes do AluguelService usando Mockito (sem banco de dados).
 * Cobre disponibilidade, exceções personalizadas, cancelamento e histórico.
 * (Sprint 3 - "disponibilidade", tratamento de exceções, novos requisitos)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AluguelService - criação, cancelamento e histórico")
class AluguelServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;
    @Mock
    private ResidenciaRepository residenciaRepository;
    @Mock
    private QuartoRepository quartoRepository;
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AluguelService service;

    private Residencia residencia;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        residencia = new Residencia("Rua A", "10", "Centro", "30000-000", "31999999999", "r@a.com");
        cliente = new Cliente("Maria", "12345678900", "Rua B", "31988888888", "m@b.com");
    }

    private QuartoIndividual quartoLivre() {
        QuartoIndividual quarto = new QuartoIndividual("101", 100.0, false, false, 0.0, 0.0, 2, 0.0);
        quarto.setStatus(StatusQuarto.LIVRE);
        return quarto;
    }

    private AluguelDTO dtoPadrao() {
        AluguelDTO dto = new AluguelDTO();
        dto.setResidenciaId(1L);
        dto.setQuartoId(2L);
        dto.setClienteId(3L);
        dto.setDataEntrada(LocalDateTime.of(2025, 1, 10, 12, 0));
        dto.setDataSaida(LocalDateTime.of(2025, 1, 12, 12, 0));
        dto.setNumHospedes(1);
        dto.setSolicitarBerco(false);
        return dto;
    }

    @Test
    @DisplayName("Criar aluguel com quarto livre reserva o quarto e calcula diárias")
    void criarComSucesso() {
        QuartoIndividual quarto = quartoLivre();
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia));
        when(quartoRepository.findById(2L)).thenReturn(Optional.of(quarto));
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(cliente));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

        Aluguel resultado = service.criar(dtoPadrao());

        assertNotNull(resultado);
        assertEquals(2, resultado.getQuantidadeDiarias());
        assertEquals(StatusQuarto.RESERVADO, quarto.getStatus());
        verify(aluguelRepository).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Criar aluguel com quarto ocupado lança QuartoIndisponivelException")
    void criarQuartoIndisponivel() {
        QuartoIndividual quarto = quartoLivre();
        quarto.setStatus(StatusQuarto.OCUPADO);
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia));
        when(quartoRepository.findById(2L)).thenReturn(Optional.of(quarto));
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(cliente));

        assertThrows(QuartoIndisponivelException.class, () -> service.criar(dtoPadrao()));
        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Solicitar berço em quarto individual lança RecursoNaoPermitidoException")
    void criarBercoEmQuartoIndividual() {
        QuartoIndividual quarto = quartoLivre();
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia));
        when(quartoRepository.findById(2L)).thenReturn(Optional.of(quarto));
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(cliente));

        AluguelDTO dto = dtoPadrao();
        dto.setSolicitarBerco(true);

        assertThrows(RecursoNaoPermitidoException.class, () -> service.criar(dto));
    }

    @Test
    @DisplayName("Residência inexistente lança EntidadeNaoEncontradaException")
    void criarResidenciaInexistente() {
        when(residenciaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.criar(dtoPadrao()));
    }

    @Test
    @DisplayName("Cancelar aluguel libera o quarto e cancela o pagamento")
    void cancelarLiberaQuarto() {
        QuartoIndividual quarto = quartoLivre();
        quarto.setStatus(StatusQuarto.RESERVADO);
        Aluguel aluguel = new Aluguel(residencia, quarto, cliente,
                LocalDateTime.of(2025, 1, 10, 12, 0), LocalDateTime.of(2025, 1, 12, 12, 0), 1);
        Pagamento pagamento = new Pagamento(aluguel);
        aluguel.setPagamento(pagamento);
        when(aluguelRepository.findById(5L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

        Aluguel cancelado = service.cancelar(5L);

        assertEquals(StatusAluguel.CANCELADO, cancelado.getStatus());
        assertEquals(StatusQuarto.LIVRE, quarto.getStatus());
        assertEquals("CANCELADO", pagamento.getStatus());
    }

    @Test
    @DisplayName("Histórico por cliente retorna os aluguéis do cliente")
    void historicoPorCliente() {
        QuartoIndividual quarto = quartoLivre();
        Aluguel aluguel = new Aluguel(residencia, quarto, cliente,
                LocalDateTime.of(2025, 1, 10, 12, 0), LocalDateTime.of(2025, 1, 12, 12, 0), 1);
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(cliente));
        when(aluguelRepository.findByClienteId(3L)).thenReturn(List.of(aluguel));

        List<Aluguel> historico = service.historicoPorCliente(3L);

        assertEquals(1, historico.size());
    }

    @Test
    @DisplayName("Histórico de cliente inexistente lança EntidadeNaoEncontradaException")
    void historicoClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.historicoPorCliente(99L));
    }
}
