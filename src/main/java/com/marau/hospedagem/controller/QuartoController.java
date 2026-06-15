package com.marau.hospedagem.controller;

import com.marau.hospedagem.dto.QuartoDTO;
import com.marau.hospedagem.model.Quarto;
import com.marau.hospedagem.service.QuartoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    /**
     * GET /quartos?residenciaId={id} — lista todos os quartos de uma residência.
     * GET /quartos?residenciaId={id}&disponivel=true — filtra apenas os quartos livres.
     * GET /quartos?residenciaId={id}&tipo=DUPLO — filtra por tipo (Sprint 3).
     */
    @GetMapping
    public List<Quarto> listar(
            @RequestParam Long residenciaId,
            @RequestParam(required = false) Boolean disponivel,
            @RequestParam(required = false) String tipo) {

        if (Boolean.TRUE.equals(disponivel)) {
            return service.listarDisponiveis(residenciaId);
        }
        if (tipo != null && !tipo.isBlank()) {
            return service.listarPorTipo(residenciaId, tipo);
        }
        return service.listarPorResidencia(residenciaId);
    }

    /** GET /quartos/{id} — busca quarto por id */
    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /** POST /quartos — cria novo quarto (Individual, Duplo ou Família) */
    @PostMapping
    public ResponseEntity<Quarto> criar(@RequestBody QuartoDTO dto) {
        Quarto salvo = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    /** DELETE /quartos/{id} — remove quarto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
