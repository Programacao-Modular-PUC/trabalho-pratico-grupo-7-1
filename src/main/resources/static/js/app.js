/* ============================================================
   Marau · Hospedagem — front-end das Opções 1 e 2 (Sprint 4)
   Consome a API REST do Spring Boot (mesma origem).
   ============================================================ */
"use strict";

const API = ""; // mesma origem do Spring Boot

/* ---------- helpers ---------- */
const $  = (sel, ctx = document) => ctx.querySelector(sel);
const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

const brl = (v) =>
  (v ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

const pct = (v) => `${Math.round((v ?? 0) * 100)}%`;

function toast(msg, kind = "") {
  const el = $("#toast");
  el.textContent = msg;
  el.className = `toast show ${kind}`;
  clearTimeout(toast._t);
  toast._t = setTimeout(() => (el.className = "toast"), 3200);
}

async function api(path, options = {}) {
  const res = await fetch(API + path, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    let detail = `HTTP ${res.status}`;
    try {
      const body = await res.json();
      detail = body.message || body.error || detail;
    } catch (_) { /* sem corpo JSON */ }
    throw new Error(detail);
  }
  if (res.status === 204) return null;
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

/* ---------- navegação entre views ---------- */
$$(".nav-item").forEach((btn) => {
  btn.addEventListener("click", () => {
    $$(".nav-item").forEach((b) => b.classList.toggle("is-active", b === btn));
    const view = btn.dataset.view;
    $$(".view").forEach((v) => v.classList.toggle("is-active", v.id === `view-${view}`));
  });
});

/* ---------- status da API ---------- */
async function checkApi() {
  const dot = $("#apiStatus");
  const txt = $("#apiStatusText");
  try {
    await api("/tarifas");
    dot.className = "pulse ok";
    txt.textContent = "API conectada";
  } catch (_) {
    dot.className = "pulse off";
    txt.textContent = "API offline";
  }
}

/* ============================================================
   OPÇÃO 1 — TARIFAÇÃO FLEXÍVEL
   ============================================================ */

function nomeAmigavel(nome) {
  return String(nome)
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

/* Cotação */
$("#cotacaoForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const f = e.target;
  const payload = {
    dataReferencia: f.dataReferencia.value || null,
    valorDiariaBase: parseFloat(f.valorDiariaBase.value),
    quantidadeDiarias: parseInt(f.quantidadeDiarias.value, 10),
    totalHospedagensCliente: parseInt(f.totalHospedagensCliente.value || "0", 10),
    tipoQuarto: f.tipoQuarto.value.trim(),
  };
  try {
    const r = await api("/tarifas/cotacao", { method: "POST", body: JSON.stringify(payload) });
    renderCotacao(r);
  } catch (err) {
    toast("Erro ao cotar: " + err.message, "err");
  }
});

function renderCotacao(r) {
  const box = $("#cotacaoResult");
  box.hidden = false;

  $("#rDiariaFinal").textContent = brl(r.valorDiariaFinal);

  const mudou = Math.abs(r.valorDiariaFinal - r.valorDiariaBase) > 0.001;
  $("#rDiariaBase").innerHTML = mudou
    ? `base <s>${brl(r.valorDiariaBase)}</s>`
    : `diária base sem ajustes`;

  const diff = r.valorDiariaFinal - r.valorDiariaBase;
  const delta = $("#rDelta");
  if (mudou) {
    const sobe = diff > 0;
    delta.className = "result-delta" + (sobe ? "" : " down");
    delta.textContent = `${sobe ? "+" : "−"}${brl(Math.abs(diff))} / diária`;
    delta.hidden = false;
  } else {
    delta.hidden = true;
  }

  const regras = $("#rRegras");
  if (r.regrasAplicadas && r.regrasAplicadas.length) {
    regras.innerHTML = r.regrasAplicadas
      .map((n) => `<span class="tag">${nomeAmigavel(n)}</span>`)
      .join("");
  } else {
    regras.innerHTML = `<span class="tag none">nenhuma regra incidiu</span>`;
  }

  $("#rTotalBase").textContent = brl(r.valorTotalBase);
  $("#rTotal").textContent = brl(r.valorTotal);
}

/* Lista de regras vigentes */
async function carregarRegras() {
  const ul = $("#regrasList");
  try {
    const regras = await api("/tarifas");
    if (!regras.length) {
      ul.innerHTML = `<li class="muted">nenhuma regra cadastrada</li>`;
      return;
    }
    regras.sort((a, b) => a.prioridade - b.prioridade);
    ul.innerHTML = regras
      .map(
        (r) => `
      <li class="rule">
        <div class="rule-info">
          <span class="rule-name">${nomeAmigavel(r.nome)}</span>
          <span class="rule-prio">prioridade ${r.prioridade}</span>
        </div>
        <button class="rule-del" title="Remover" data-nome="${r.nome}">✕</button>
      </li>`
      )
      .join("");

    $$(".rule-del", ul).forEach((btn) =>
      btn.addEventListener("click", () => removerRegra(btn.dataset.nome))
    );
  } catch (err) {
    ul.innerHTML = `<li class="muted">erro ao carregar (${err.message})</li>`;
  }
}

async function removerRegra(nome) {
  try {
    await api("/tarifas/" + encodeURIComponent(nome), { method: "DELETE" });
    toast(`Regra "${nomeAmigavel(nome)}" removida`, "ok");
    carregarRegras();
  } catch (err) {
    toast("Erro ao remover: " + err.message, "err");
  }
}

$("#reloadRegras").addEventListener("click", carregarRegras);

/* Nova promoção */
$("#promoForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const f = e.target;
  const payload = {
    nome: f.nome.value.trim(),
    inicio: f.inicio.value,
    fim: f.fim.value,
    percentualDesconto: parseFloat(f.percentualDesconto.value) / 100,
  };
  if (payload.fim < payload.inicio) {
    toast("A data fim deve ser posterior ao início", "err");
    return;
  }
  try {
    await api("/tarifas/promocao", { method: "POST", body: JSON.stringify(payload) });
    toast(`Promoção "${payload.nome}" cadastrada`, "ok");
    f.reset();
    carregarRegras();
  } catch (err) {
    toast("Erro ao cadastrar: " + err.message, "err");
  }
});

/* ============================================================
   OPÇÃO 2 — PROGRAMA DE FIDELIDADE
   ============================================================ */

async function carregarClientes() {
  const sel = $("#clienteSelect");
  try {
    const clientes = await api("/clientes");
    sel.length = 1;
    clientes.forEach((c) => {
      const opt = document.createElement("option");
      opt.value = c.id;
      opt.textContent = `#${c.id} · ${c.nome}`;
      sel.appendChild(opt);
    });
  } catch (_) { /* lista vazia ou API offline */ }
}

$("#clienteSelect").addEventListener("change", (e) => {
  if (e.target.value) $("#clienteId").value = e.target.value;
});

$("#fidForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const f = e.target;
  const id = parseInt(f.clienteId.value, 10);
  const valor = parseFloat(f.valor.value || "0");
  const diarias = parseInt(f.diarias.value || "1", 10);

  try {
    let dados;
    if (valor > 0) {
      dados = await api(`/fidelidade/cliente/${id}/simular?valor=${valor}&diarias=${diarias}`);
    } else {
      dados = await api(`/fidelidade/cliente/${id}`);
    }
    renderFidelidade(dados);
  } catch (err) {
    $("#fidResultCard").hidden = true;
    toast("Cliente não encontrado ou erro: " + err.message, "err");
  }
});

const CAT_CLASS = { BRONZE: "bronze", PRATA: "prata", OURO: "ouro", DIAMANTE: "diamante" };

function renderFidelidade(d) {
  const card = $("#fidResultCard");
  card.hidden = false;

  $("#fNome").textContent = d.nomeCliente || `Cliente #${d.clienteId}`;
  $("#fHosp").textContent = `${d.totalHospedagens} hospedagem(ns) no histórico`;

  const badge = $("#fCategoria");
  badge.textContent = d.categoria;
  badge.className = "badge " + (CAT_CLASS[d.categoria] || "");

  const grid = $("#fBeneficios");
  if (d.beneficios && d.beneficios.length) {
    grid.innerHTML = d.beneficios
      .map((b) => `<div class="benef"><span class="ic">✦</span><p>${b}</p></div>`)
      .join("");
  } else {
    grid.innerHTML = `<div class="benef empty">Sem benefícios — cliente ainda na categoria base.</div>`;
  }

  const sim = $("#fSim");
  if (d.valorComDesconto != null && d.valorOriginal != null) {
    sim.hidden = false;
    $("#fOriginal").textContent = brl(d.valorOriginal);
    $("#fComDesconto").textContent = brl(d.valorComDesconto);
    const eco = d.valorOriginal - d.valorComDesconto;
    $("#fEconomia").textContent = eco > 0 ? `economia ${brl(eco)}` : "sem desconto";
  } else {
    sim.hidden = true;
  }
}

/* Criar cliente de teste */
$("#quickClienteForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const f = e.target;
  const payload = { nome: f.nome.value.trim(), cpf: f.cpf.value.trim() };
  try {
    const novo = await api("/clientes", { method: "POST", body: JSON.stringify(payload) });
    toast(`Cliente "${novo.nome}" criado (#${novo.id})`, "ok");
    f.reset();
    await carregarClientes();
    $("#clienteId").value = novo.id;
    $("#clienteSelect").value = String(novo.id);
  } catch (err) {
    toast("Erro ao criar cliente: " + err.message, "err");
  }
});

/* ---------- boot ---------- */
(function init() {
  const hoje = new Date().toISOString().slice(0, 10);
  const dr = document.querySelector('#cotacaoForm [name="dataReferencia"]');
  if (dr) dr.value = hoje;

  checkApi();
  carregarRegras();
  carregarClientes();
})();
