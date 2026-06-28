package com.marau.hospedagem.model.fidelidade;

/**
 * Decorator abstrato. Mantém a referência ao componente envolvido e delega a
 * ele o cálculo, permitindo que cada subclasse acrescente o seu benefício ao
 * resultado já produzido pelos decoradores internos.
 */
public abstract class BeneficioDecorator implements CalculoBeneficios {

    protected final CalculoBeneficios componente;

    protected BeneficioDecorator(CalculoBeneficios componente) {
        this.componente = componente;
    }

    @Override
    public BeneficiosConcedidos calcular(ContextoFidelidade contexto) {
        BeneficiosConcedidos beneficios = componente.calcular(contexto);
        aplicarBeneficio(beneficios, contexto);
        return beneficios;
    }

    /** Cada decorador concreto adiciona o seu benefício específico. */
    protected abstract void aplicarBeneficio(BeneficiosConcedidos beneficios, ContextoFidelidade contexto);
}
