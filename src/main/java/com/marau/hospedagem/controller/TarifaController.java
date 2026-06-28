package com.marau.hospedagem.controller;

import com.marau.hospedagem.dto.CotacaoTarifaDTO;
import com.marau.hospedagem.dto.PromocaoDTO;
import com.marau.hospedagem.dto.RegraTarifaResumoDTO;
import com.marau.hospedagem.model.tarifa.ContextoTarifa;
import com.marau.hospedagem.model.tarifa.ResultadoTarifa;
import com.marau.hospedagem.model.tarifa.TarifaPromocional;
import com.marau.hospedagem.service.tarifa.GerenciadorTarifas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints da funcionalidade de Tarifação Flexível (Strategy + Singleton).
 */
@RestController
@RequestMapping("/tarifas")
public class TarifaController {

    private final GerenciadorTarifas gerenciadorTarifas;

    public TarifaController(GerenciadorTarifas gerenciadorTarifas) {
        this.gerenciadorTarifas = gerenciadorTarifas;
    }

    /** GET /tarifas — lista as regras de tarifa atualmente registradas. */
    @GetMapping
    public List<RegraTarifaResumoDTO> listar() {
        return gerenciadorTarifas.listarRegras().stream()
                .map(RegraTarifaResumoDTO::new)
                .toList();
    }

    /** POST /tarifas/cotacao — cota uma diária aplicando as regras vigentes. */
    @PostMapping("/cotacao")
    public ResultadoTarifa cotar(@RequestBody CotacaoTarifaDTO dto) {
        ContextoTarifa contexto = ContextoTarifa.builder()
                .dataReferencia(dto.getDataReferencia() != null ? dto.getDataReferencia() : LocalDate.now())
                .valorDiariaBase(dto.getValorDiariaBase())
                .quantidadeDiarias(dto.getQuantidadeDiarias())
                .totalHospedagensCliente(dto.getTotalHospedagensCliente())
                .tipoQuarto(dto.getTipoQuarto())
                .build();
        return gerenciadorTarifas.calcular(contexto);
    }

    /** POST /tarifas/promocao — registra uma nova promoção temporária em runtime. */
    @PostMapping("/promocao")
    public ResponseEntity<List<RegraTarifaResumoDTO>> registrarPromocao(@RequestBody PromocaoDTO dto) {
        gerenciadorTarifas.registrarRegra(
                new TarifaPromocional(dto.getNome(), dto.getInicio(), dto.getFim(), dto.getPercentualDesconto()));
        return ResponseEntity.status(201).body(listar());
    }

    /** DELETE /tarifas/{nome} — remove uma regra de tarifa pelo nome. */
    @DeleteMapping("/{nome}")
    public ResponseEntity<Void> remover(@PathVariable String nome) {
        boolean removido = gerenciadorTarifas.removerRegra(nome);
        return removido ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
