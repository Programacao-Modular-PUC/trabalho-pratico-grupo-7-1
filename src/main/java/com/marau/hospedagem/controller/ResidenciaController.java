package com.marau.hospedagem.controller;

import com.marau.hospedagem.model.Residencia;
import com.marau.hospedagem.service.ResidenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/residencias")
public class ResidenciaController {

    private final ResidenciaService service;

    public ResidenciaController(ResidenciaService service) {
        this.service = service;
    }

    /** GET /residencias — lista todas as residências */
    @GetMapping
    public List<Residencia> listar() {
        return service.listarTodas();
    }

    /** GET /residencias/{id} — busca por id */
    @GetMapping("/{id}")
    public ResponseEntity<Residencia> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /** POST /residencias — cadastra nova residência */
    @PostMapping
    public ResponseEntity<Residencia> criar(@RequestBody Residencia residencia) {
        Residencia salva = service.criar(residencia);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(location).body(salva);
    }

    /** PUT /residencias/{id} — atualiza residência */
    @PutMapping("/{id}")
    public ResponseEntity<Residencia> atualizar(@PathVariable Long id,
                                                @RequestBody Residencia dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    /** DELETE /residencias/{id} — remove residência */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
