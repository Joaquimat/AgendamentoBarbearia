# 💈 Barbearia Tecnobelo — Sistema de Agendamentos (Front-end)

Front-end do sistema de agendamento da **Barbearia Tecnobelo**. Os clientes criam uma conta, escolhem um serviço (corte, barba etc.) e marcam um horário. O administrador gerencia os serviços e acompanha todos os agendamentos.

Feito com **HTML, CSS e JavaScript puro**, sem bibliotecas nem frameworks, consumindo uma API REST em **Spring Boot**.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Regras de negócio](#regras-de-negócio)
- [Tecnologias](#tecnologias)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Como executar](#como-executar)
- [Configuração](#configuração)
- [Endpoints utilizados](#endpoints-utilizados)
- [Problemas comuns](#problemas-comuns)

## Funcionalidades

### Para todos os usuários

- Cadastro e login com autenticação por token **JWT**.
- Vitrine com os serviços disponíveis, mostrando nome, duração e valor.
- Agendamento escolhendo serviço, data e horário (de 30 em 30 minutos).
- Lista dos próprios agendamentos, com opção de cancelar.

### Somente para administradores

- Cadastro e remoção de serviços.
- Lista de todos os agendamentos de todos os clientes, com opção de remover.
- Selo **"Administrador"** no topo da página.

## Regras de negócio

**Agendamento**

- Só é possível agendar com o usuário logado; o token é enviado em todas as chamadas protegidas.
- Não é possível agendar em uma data ou horário que já passou.
- O horário de término é calculado como *início + duração do serviço*.
- O usuário não pode marcar dois horários que se sobreponham.

**Cancelamento e conclusão**

- Agendamentos que já terminaram aparecem como **"Concluído"** e não podem ser cancelados por usuários comuns.
- O administrador pode remover qualquer agendamento.
- Ao remover um serviço que já tem agendamentos, o administrador é avisado de que eles serão removidos junto.

**Perfis e sessão**

- O administrador é identificado pelo próprio servidor: se a lista geral de agendamentos for liberada para a conta, ela é tratada como administradora.
- Sessão expirada leva o usuário de volta ao login.

**Mensagens**

- Os erros são exibidos em linguagem simples, sem códigos ou detalhes técnicos. Os detalhes ficam apenas no console do navegador (F12).

## Tecnologias

| Camada | Tecnologias |
|---|---|
| Front-end | HTML5, CSS3 e JavaScript (ES6+) |
| Comunicação | `fetch` para consumir a API REST |
| Autenticação | JWT |
| Armazenamento no navegador | `localStorage`, usado para manter o login |
| Back-end | Java 21, Spring Boot, Spring Security, JPA/Hibernate e PostgreSQL |

## Estrutura do projeto

```
barbearia/
├── index.html   # estrutura da página
├── style.css    # aparência
├── script.js    # lógica e comunicação com a API
└── README.md
```

## Como executar

### 1. Back-end

Deixe o back-end (Spring Boot) rodando em `http://localhost:8080`. As rotas começam com `/brb`.

> **Importante:** o back-end precisa liberar **CORS** para o front-end, senão o navegador bloqueia as chamadas. No Spring Security, isso é feito com `http.cors(...)` e um bean `CorsConfigurationSource` que permita:
>
> - a origem do front-end (por exemplo, `http://localhost:5500`);
> - os métodos `GET`, `POST`, `PUT`, `DELETE` e `OPTIONS`;
> - os cabeçalhos necessários, como `Authorization` e `Content-Type`.

### 2. Front-end

Não abra o `index.html` com duplo clique: o navegador trata arquivos locais (`file://`) como origem `null` e bloqueia as requisições. Sirva a pasta por um servidor local, de uma destas formas:

**Opção A: VS Code**

1. Instale a extensão **Live Server**.
2. Abra a pasta do projeto.
3. Clique com o botão direito no `index.html` e escolha **Open with Live Server**.

**Opção B: Python**

```bash
python -m http.server 5500
```

Depois, acesse `http://localhost:5500`.

## Configuração

As constantes ficam no início do `script.js`:

| Constante | Para que serve | Padrão |
|---|---|---|
| `API` | Endereço do back-end | `http://localhost:8080/brb` |
| `HORA_ABRIR` | Primeiro horário do dia | `8` (08:00) |
| `HORA_FECHAR` | Horário de fechamento; o último horário sai antes dele | `19` (último: 18:30) |
| `INTERVALO` | Minutos entre um horário e outro | `30` |

## Endpoints utilizados

| Método | Rota | Uso | Perfil |
|---|---|---|---|
| POST | `/brb/auth/register` | Criar conta | Todos |
| POST | `/brb/auth/login` | Entrar e receber o token | Todos |
| GET | `/brb/servicos` | Listar serviços | Todos |
| POST | `/brb/servicos` | Cadastrar serviço | Admin |
| DELETE | `/brb/servicos/{id}` | Remover serviço | Admin |
| GET | `/brb/agendamentos/usuario` | Agendamentos do usuário logado | Todos |
| GET | `/brb/agendamentos` | Todos os agendamentos | Admin |
| POST | `/brb/agendamentos` | Criar agendamento | Todos |
| DELETE | `/brb/agendamentos/{id}` | Remover agendamento | Todos* |

\* Usuários comuns só removem os próprios agendamentos que ainda não foram concluídos.

A data do agendamento é enviada no formato ISO 8601, por exemplo `2026-10-20T14:30:00`.

## Problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| "Sem conexão com o servidor" | Back-end desligado ou CORS não liberado | Confirme que o back-end está rodando e confira o console (F12) |
| Erro de CORS no console | Falta a configuração de CORS no back-end, ou a página foi aberta direto do arquivo | Configure o CORS e sirva o front por um servidor local |
| As opções de administrador não aparecem | A conta não é administradora, ou o servidor não liberou a lista geral de agendamentos para ela | Veja a linha "Teste de administrador" no console |
| O token para de funcionar depois de um tempo | O token expira (cerca de 15 minutos) | Entre novamente |
