package com.marau.hospedagem.model.fidelidade;

/**
 * Benefício: desconto progressivo sobre o valor da hospedagem.
 */
public class DescontoProgressivoDecorator extends BeneficioDecorator {

    private final double percentual;

    public DescontoProgressivoDecorator(CalculoBeneficios componente, double percentual) {
        super(componente);
        this.percentual = percentual;
    }

    @Override
    protected void aplicarBeneficio(BeneficiosConcedidos beneficios, ContextoFidelidade contexto) {
        beneficios.adicionarDesconto(percentual,
                String.format("Desconto de %.0f%% sobre a hospedagem", percentual * 100));
    }
}
