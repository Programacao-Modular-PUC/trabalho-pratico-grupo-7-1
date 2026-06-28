package com.marau.hospedagem.model.fidelidade;

/**
 * Benefício: concessão de diária(s) gratuita(s) na hospedagem atual.
 */
public class DiariaGratuitaDecorator extends BeneficioDecorator {

    private final int diarias;

    public DiariaGratuitaDecorator(CalculoBeneficios componente, int diarias) {
        super(componente);
        this.diarias = diarias;
    }

    @Override
    protected void aplicarBeneficio(BeneficiosConcedidos beneficios, ContextoFidelidade contexto) {
        // Concede no máximo o número de diárias da estadia atual.
        int concedidas = Math.min(diarias, contexto.getDiariasAtual());
        if (concedidas > 0) {
            beneficios.adicionarDiariasGratuitas(concedidas,
                    String.format("%d diária(s) gratuita(s)", concedidas));
        }
    }
}
