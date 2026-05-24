package com.marau.hospedagem.controller;

import com.marau.hospedagem.model.Residencia;
import com.marau.hospedagem.service.ResidenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/residencias")
public class ResidenciaController {

    private final ResidenciaService service;

    public ResidenciaController(ResidenciaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Residencia> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Residencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Residencia> criar(@RequestBody Residencia residencia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(residencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Residencia> atualizar(@PathVariable Long id, @RequestBody Residencia residencia) {
        return ResponseEntity.ok(service.atualizar(id, residencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
