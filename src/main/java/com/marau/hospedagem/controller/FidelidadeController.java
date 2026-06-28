package com.marau.hospedagem.controller;

import com.marau.hospedagem.dto.ResultadoFidelidadeDTO;
import com.marau.hospedagem.service.fidelidade.FidelidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints da funcionalidade Programa de Fidelidade (Decorator + Factory Method).
 */
@RestController
@RequestMapping("/fidelidade")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    public FidelidadeController(FidelidadeService fidelidadeService) {
        this.fidelidadeService = fidelidadeService;
    }

    /** GET /fidelidade/cliente/{id} — categoria e benefícios do cliente. */
    @GetMapping("/cliente/{id}")
    public ResponseEntity<ResultadoFidelidadeDTO> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(fidelidadeService.consultar(id));
    }

    /**
     * GET /fidelidade/cliente/{id}/simular?valor=&diarias=
     * Simula os benefícios sobre uma hospedagem hipotética, retornando o valor
     * final já com o desconto de fidelidade.
     */
    @GetMapping("/cliente/{id}/simular")
    public ResponseEntity<ResultadoFidelidadeDTO> simular(
            @PathVariable Long id,
            @RequestParam double valor,
            @RequestParam(defaultValue = "1") int diarias) {
        return ResponseEntity.ok(fidelidadeService.simular(id, valor, diarias));
    }
}
