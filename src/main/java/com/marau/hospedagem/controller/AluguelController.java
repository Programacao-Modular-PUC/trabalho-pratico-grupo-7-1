package com.marau.hospedagem.controller;

import com.marau.hospedagem.dto.AluguelDTO;
import com.marau.hospedagem.model.Aluguel;
import com.marau.hospedagem.service.AluguelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService service;

    public AluguelController(AluguelService service) {
        this.service = service;
    }

    /**
     * GET /alugueis — lista todos os aluguéis.
     * GET /alugueis?residenciaId={id} — filtra por residência.
     * GET /alugueis?clienteId={id} — filtra por cliente.
     */
    @GetMapping
    public List<Aluguel> listar(
            @RequestParam(required = false) Long residenciaId,
            @RequestParam(required = false) Long clienteId) {

        if (residenciaId != null) {
            return service.listarPorResidencia(residenciaId);
        }
        if (clienteId != null) {
            return service.listarPorCliente(clienteId);
        }
        return service.listarTodos();
    }

    /** GET /alugueis/{id} — busca aluguel por id */
    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * GET /alugueis/historico/cliente/{clienteId}
     * Histórico completo de aluguéis de um cliente (Sprint 3).
     */
    @GetMapping("/historico/cliente/{clienteId}")
    public List<Aluguel> historicoPorCliente(@PathVariable Long clienteId) {
        return service.historicoPorCliente(clienteId);
    }

    /** GET /alugueis/{id}/recibo — retorna o recibo formatado */
    @GetMapping("/{id}/recibo")
    public ResponseEntity<String> recibo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRecibo(id));
    }

    /** POST /alugueis — cria novo aluguel */
    @PostMapping
    public ResponseEntity<Aluguel> criar(@RequestBody AluguelDTO dto) {
        Aluguel salvo = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    /**
     * PATCH /alugueis/{id}/confirmar
     * Confirma um aluguel: RESERVADO → ATIVO, quarto passa a OCUPADO.
     */
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Aluguel> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmar(id));
    }

    /**
     * PATCH /alugueis/{id}/encerrar
     * Encerra um aluguel: ATIVO → ENCERRADO, quarto fica LIVRE.
     */
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<Aluguel> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(service.encerrar(id));
    }

    /**
     * PATCH /alugueis/{id}/cancelar
     * Cancela um aluguel (Sprint 3): libera o quarto e cancela o pagamento.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Aluguel> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
