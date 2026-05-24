package com.marau.hospedagem.controller;

import com.marau.hospedagem.dto.AluguelDTO;
import com.marau.hospedagem.model.Aluguel;
import com.marau.hospedagem.service.AluguelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alugueis")
public class AluguelController {

    private final AluguelService service;

    public AluguelController(AluguelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Aluguel> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/residencia/{residenciaId}")
    public List<Aluguel> listarPorResidencia(@PathVariable Long residenciaId) {
        return service.listarPorResidencia(residenciaId);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Aluguel> listarPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    /**
     * Cria um novo aluguel/reserva.
     * Exemplo de body para QuartoDuplo com berço:
     * {
     *   "residenciaId": 1, "quartoId": 2, "clienteId": 1,
     *   "dataEntrada": "2025-07-10T14:00:00",
     *   "dataSaida": "2025-07-14T12:00:00",
     *   "numHospedes": 2, "solicitarBerco": true
     * }
     */
    @PostMapping
    public ResponseEntity<Aluguel> criar(@RequestBody AluguelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Aluguel> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmar(id));
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<Aluguel> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(service.encerrar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Aluguel> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    /**
     * Retorna o recibo formatado do aluguel.
     */
    @GetMapping("/{id}/recibo")
    public ResponseEntity<String> getRecibo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRecibo(id));
    }
}
