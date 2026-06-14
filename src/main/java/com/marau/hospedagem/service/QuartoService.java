package com.marau.hospedagem.service;

import com.marau.hospedagem.dto.QuartoDTO;
import com.marau.hospedagem.exception.EntidadeNaoEncontradaException;
import com.marau.hospedagem.model.*;
import com.marau.hospedagem.model.enums.StatusQuarto;
import com.marau.hospedagem.model.enums.TipoCamaCasal;
import com.marau.hospedagem.repository.QuartoRepository;
import com.marau.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ResidenciaRepository residenciaRepository;

    public QuartoService(QuartoRepository quartoRepository, ResidenciaRepository residenciaRepository) {
        this.quartoRepository = quartoRepository;
        this.residenciaRepository = residenciaRepository;
    }

    public List<Quarto> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    public List<Quarto> listarDisponiveis(Long residenciaId) {
        return quartoRepository.findByResidenciaIdAndStatus(residenciaId, StatusQuarto.LIVRE);
    }

    /**
     * Filtra os quartos de uma residência por tipo (NOVO REQUISITO - Sprint 3).
     * A comparação é case-insensitive: INDIVIDUAL, DUPLO ou FAMILIA.
     *
     * @throws IllegalArgumentException se o tipo não for informado.
     */
    public List<Quarto> listarPorTipo(Long residenciaId, String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("O tipo de quarto deve ser informado.");
        }
        String alvo = tipo.trim();
        return listarPorResidencia(residenciaId).stream()
                .filter(quarto -> quarto.getTipo().equalsIgnoreCase(alvo))
                .toList();
    }

    public Quarto buscarPorId(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Quarto", id));
    }

    /**
     * Cria um quarto com base no tipo informado no DTO.
     */
    public Quarto criar(QuartoDTO dto) {
        Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Residência", dto.getResidenciaId()));

        Quarto quarto = criarPorTipo(dto);
        residencia.adicionarQuarto(quarto);
        return quartoRepository.save(quarto);
    }

    /**
     * Factory method para criar o tipo correto de quarto.
     */
    private Quarto criarPorTipo(QuartoDTO dto) {
        if (dto.getTipo() == null || dto.getTipo().isBlank()) {
            throw new IllegalArgumentException("O tipo de quarto deve ser informado.");
        }
        return switch (dto.getTipo().trim().toUpperCase()) {
            case "INDIVIDUAL" -> new QuartoIndividual(
                    dto.getIdentificacao(), dto.getValorBase(),
                    dto.getPossuiAr(), dto.getPossuiHidro(),
                    dto.getAdicionalAr(), dto.getAdicionalHidro(),
                    dto.getQuantidadeCamas(), dto.getAdicionalPorCama()
            );
            case "DUPLO" -> new QuartoDuplo(
                    dto.getIdentificacao(), dto.getValorBase(),
                    dto.getPossuiAr(), dto.getPossuiHidro(),
                    dto.getAdicionalAr(), dto.getAdicionalHidro(),
                    converterTipoCama(dto.getTipoCama()),
                    dto.getTaxaBerco(), dto.getAdicionalConforto()
            );
            case "FAMILIA" -> new QuartoFamilia(
                    dto.getIdentificacao(), dto.getValorBase(),
                    dto.getPossuiAr(), dto.getPossuiHidro(),
                    dto.getAdicionalAr(), dto.getAdicionalHidro(),
                    dto.getCamasSolteiro(), dto.getCamasCasal(),
                    dto.getCamasQueenKing(), dto.getQuantidadeAmbientes(),
                    dto.getPercentualPorHospede()
            );
            default -> throw new IllegalArgumentException("Tipo de quarto inválido: " + dto.getTipo());
        };
    }

    /**
     * Converte a string do tipo de cama no enum correspondente, tratando a
     * exceção do Java ({@link IllegalArgumentException} lançada por
     * {@code valueOf}) e devolvendo uma mensagem clara.
     */
    private TipoCamaCasal converterTipoCama(String valor) {
        if (valor == null || valor.isBlank()) {
            return TipoCamaCasal.CASAL_COMUM;
        }
        try {
            return TipoCamaCasal.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tipo de cama inválido: " + valor + ". Valores aceitos: CASAL_COMUM, QUEEN, KING.", e);
        }
    }

    public void deletar(Long id) {
        buscarPorId(id);
        quartoRepository.deleteById(id);
    }
}
