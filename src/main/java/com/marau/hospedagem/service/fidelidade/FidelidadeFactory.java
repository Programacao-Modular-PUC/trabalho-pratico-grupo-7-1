package com.marau.hospedagem.service.fidelidade;

import com.marau.hospedagem.model.fidelidade.CalculoBeneficios;
import com.marau.hospedagem.model.fidelidade.CategoriaFidelidade;
import com.marau.hospedagem.model.fidelidade.CheckoutEstendidoDecorator;
import com.marau.hospedagem.model.fidelidade.DescontoProgressivoDecorator;
import com.marau.hospedagem.model.fidelidade.DiariaGratuitaDecorator;
import com.marau.hospedagem.model.fidelidade.SemBeneficios;
import com.marau.hospedagem.model.fidelidade.UpgradeQuartoDecorator;

/**
 * Padrão <b>Factory Method</b>.
 *
 * <p>Encapsula a regra de "qual conjunto de benefícios uma categoria recebe",
 * montando a cadeia de decoradores apropriada. O cliente (serviço/controller)
 * não conhece os decoradores concretos nem a ordem em que são empilhados —
 * apenas pede a fábrica o objeto pronto.</p>
 *
 * <p>Benefícios cumulativos por categoria (cada nível herda os anteriores):</p>
 * <ul>
 *   <li><b>BRONZE</b>: sem benefícios.</li>
 *   <li><b>PRATA</b>: 5% de desconto.</li>
 *   <li><b>OURO</b>: 10% de desconto + check-out estendido (2h).</li>
 *   <li><b>DIAMANTE</b>: 15% de desconto + check-out estendido (4h)
 *       + upgrade de quarto + 1 diária gratuita.</li>
 * </ul>
 */
public final class FidelidadeFactory {

    private FidelidadeFactory() {
    }

    /** Factory Method: cria o objeto de cálculo de benefícios para a categoria. */
    public static CalculoBeneficios criarBeneficios(CategoriaFidelidade categoria) {
        CalculoBeneficios calculo = new SemBeneficios();

        switch (categoria) {
            case BRONZE:
                // Sem benefícios adicionais.
                break;
            case PRATA:
                calculo = new DescontoProgressivoDecorator(calculo, 0.05);
                break;
            case OURO:
                calculo = new DescontoProgressivoDecorator(calculo, 0.10);
                calculo = new CheckoutEstendidoDecorator(calculo, 2);
                break;
            case DIAMANTE:
                calculo = new DescontoProgressivoDecorator(calculo, 0.15);
                calculo = new CheckoutEstendidoDecorator(calculo, 4);
                calculo = new UpgradeQuartoDecorator(calculo);
                calculo = new DiariaGratuitaDecorator(calculo, 1);
                break;
        }
        return calculo;
    }
}
