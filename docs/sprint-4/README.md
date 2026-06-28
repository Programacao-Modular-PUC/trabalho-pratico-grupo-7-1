# Sprint 4 — Evolução Arquitetural e Padrões de Projeto

Documento técnico da Sprint 4. Foram escolhidas **duas** das seis opções e
aplicado o **Singleton** obrigatório, além dos padrões específicos de cada
funcionalidade.

| Item da entrega | Onde está |
|-----------------|-----------|
| Código-fonte completo | `src/main/java/com/marau/hospedagem/{model,service,controller,dto,config}` |
| Diagrama UML atualizado | [`diagrama-classes/`](diagrama-classes/) ([Mermaid](diagrama-classes/diagrama_sprint4.md) · [PlantUML](diagrama-classes/diagrama_sprint4.puml)) |
| Documento técnico | este README |
| Testes | 23 novos testes (total do projeto: **67, 0 falhas**) |

---

## 1. Funcionalidades escolhidas

1. **Opção 1 — Sistema de Tarifação Flexível**
2. **Opção 2 — Programa de Fidelidade**

Resumo dos padrões:

| Funcionalidade | Padrão principal | Padrão de apoio |
|----------------|------------------|-----------------|
| Tarifação Flexível | **Strategy** | **Singleton** (`GerenciadorTarifas`) |
| Programa de Fidelidade | **Decorator** | **Factory Method** |

> O **Singleton** é usado como recurso global, **mas não** como único padrão da
> sprint — ele orquestra as estratégias de tarifa, conforme exige o enunciado.

---

## 2. Funcionalidade 1 — Tarifação Flexível

### 2.1. Problema de software
O cálculo da diária estava **fixo** dentro de `Aluguel`
(`valorFinal = valorDiaria × quantidadeDiarias`). Qualquer nova situação de
preço (alta/baixa temporada, feriado, promoção, desconto para cliente
frequente) exigiria alterar a classe `Aluguel` com uma cadeia de `if/else`,
violando o princípio **Aberto/Fechado** e tornando o cálculo difícil de testar
e de manter.

### 2.2. Solução — Strategy + Singleton
- Cada regra de preço virou uma **estratégia** independente que implementa
  [`RegraTarifa`](../../src/main/java/com/marau/hospedagem/model/tarifa/RegraTarifa.java):
  `TarifaAltaTemporada`, `TarifaBaixaTemporada`, `TarifaFeriado`,
  `TarifaPromocional`, `TarifaClienteFrequente`.
- O [`GerenciadorTarifas`](../../src/main/java/com/marau/hospedagem/service/tarifa/GerenciadorTarifas.java)
  (**Singleton**) é o catálogo global dessas regras. Ele recebe um
  `ContextoTarifa` (data, diária-base, nº de diárias, histórico do cliente),
  seleciona as regras **aplicáveis**, ordena por **prioridade** e as aplica em
  cadeia, produzindo um `ResultadoTarifa` auditável (lista das regras que
  incidiram + valores).
- Cada `RegraTarifa` decide **se** incide (`isAplicavel`) e **como** ajusta o
  valor (`aplicar`). Adicionar uma nova forma de tarifação = **criar uma nova
  classe e registrá-la** — zero alteração nas regras existentes.

```
diária base ──> [FERIADO +20%] ──> [ALTA_TEMPORADA +30%] ──> [PROMO -25%] ──> [CLIENTE_FREQUENTE -10%] ──> diária final
                 (prio. 5)          (prio. 10)               (prio. 20)        (prio. 30)
```

### 2.3. Justificativa
- **Strategy**: o requisito literal é "permitir a criação de novas regras de
  cálculo sem exigir alterações significativas no código existente" — a
  definição de manual do Strategy. Isola cada algoritmo de preço, deixando-os
  testáveis individualmente.
- **Prioridade + lista de estratégias** permite **combinar** várias situações
  (ex.: feriado em alta temporada para cliente frequente) sem criar uma classe
  para cada combinação.

### 2.4. Integração com o sistema
`AluguelService.criar(...)` monta o `ContextoTarifa` a partir do aluguel e do
histórico do cliente e chama `GerenciadorTarifas.getInstance().calcularValorDiaria(...)`.
O valor tarifado passa a alimentar `Pagamento` e `Recibo` automaticamente.

---

## 3. Funcionalidade 2 — Programa de Fidelidade

### 3.1. Problema de software
Clientes frequentes devem receber benefícios (descontos progressivos, upgrade,
check-out estendido, diárias gratuitas) conforme o histórico. Modelar isso com
uma classe por **combinação** de benefícios geraria uma explosão de subclasses
(Bronze, Prata, Ouro… × cada conjunto de benefícios), e adicionar um benefício
novo obrigaria a tocar em todas elas.

### 3.2. Solução — Decorator + Factory Method
- Cada benefício é um **decorador** que envolve um
  [`CalculoBeneficios`](../../src/main/java/com/marau/hospedagem/model/fidelidade/CalculoBeneficios.java)
  e **acrescenta** o seu efeito ao resultado:
  `DescontoProgressivoDecorator`, `UpgradeQuartoDecorator`,
  `CheckoutEstendidoDecorator`, `DiariaGratuitaDecorator`. Eles são empilhados
  livremente sobre o componente base `SemBeneficios`.
- A [`FidelidadeFactory`](../../src/main/java/com/marau/hospedagem/service/fidelidade/FidelidadeFactory.java)
  (**Factory Method**) decide **qual pilha de decoradores** cada
  `CategoriaFidelidade` (BRONZE/PRATA/OURO/DIAMANTE) recebe e devolve o objeto
  pronto — o cliente do código não conhece os decoradores concretos.
- A categoria é derivada do histórico em
  `CategoriaFidelidade.porHospedagens(n)`; o `FidelidadeService` conta as
  hospedagens **não canceladas** do cliente no `AluguelRepository`.

Benefícios cumulativos por categoria:

| Categoria | Limiar (hospedagens) | Benefícios |
|-----------|----------------------|------------|
| BRONZE | 0 | — |
| PRATA | 5 | 5% de desconto |
| OURO | 10 | 10% de desconto + check-out +2h |
| DIAMANTE | 20 | 15% de desconto + check-out +4h + upgrade + 1 diária grátis |

### 3.3. Justificativa
- **Decorator**: o requisito é "permitir a criação de novos benefícios e
  categorias de forma simples e extensível". O Decorator adiciona
  responsabilidades dinamicamente e **evita a explosão de combinações fixas** —
  exatamente o problema descrito. Um benefício novo = um novo decorador.
- **Factory Method**: encapsula a regra "categoria → benefícios", concentrando
  num único ponto a montagem da cadeia e mantendo o serviço/controller
  desacoplados das classes concretas.

---

## 4. Requisito obrigatório — Singleton

**Componente:** `GerenciadorTarifas` (gerenciador/catálogo global de tarifas).

**Por que uma única instância?** As regras de tarifa são uma **política de
preços única e global** da hospedagem. Se cada reserva, serviço ou tela
mantivesse sua própria tabela de regras, o sistema cobraria **preços
inconsistentes** para o mesmo período. Uma instância única garante que:

- toda a aplicação (criação de aluguel, cotação, futuros relatórios) enxergue
  **o mesmo** conjunto de regras vigentes;
- cadastrar/remover uma regra (ex.: ligar uma promoção de Black Friday) **reflita
  imediatamente** em todo o sistema, em tempo de execução;
- exista um ponto único de acesso global, inclusive para código que não é
  gerenciado pelo Spring.

**Implementação:** idioma *initialization-on-demand holder* (`getInstance()`),
que é **thread-safe** e faz inicialização tardia sem custo de sincronização. A
lista interna usa `CopyOnWriteArrayList` (leitura/escrita concorrentes seguras).
A classe [`TarifaConfig`](../../src/main/java/com/marau/hospedagem/config/TarifaConfig.java)
expõe `getInstance()` como `@Bean`, permitindo injeção nos controllers **sem
criar uma segunda instância** — continua sendo um Singleton GoF legítimo.

> O Singleton **não** resolve sozinho a sprint: ele é o *contexto* que armazena e
> orquestra as estratégias (Strategy). Os demais requisitos usam Decorator e
> Factory Method.

---

## 5. Demonstração

### 5.1. Tarifação — endpoints
```
GET    /tarifas                 # lista as regras vigentes
POST   /tarifas/cotacao         # cota uma diária aplicando as regras
POST   /tarifas/promocao        # cadastra uma promoção em runtime
DELETE /tarifas/{nome}          # remove uma regra pelo nome
```

Exemplo de cotação (alta temporada + cliente frequente):
```http
POST /tarifas/cotacao
{ "dataReferencia": "2025-01-10", "valorDiariaBase": 100.0,
  "quantidadeDiarias": 2, "totalHospedagensCliente": 5 }
```
```json
{ "valorDiariaBase": 100.0, "valorDiariaFinal": 117.0, "quantidadeDiarias": 2,
  "regrasAplicadas": ["ALTA_TEMPORADA", "CLIENTE_FREQUENTE"],
  "valorTotal": 234.0, "valorTotalBase": 200.0 }
```
> `100 × 1,30 (alta) = 130`; `130 × 0,90 (frequente) = 117` por diária.

### 5.2. Fidelidade — endpoints
```
GET /fidelidade/cliente/{id}                          # categoria + benefícios
GET /fidelidade/cliente/{id}/simular?valor=&diarias=  # valor final com desconto
```
Exemplo (cliente DIAMANTE, hospedagem de R$300 em 3 diárias):
```json
{ "categoria": "DIAMANTE", "totalHospedagens": 22, "percentualDesconto": 0.15,
  "upgradeQuarto": true, "horasCheckoutEstendido": 4, "diariasGratuitas": 1,
  "beneficios": ["Desconto de 15% sobre a hospedagem", "Check-out estendido em 4 hora(s)",
                 "Upgrade de quarto sujeito à disponibilidade", "1 diária(s) gratuita(s)"],
  "valorOriginal": 300.0, "valorComDesconto": 170.0 }
```
> 1 diária grátis (R$100) ⇒ R$200; 15% off ⇒ **R$170**.

### 5.3. Testes automatizados (demonstração de funcionamento)
- `TarifaStrategyTest` (6) — cada estratégia isolada.
- `GerenciadorTarifasTest` (4) — Singleton, combinação por prioridade, cadastro/remoção em runtime.
- `FidelidadeTest` (7) — categorias e empilhamento de decoradores.
- `FidelidadeServiceTest` (6) — histórico (Mockito) → categoria → benefícios e valores.

```bash
./mvnw test        # 67 testes, 0 falhas
```

---

## 6. Benefícios obtidos com a nova arquitetura

- **Extensibilidade (Aberto/Fechado):** nova regra de tarifa ou novo benefício
  de fidelidade = **nova classe**, sem alterar o que já funciona.
- **Coesão e testabilidade:** cada algoritmo isolado em sua própria classe, com
  teste unitário próprio.
- **Eliminação de `if/else` e de combinações fixas:** Strategy substitui a
  cadeia de condicionais do preço; Decorator evita a explosão de subclasses de
  pacotes de benefícios.
- **Consistência global:** o Singleton garante uma política de preços única em
  todo o sistema, configurável em tempo de execução.
- **Baixo acoplamento:** Factory Method e a injeção do Singleton via bean
  mantêm controllers/serviços desacoplados das classes concretas.
- **Compatibilidade:** os 44 testes das sprints anteriores continuam passando —
  a evolução foi aditiva.

---

## 7. Mapa das classes novas

```
model/tarifa/      RegraTarifa, TarifaAltaTemporada, TarifaBaixaTemporada,
                   TarifaFeriado, TarifaPromocional, TarifaClienteFrequente,
                   ContextoTarifa, ResultadoTarifa
service/tarifa/    GerenciadorTarifas  (Singleton)
config/            TarifaConfig
controller/        TarifaController

model/fidelidade/  CalculoBeneficios, SemBeneficios, BeneficioDecorator,
                   DescontoProgressivoDecorator, UpgradeQuartoDecorator,
                   CheckoutEstendidoDecorator, DiariaGratuitaDecorator,
                   CategoriaFidelidade, ContextoFidelidade, BeneficiosConcedidos
service/fidelidade/ FidelidadeFactory (Factory Method), FidelidadeService
controller/        FidelidadeController
dto/               CotacaoTarifaDTO, PromocaoDTO, RegraTarifaResumoDTO,
                   ResultadoFidelidadeDTO
```
