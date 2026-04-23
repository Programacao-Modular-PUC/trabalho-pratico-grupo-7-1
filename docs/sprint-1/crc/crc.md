# Cartões CRC – Sistema de Hospedagem

---

## Cliente

| Responsabilidades | Colaborações |
|------------------|--------------|
| 1. Armazenar dados pessoais (nome, CPF, contato) <br> 2. Realizar reservas <br> 3. Consultar histórico de hospedagens | Reserva <br> Aluguel |

---

## Residência

| Responsabilidades | Colaborações |
|------------------|--------------|
| 1. Armazenar dados da residência <br> 2. Gerenciar lista de quartos <br> 3. Manter histórico de hospedagens | Quarto <br> Aluguel |

---

## Quarto

| Responsabilidades | Colaborações |
|------------------|--------------|
| 1. Armazenar características do quarto <br> 2. Informar disponibilidade <br> 3. Calcular valor da diária | Residência <br> Reserva <br> Aluguel |

---

## Reserva

| Responsabilidades | Colaborações |
|------------------|--------------|
| 1. Registrar reservas futuras <br> 2. Verificar disponibilidade do quarto <br> 3. Armazenar datas de entrada e saída | Cliente <br> Quarto |

---

## Aluguel

| Responsabilidades | Colaborações |
|------------------|--------------|
| 1. Registrar hospedagem realizada <br> 2. Calcular quantidade de diárias <br> 3. Calcular valor total <br> 4. Gerar recibo | Cliente <br> Quarto <br> Pagamento |

---

## Pagamento

| Responsabilidades | Colaborações |
|------------------|--------------|
| 1. Registrar pagamento do aluguel <br> 2. Armazenar valor pago <br> 3. Confirmar pagamento | Aluguel |
