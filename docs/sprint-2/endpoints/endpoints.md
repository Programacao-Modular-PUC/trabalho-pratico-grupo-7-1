# Endpoints da API

## Residências

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /residencias | Lista todas as residências |
| GET | /residencias/{id} | Busca residência por id |
| POST | /residencias | Cadastra nova residência |
| PUT | /residencias/{id} | Atualiza residência |
| DELETE | /residencias/{id} | Remove residência |

---

## Quartos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /quartos?residenciaId={id} | Lista quartos de uma residência |
| GET | /quartos?residenciaId={id}&disponivel=true | Lista apenas quartos livres |
| GET | /quartos?residenciaId={id}&tipo=DUPLO | Filtra por tipo (INDIVIDUAL, DUPLO, FAMILIA) |
| GET | /quartos/{id} | Busca quarto por id |
| POST | /quartos | Cria novo quarto (Individual, Duplo ou Família) |
| DELETE | /quartos/{id} | Remove quarto |

---

## Clientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /clientes | Lista todos os clientes |
| GET | /clientes/{id} | Busca cliente por id |
| GET | /clientes/cpf/{cpf} | Busca cliente por CPF |
| POST | /clientes | Cadastra novo cliente |
| PUT | /clientes/{id} | Atualiza cliente |
| DELETE | /clientes/{id} | Remove cliente |

---

## Aluguéis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /alugueis | Lista todos os aluguéis |
| GET | /alugueis?residenciaId={id} | Filtra por residência |
| GET | /alugueis?clienteId={id} | Filtra por cliente |
| GET | /alugueis/{id} | Busca aluguel por id |
| GET | /alugueis/{id}/recibo | Retorna recibo formatado |
| GET | /alugueis/historico/cliente/{clienteId} | Histórico de aluguéis por cliente (Sprint 3) |
| POST | /alugueis | Cria novo aluguel |
| PATCH | /alugueis/{id}/confirmar | Confirma aluguel (RESERVADO → ATIVO) |
| PATCH | /alugueis/{id}/encerrar | Encerra aluguel (ATIVO → ENCERRADO) |
| PATCH | /alugueis/{id}/cancelar | Cancela aluguel (Sprint 3) |
