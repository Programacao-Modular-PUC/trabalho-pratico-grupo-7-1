package com.marau.hospedagem.model.fidelidade;

/**
 * Componente concreto base do Decorator: ponto de partida sem nenhum benefício.
 * É a "casca" interna sobre a qual os decoradores empilham os benefícios.
 */
public class SemBeneficios implements CalculoBeneficios {

    @Override
    public BeneficiosConcedidos calcular(ContextoFidelidade contexto) {
        return new BeneficiosConcedidos();
    }
}
