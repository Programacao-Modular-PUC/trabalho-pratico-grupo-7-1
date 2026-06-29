# Diagrama de Classes — Sprint 4

Diagramas com foco nos **padrões de projeto** introduzidos nesta sprint e em como
as classes novas se relacionam com as já existentes (`AluguelService`,
`Aluguel`, `Cliente`, `AluguelRepository`).

> Versão renderizável também disponível em PlantUML:
> [`diagrama_sprint4.puml`](diagrama_sprint4.puml).
>
> Visão consolidada (todas as classes novas + núcleo existente em uma única
> imagem, útil para apresentação rápida): [`diagrama_visao_geral.png`](diagrama_visao_geral.png).

## 1. Tarifação Flexível — Strategy + Singleton

```mermaid
classDiagram
    class RegraTarifa {
        <<interface>> Strategy
        +getNome() String
        +getPrioridade() int
        +isAplicavel(ctx) boolean
        +aplicar(ctx, valor) double
    }
    class TarifaAltaTemporada
    class TarifaBaixaTemporada
    class TarifaFeriado
    class TarifaPromocional
    class TarifaClienteFrequente

    RegraTarifa <|.. TarifaAltaTemporada
    RegraTarifa <|.. TarifaBaixaTemporada
    RegraTarifa <|.. TarifaFeriado
    RegraTarifa <|.. TarifaPromocional
    RegraTarifa <|.. TarifaClienteFrequente

    class GerenciadorTarifas {
        <<Singleton>>
        -List~RegraTarifa~ regras
        -GerenciadorTarifas()
        +getInstance() GerenciadorTarifas$
        +registrarRegra(r)
        +removerRegra(nome) boolean
        +calcular(ctx) ResultadoTarifa
    }
    class ContextoTarifa
    class ResultadoTarifa
    class AluguelService

    GerenciadorTarifas o--> "*" RegraTarifa : regras
    GerenciadorTarifas ..> ContextoTarifa
    GerenciadorTarifas ..> ResultadoTarifa
    AluguelService ..> GerenciadorTarifas : getInstance()
    AluguelService ..> ContextoTarifa
```

## 2. Programa de Fidelidade — Decorator + Factory Method

```mermaid
classDiagram
    class CalculoBeneficios {
        <<interface>> Component
        +calcular(ctx) BeneficiosConcedidos
    }
    class SemBeneficios {
        <<ConcreteComponent>>
    }
    class BeneficioDecorator {
        <<abstract>> Decorator
        #CalculoBeneficios componente
        +calcular(ctx) BeneficiosConcedidos
        #aplicarBeneficio(b, ctx)*
    }
    class DescontoProgressivoDecorator
    class UpgradeQuartoDecorator
    class CheckoutEstendidoDecorator
    class DiariaGratuitaDecorator

    CalculoBeneficios <|.. SemBeneficios
    CalculoBeneficios <|.. BeneficioDecorator
    BeneficioDecorator o--> CalculoBeneficios : componente
    BeneficioDecorator <|-- DescontoProgressivoDecorator
    BeneficioDecorator <|-- UpgradeQuartoDecorator
    BeneficioDecorator <|-- CheckoutEstendidoDecorator
    BeneficioDecorator <|-- DiariaGratuitaDecorator

    class CategoriaFidelidade {
        <<enumeration>>
        BRONZE
        PRATA
        OURO
        DIAMANTE
        +porHospedagens(n) CategoriaFidelidade$
    }
    class FidelidadeFactory {
        <<Factory Method>>
        +criarBeneficios(cat) CalculoBeneficios$
    }
    class FidelidadeService
    class AluguelRepository

    FidelidadeFactory ..> CategoriaFidelidade
    FidelidadeFactory ..> CalculoBeneficios : cria
    FidelidadeService ..> FidelidadeFactory
    FidelidadeService ..> CategoriaFidelidade
    FidelidadeService --> AluguelRepository : histórico
```
