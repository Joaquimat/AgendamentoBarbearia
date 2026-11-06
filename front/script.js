// ===================== CONFIGURAÇÃO =====================
// Endereço do backend (Spring Boot). Todas as rotas começam com /brb
const API = "http://localhost:8080/brb";

// Token JWT guardado no navegador para continuar logado ao recarregar a página
let token = localStorage.getItem("token");

let servicos = [];          // serviços vindos do backend
let meusAgendamentos = [];  // agendamentos do usuário logado
let admin = false;          // true se o usuário logado for administrador
let modoCadastro = false;   // false = tela de login | true = tela de cadastro
let timerMensagem = null;

function el(id) {
  return document.getElementById(id);
}

// Evita que textos digitados quebrem o HTML
function esc(texto) {
  const div = document.createElement("div");
  div.textContent = texto;
  return div.innerHTML;
}

function mostrarMensagem(texto, tipo) {
  const m = el("mensagem");
  m.textContent = texto;
  m.className = "mensagem " + tipo; // "erro" ou "ok"
  clearTimeout(timerMensagem);
  timerMensagem = setTimeout(() => m.classList.add("escondido"), 4000);
}

function formatarDataHora(iso) {
  return new Date(iso).toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}

function formatarValor(v) {
  return Number(v).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

// Desabilita o botão enquanto a ação roda (evita cliques duplos)
async function comBotao(botao, funcao) {
  botao.disabled = true;
  try {
    await funcao();
  } finally {
    botao.disabled = false;
  }
}

// ===================== DADOS DO TOKEN (JWT) =====================
// O token tem 3 partes separadas por ponto. A do meio guarda os dados do usuário
// (papel/role, data de expiração...) codificados em Base64.
function lerToken() {
  try {
    const parte = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    const json = decodeURIComponent(
      atob(parte).split("").map(c => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)).join("")
    );
    return JSON.parse(json);
  } catch (e) {
    return null;
  }
}

// Verifica se o usuário é admin procurando "ADMIN" nos campos de papel do token
// (funciona com "ADMIN", "ROLE_ADMIN", listas de roles, authorities etc.)
function ehAdmin() {
  const dados = lerToken();
  console.log("Dados do token:", dados); // ajuda a conferir o nome do campo da role
  if (!dados) return false;
  // ignora campos que guardam nome/e-mail (um e-mail pode conter "admin")
  const ignorar = ["sub", "email", "nome", "name", "username", "login", "iss", "iat", "exp", "jti"];
  for (const campo in dados) {
    if (ignorar.includes(campo)) continue;
    if (JSON.stringify(dados[campo]).toUpperCase().includes("ADMIN")) {
      return true;
    }
  }
  return false;
}

// Se o token não diz que é admin, quem decide é o servidor:
// a lista geral de agendamentos (GET /agendamentos) é rota de admin.
// Se o servidor liberar, o usuário é admin; se negar, é usuário comum.
async function descobrirAdmin() {
  if (ehAdmin()) return true;
  try {
    await requisicao("/agendamentos", "GET");
    console.log("Teste de administrador: o servidor liberou a lista geral -> ADMIN");
    return true;
  } catch (erro) {
    console.log("Teste de administrador: o servidor negou (" + erro.status + ") -> usuário comum");
    return false;
  }
}

function tokenExpirado() {
  const dados = lerToken();
  return !!(dados && dados.exp && dados.exp * 1000 < Date.now());
}

// ===================== COMUNICAÇÃO COM A API =====================
// Função única que faz todas as chamadas ao backend.
// - envia o token no cabeçalho Authorization (exceto login/cadastro)
// - lança um erro com a mensagem certa quando algo dá errado
async function requisicao(caminho, metodo, corpo, publica) {
  const cabecalhos = { "Content-Type": "application/json" };
  if (token && !publica) {
    cabecalhos["Authorization"] = "Bearer " + token;
  }

  let resposta;
  try {
    resposta = await fetch(API + caminho, {
      method: metodo || "GET",
      headers: cabecalhos,
      body: corpo ? JSON.stringify(corpo) : null
    });
  } catch (e) {
    const erro = new Error("sem conexão");
    erro.conexao = true;
    throw erro;
  }

  if (!resposta.ok) {
    const texto = await resposta.text();
    let mensagem = texto;
    try {
      const json = JSON.parse(texto);
      mensagem = json.message || json.error || texto;
    } catch (e) { /* resposta não era JSON */ }

    const erro = new Error(mensagem || "Erro " + resposta.status);
    erro.status = resposta.status;
    throw erro;
  }

  // Respostas 201 e 204 não têm corpo
  const texto = await resposta.text();
  try {
    return texto ? JSON.parse(texto) : null;
  } catch (e) {
    return null; // sucesso com resposta em texto simples
  }
}

// Mostra ao usuário SOMENTE erros que ele entende e pode resolver.
// Detalhes técnicos (códigos, mensagens do servidor) vão só para o console (F12).
//  - mensagemContexto: texto amigável para erros causados pelos dados enviados
//  - mostrarEm5xx: usar o mesmo texto quando o servidor falhar por regra de negócio
function tratarErro(erro, mensagemContexto, mostrarEm5xx) {
  console.error("Erro técnico:", erro.status, erro.message);

  if (erro.conexao) {
    mostrarMensagem("Sem conexão com o servidor. Verifique sua internet e tente novamente.", "erro");
    return;
  }
  if (erro.status === 401) {
    sair();
    mostrarMensagem("Sua sessão expirou. Entre novamente.", "erro");
    return;
  }
  if (admin && erro.status === 403) {
    mostrarMensagem("O servidor não autorizou esta ação para o seu usuário.", "erro");
    return;
  }
  if (!mensagemContexto || erro.status === 403) {
    return; // o usuário não tem o que fazer: nada aparece na tela
  }
  if (erro.status >= 500 && !mostrarEm5xx) {
    return;
  }
  mostrarMensagem(mensagemContexto, "erro");
}

// ===================== LOGIN / CADASTRO =====================
el("link-trocar").onclick = function (e) {
  e.preventDefault();
  modoCadastro = !modoCadastro;
  el("titulo-login").textContent = modoCadastro ? "Criar conta" : "Entrar";
  el("btn-login").textContent = modoCadastro ? "Cadastrar" : "Entrar";
  el("campo-nome").classList.toggle("escondido", !modoCadastro);
  el("texto-trocar").textContent = modoCadastro ? "Já tem conta?" : "Ainda não tem conta?";
  el("link-trocar").textContent = modoCadastro ? "Entrar" : "Cadastre-se";
};

async function entrar() {
  const nome = el("nome").value.trim();
  const email = el("email").value.trim();
  const senha = el("senha").value;

  if (!email || !senha || (modoCadastro && !nome)) {
    mostrarMensagem("Preencha todos os campos.", "erro");
    return;
  }

  try {
    if (modoCadastro) {
      // 1) cria a conta
      await requisicao("/auth/register", "POST", { nome, email, senha }, true);
    }
    // 2) faz login (também logo após cadastrar) e guarda o token
    const resultado = await requisicao("/auth/login", "POST", { email, senha }, true);
    token = resultado.token;
    localStorage.setItem("token", token);
    el("senha").value = "";
    await abrirApp();
  } catch (erro) {
    if (modoCadastro) {
      tratarErro(erro, "Não foi possível criar a conta. Confira os dados ou use outro e-mail.", true);
    } else if (erro.conexao) {
      tratarErro(erro);
    } else if (erro.status === 400 || erro.status === 401 || erro.status === 403) {
      console.error("Erro técnico:", erro.status, erro.message);
      mostrarMensagem("E-mail ou senha incorretos.", "erro");
    } else {
      console.error("Erro técnico:", erro.status, erro.message);
    }
  }
}

el("btn-login").onclick = e => comBotao(e.currentTarget, entrar);
["nome", "email", "senha"].forEach(function (id) {
  el(id).addEventListener("keydown", function (e) {
    if (e.key === "Enter") el("btn-login").click();
  });
});

el("btn-sair").onclick = sair;

function sair() {
  token = null;
  admin = false;
  localStorage.removeItem("token");
  el("tela-app").classList.add("escondido");
  el("tela-login").classList.remove("escondido");
  el("btn-sair").classList.add("escondido");
  el("selo-admin").classList.add("escondido");
  el("caixa-servicos").classList.add("escondido");
  el("caixa-todos").classList.add("escondido");
}

// ===================== TELA PRINCIPAL =====================
async function abrirApp() {
  if (tokenExpirado()) {
    sair();
    mostrarMensagem("Sua sessão expirou. Entre novamente.", "erro");
    return;
  }

  admin = await descobrirAdmin();

  el("tela-login").classList.add("escondido");
  el("tela-app").classList.remove("escondido");
  el("btn-sair").classList.remove("escondido");

  // Opções de administrador só aparecem para quem tem a role ADMIN
  el("selo-admin").classList.toggle("escondido", !admin);
  el("caixa-servicos").classList.toggle("escondido", !admin);
  el("caixa-todos").classList.toggle("escondido", !admin);

  // Não permite escolher data/hora no passado
  const agora = new Date();
  agora.setMinutes(agora.getMinutes() - agora.getTimezoneOffset());
  el("data-inicio").min = agora.toISOString().slice(0, 16);

  try {
    // Serviços primeiro, porque as listas de agendamentos usam os nomes deles
    await carregarServicos();
    await carregarAgendamentos();
    if (admin) await carregarTodos();
  } catch (erro) {
    if (erro.status === 403) {
      // token inválido ou vencido
      sair();
      mostrarMensagem("Sua sessão expirou. Entre novamente.", "erro");
    } else {
      tratarErro(erro);
    }
  }
}

// ----- Serviços -----
async function carregarServicos() {
  servicos = await requisicao("/servicos", "GET");

  // preenche o <select> do formulário de agendamento (todos os usuários)
  const select = el("servico");
  select.innerHTML = "";
  servicos.forEach(function (s) {
    const op = document.createElement("option");
    op.value = s.id;
    op.textContent = s.nome + " - " + formatarValor(s.valor);
    select.appendChild(op);
  });

  // tabela de gerenciamento (só admin)
  if (admin) {
    const corpo = el("lista-servicos");
    corpo.innerHTML = "";
    if (servicos.length === 0) {
      corpo.innerHTML = '<tr><td colspan="4">Nenhum serviço cadastrado. Cadastre um abaixo.</td></tr>';
    }
    servicos.forEach(function (s) {
      corpo.innerHTML +=
        "<tr><td>" + esc(s.nome) + "</td><td>" + s.duracao + " min</td><td>" +
        formatarValor(s.valor) + '</td><td><button class="perigo" onclick="removerServico(' +
        s.id + ')">Remover</button></td></tr>';
    });
  }

  atualizarResumo();
}

async function adicionarServico() {
  if (!admin) return;

  const nome = el("s-nome").value.trim();
  const duracao = parseInt(el("s-duracao").value);
  const valor = parseFloat(el("s-valor").value);

  if (!nome || !(duracao > 0) || isNaN(valor) || valor < 0) {
    mostrarMensagem("Informe nome, duração (maior que 0) e valor do serviço.", "erro");
    return;
  }

  try {
    await requisicao("/servicos", "POST", { nome, duracao, valor });
    el("s-nome").value = "";
    el("s-duracao").value = "";
    el("s-valor").value = "";
    await carregarServicos();
    mostrarMensagem("Serviço cadastrado.", "ok");
  } catch (erro) {
    tratarErro(erro, "Não foi possível cadastrar o serviço. Confira os dados e tente novamente.");
  }
}
el("btn-add-servico").onclick = e => comBotao(e.currentTarget, adicionarServico);

async function removerServico(id) {
  if (!admin) return;
  try {
    // procura os agendamentos que usam este serviço
    const todos = await requisicao("/agendamentos", "GET");
    const doServico = todos.filter(function (a) { return a.servicoId === id; });

    let pergunta = "Remover este serviço?";
    if (doServico.length > 0) {
      pergunta = "Este serviço tem " + doServico.length + " agendamento(s). " +
                 "Remover o serviço também vai remover esses agendamentos. Deseja continuar?";
    }
    if (!confirm(pergunta)) return;

    // remove primeiro os agendamentos do serviço, depois o serviço
    for (const a of doServico) {
      await requisicao("/agendamentos/" + a.id, "DELETE");
    }
    await requisicao("/servicos/" + id, "DELETE");

    await carregarServicos();
    await carregarAgendamentos();
    await carregarTodos();
    mostrarMensagem("Serviço removido.", "ok");
  } catch (erro) {
    tratarErro(erro, "Não foi possível remover este serviço. Tente novamente.", true);
    carregarServicos().catch(function () {});
    carregarAgendamentos().catch(function () {});
    carregarTodos();
  }
}

// ----- Agendamentos -----
function nomeDoServico(id) {
  const s = servicos.find(function (x) { return x.id === id; });
  return s ? s.nome : "Serviço #" + id;
}

function atualizarResumo() {
  const s = servicos.find(function (x) { return x.id == el("servico").value; });
  const data = el("data-inicio").value;

  if (!s) {
    el("resumo").textContent = admin
      ? "Cadastre um serviço para poder agendar."
      : "Nenhum serviço disponível no momento.";
    return;
  }
  let texto = s.nome + " | " + s.duracao + " min | " + formatarValor(s.valor);
  if (data) {
    // o horário de término é o início + a duração do serviço
    const fim = new Date(new Date(data).getTime() + s.duracao * 60000);
    texto += " | Termina às " + fim.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
  }
  el("resumo").textContent = texto;
}
el("servico").onchange = atualizarResumo;
el("data-inicio").onchange = atualizarResumo;

// Agendamentos que já terminaram não podem mais ser cancelados
// Descobre o número (id) do agendamento. Normalmente o campo se chama "id",
// mas aceita outros nomes (agendamentoId, idAgendamento...) ignorando
// os ids do serviço e do usuário.
function idDoAgendamento(a) {
  if (a.id !== undefined && a.id !== null) return a.id;
  for (const chave in a) {
    const k = chave.toLowerCase();
    const ehOutroId = k.includes("servico") || k.includes("usuario") || k.includes("cliente");
    if (k.includes("id") && !ehOutroId && a[chave] !== null && a[chave] !== undefined) {
      return a[chave];
    }
  }
  console.error("Agendamento sem identificador. Campos recebidos do servidor:", a);
  return null;
}

// (o administrador pode remover qualquer agendamento, mesmo os já concluídos)
function botaoCancelar(a, rotulo) {
  if (new Date(a.dataFim) < new Date() && !admin) {
    return '<span class="concluido">Concluído</span>';
  }
  const id = idDoAgendamento(a);
  if (id === null) return "";
  return '<button class="perigo" onclick="cancelarAgendamento(' + id + ', this)">' + (rotulo || "Cancelar") + '</button>';
}

async function carregarAgendamentos() {
  // rota que devolve só os agendamentos do usuário logado
  meusAgendamentos = await requisicao("/agendamentos/usuario", "GET");
  meusAgendamentos.sort(function (a, b) { return new Date(a.dataInicio) - new Date(b.dataInicio); });

  const corpo = el("lista-agendamentos");
  corpo.innerHTML = "";
  if (meusAgendamentos.length === 0) {
    corpo.innerHTML = '<tr><td colspan="4">Você ainda não tem agendamentos.</td></tr>';
  }
  meusAgendamentos.forEach(function (a) {
    corpo.innerHTML +=
      "<tr><td>" + esc(nomeDoServico(a.servicoId)) + "</td><td>" + formatarDataHora(a.dataInicio) +
      "</td><td>" + formatarDataHora(a.dataFim) + "</td><td>" + botaoCancelar(a) + "</td></tr>";
  });
}

// Só admin: lista de todos os agendamentos de todos os clientes
async function carregarTodos() {
  if (!admin) return;
  try {
    await buscarTodos();
  } catch (erro) {
    console.error("Erro técnico:", erro.status, erro.message);
    if (erro.status === 401) tratarErro(erro);
    el("lista-todos").innerHTML = '<tr><td colspan="5">Não foi possível carregar os agendamentos agora.</td></tr>';
  }
}

async function buscarTodos() {
  const lista = await requisicao("/agendamentos", "GET");
  lista.sort(function (a, b) { return new Date(a.dataInicio) - new Date(b.dataInicio); });

  const corpo = el("lista-todos");
  corpo.innerHTML = "";
  if (lista.length === 0) {
    corpo.innerHTML = '<tr><td colspan="5">Nenhum agendamento cadastrado.</td></tr>';
  }
  lista.forEach(function (a) {
    corpo.innerHTML +=
      "<tr><td>Usuário #" + a.usuarioId + "</td><td>" + esc(nomeDoServico(a.servicoId)) +
      "</td><td>" + formatarDataHora(a.dataInicio) + "</td><td>" + formatarDataHora(a.dataFim) +
      "</td><td>" + botaoCancelar(a, "Remover") + "</td></tr>";
  });
}

async function agendar() {
  const servicoId = parseInt(el("servico").value);
  const data = el("data-inicio").value;
  const servico = servicos.find(function (x) { return x.id === servicoId; });

  if (!servico) {
    mostrarMensagem("Escolha um serviço.", "erro");
    return;
  }
  if (!data) {
    mostrarMensagem("Escolha a data e a hora.", "erro");
    return;
  }

  const inicio = new Date(data);
  const fim = new Date(inicio.getTime() + servico.duracao * 60000);

  if (inicio < new Date()) {
    mostrarMensagem("Não é possível agendar em uma data que já passou.", "erro");
    return;
  }

  // Não deixa o usuário marcar dois horários que se sobrepõem
  const conflito = meusAgendamentos.some(function (a) {
    return inicio < new Date(a.dataFim) && fim > new Date(a.dataInicio);
  });
  if (conflito) {
    mostrarMensagem("Você já tem um agendamento nesse horário.", "erro");
    return;
  }

  try {
    // O backend espera a data no formato 2026-10-20T14:30:00
    await requisicao("/agendamentos", "POST", { servicoId: servicoId, dataInicio: data + ":00" });
    el("data-inicio").value = "";
    atualizarResumo();
    await carregarAgendamentos();
    await carregarTodos();
    mostrarMensagem("Agendamento realizado.", "ok");
  } catch (erro) {
    tratarErro(erro, "Não foi possível agendar nesse horário. Escolha outro horário.", true);
  }
}
el("btn-agendar").onclick = e => comBotao(e.currentTarget, agendar);

// 1º clique: o botão vira "Confirmar?" (não depende de janela do navegador)
// 2º clique: remove o agendamento
function cancelarAgendamento(id, botao) {
  if (botao && botao.dataset.confirmar !== "1") {
    botao.dataset.confirmar = "1";
    botao.dataset.rotulo = botao.textContent;
    botao.textContent = "Confirmar?";
    setTimeout(function () {
      if (botao.isConnected && botao.dataset.confirmar === "1") {
        botao.dataset.confirmar = "";
        botao.textContent = botao.dataset.rotulo;
      }
    }, 4000);
    return;
  }
  executarCancelamento(id, botao);
}

async function executarCancelamento(id, botao) {
  if (botao) botao.disabled = true;
  try {
    await requisicao("/agendamentos/" + id, "DELETE");
    await carregarAgendamentos();
    await carregarTodos();
    mostrarMensagem(admin ? "Agendamento removido." : "Agendamento cancelado.", "ok");
  } catch (erro) {
    if (erro.status === 403) {
      console.error("Erro técnico:", erro.status, erro.message);
      mostrarMensagem("Você não tem permissão para remover este agendamento.", "erro");
    } else {
      tratarErro(erro, "Não foi possível remover este agendamento. Tente novamente.", true);
    }
    // atualiza as listas em silêncio (o agendamento pode já ter sido removido)
    carregarAgendamentos().catch(function () {});
    carregarTodos();
  }
}

// ===================== INÍCIO =====================
// Se já existe token salvo, vai direto para a tela principal
if (token) {
  abrirApp();
}
