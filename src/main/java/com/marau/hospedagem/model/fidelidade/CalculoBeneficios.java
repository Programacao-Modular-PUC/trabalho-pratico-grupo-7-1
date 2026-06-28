package com.marau.hospedagem.model.fidelidade;

/**
 * Componente do padrão <b>Decorator</b>.
 *
 * <p>Define a operação de calcular os benefícios de fidelidade. Os benefícios
 * concretos são decoradores que envolvem outro {@code CalculoBeneficios},
 * permitindo empilhar quantos benefícios forem necessários sem criar uma
 * explosão de subclasses fixas para cada combinação.</p>
 */
public interface CalculoBeneficios {

    BeneficiosConcedidos calcular(ContextoFidelidade contexto);
}
