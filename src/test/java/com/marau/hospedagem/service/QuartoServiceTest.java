package com.marau.hospedagem.service;

import com.marau.hospedagem.dto.QuartoDTO;
import com.marau.hospedagem.model.Quarto;
import com.marau.hospedagem.model.QuartoDuplo;
import com.marau.hospedagem.model.QuartoFamilia;
import com.marau.hospedagem.model.QuartoIndividual;
import com.marau.hospedagem.model.Residencia;
import com.marau.hospedagem.model.enums.StatusQuarto;
import com.marau.hospedagem.model.enums.TipoCamaCasal;
import com.marau.hospedagem.repository.QuartoRepository;
import com.marau.hospedagem.repository.ResidenciaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes do QuartoService com Mockito, focados no novo requisito de
 * filtro por tipo de quarto e na listagem de disponíveis.
 * (Sprint 3 - novo requisito "Filtro por tipo de quarto")
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QuartoService - filtro por tipo e disponibilidade")
class QuartoServiceTest {

    @Mock
    private QuartoRepository quartoRepository;
    @Mock
    private ResidenciaRepository residenciaRepository;

    @InjectMocks
    private QuartoService service;

    private QuartoIndividual individual() {
        return new QuartoIndividual("101", 100.0, false, false, 0.0, 0.0, 1, 0.0);
    }

    private QuartoDuplo duplo() {
        return new QuartoDuplo("201", 200.0, false, false, 0.0, 0.0,
                TipoCamaCasal.CASAL_COMUM, 50.0, 30.0);
    }

    private QuartoFamilia familia() {
        return new QuartoFamilia("301", 300.0, false, false, 0.0, 0.0, 2, 1, 0, 1, 0.10);
    }

    @Test
    @DisplayName("Filtra apenas os quartos do tipo informado")
    void filtraPorTipo() {
        when(quartoRepository.findByResidenciaId(1L))
                .thenReturn(List.of(individual(), duplo(), familia()));

        List<Quarto> duplos = service.listarPorTipo(1L, "DUPLO");

        assertEquals(1, duplos.size());
        assertEquals("DUPLO", duplos.get(0).getTipo());
    }

    @Test
    @DisplayName("Filtro por tipo é case-insensitive")
    void filtroCaseInsensitive() {
        when(quartoRepository.findByResidenciaId(1L))
                .thenReturn(List.of(individual(), duplo()));

        assertEquals(1, service.listarPorTipo(1L, "individual").size());
    }

    @Test
    @DisplayName("Tipo nulo lança IllegalArgumentException")
    void tipoNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> service.listarPorTipo(1L, null));
    }

    @Test
    @DisplayName("Listar disponíveis delega ao repositório filtrando por status LIVRE")
    void listarDisponiveis() {
        when(quartoRepository.findByResidenciaIdAndStatus(1L, StatusQuarto.LIVRE))
                .thenReturn(List.of(individual()));

        assertEquals(1, service.listarDisponiveis(1L).size());
    }

    private Residencia residencia() {
        return new Residencia("Rua A", "10", "Centro", "30000-000", "31999999999", "r@a.com");
    }

    @Test
    @DisplayName("Criar quarto duplo com sucesso adiciona o quarto à residência")
    void criarQuartoDuploComSucesso() {
        QuartoDTO dto = new QuartoDTO();
        dto.setResidenciaId(1L);
        dto.setTipo("duplo");
        dto.setIdentificacao("201");
        dto.setValorBase(200.0);
        dto.setTipoCama("QUEEN");
        dto.setTaxaBerco(50.0);
        dto.setAdicionalConforto(30.0);
        Residencia residencia = residencia();
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia));
        when(quartoRepository.save(any(Quarto.class))).thenAnswer(i -> i.getArgument(0));

        Quarto criado = service.criar(dto);

        assertEquals("DUPLO", criado.getTipo());
        assertEquals(1, residencia.getQuartos().size());
    }

    @Test
    @DisplayName("Criar quarto com tipo nulo lança IllegalArgumentException (trata NPE do Java)")
    void criarQuartoComTipoNulo() {
        QuartoDTO dto = new QuartoDTO();
        dto.setResidenciaId(1L);
        dto.setTipo(null);
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia()));

        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
    }

    @Test
    @DisplayName("Criar quarto com tipo inválido lança IllegalArgumentException")
    void criarQuartoComTipoInvalido() {
        QuartoDTO dto = new QuartoDTO();
        dto.setResidenciaId(1L);
        dto.setTipo("CHALE");
        dto.setIdentificacao("X1");
        dto.setValorBase(100.0);
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia()));

        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
    }

    @Test
    @DisplayName("Criar quarto duplo com tipo de cama inválido lança IllegalArgumentException")
    void criarDuploComTipoCamaInvalido() {
        QuartoDTO dto = new QuartoDTO();
        dto.setResidenciaId(1L);
        dto.setTipo("DUPLO");
        dto.setIdentificacao("201");
        dto.setValorBase(200.0);
        dto.setTipoCama("QUEEN_SIZE");
        when(residenciaRepository.findById(1L)).thenReturn(Optional.of(residencia()));

        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
    }
}
