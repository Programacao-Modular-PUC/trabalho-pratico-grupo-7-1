package com.marau.hospedagem.controller;

import com.marau.hospedagem.dto.QuartoDTO;
import com.marau.hospedagem.model.Quarto;
import com.marau.hospedagem.service.QuartoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    @GetMapping("/residencia/{residenciaId}")
    public List<Quarto> listarPorResidencia(@PathVariable Long residenciaId) {
        return service.listarPorResidencia(residenciaId);
    }

    @GetMapping("/residencia/{residenciaId}/disponiveis")
    public List<Quarto> listarDisponiveis(@PathVariable Long residenciaId) {
        return service.listarDisponiveis(residenciaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/valor-diaria")
    public ResponseEntity<Double> calcularDiaria(@PathVariable Long id) {
        Quarto quarto = service.buscarPorId(id);
        return ResponseEntity.ok(quarto.calcularValorDiaria());
    }

    @PostMapping
    public ResponseEntity<Quarto> criar(@RequestBody QuartoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
