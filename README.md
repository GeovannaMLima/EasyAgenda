# AgendaFácil

[![Java CI with Maven](https://github.com/GeovannaMLima/EasyAgenda/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/GeovannaMLima/EasyAgenda/actions/workflows/maven.yml)

API de agendamento de serviços para profissionais autônomos (cabeleireiros, personal trainers, terapeutas). Profissionais cadastram os serviços que oferecem, clientes marcam horário, e o sistema **impede que dois agendamentos se sobreponham** para o mesmo profissional.

Projeto com mensageria **RabbitMQ** (notificação) e **Kafka** (auditoria). A aplicação e toda a infraestrutura rodam em containers, com Docker Compose.

## Stack

- Java 25 e Spring Boot 4 (Web, Data JPA, Validation)
- Spring Security com autenticação **JWT** (`java-jwt`) e senhas com **BCrypt**
- PostgreSQL
- RabbitMQ (Spring AMQP)
- Apache Kafka (Spring Kafka)
- Docker (build multi-stage), Docker Compose, DockerHub
- JUnit 5 e Mockito
- GitHub Actions

## Como funciona

Ao criar um agendamento, o sistema valida os dados, verifica se o profissional já tem outro compromisso no mesmo intervalo, salva no banco e publica dois eventos: um para notificar o cliente e outro para registrar a auditoria.

```mermaid
flowchart LR
    C[Cliente / Postman] -->|POST /agendamentos/add| API[Controller]
    API --> S[Service<br/>regra de conflito de horário]
    S -->|salva| DB[(PostgreSQL)]
    S -->|AgendamentoCriadoEvent| R{{RabbitMQ}}
    R --> N[Consumer de notificação<br/>ack manual + DLQ]
    S -->|AgendamentoAuditoriaEvent| K{{Kafka}}
    K --> A[Consumer de auditoria]
```

### Entidades

- **Profissional:** quem presta o serviço.
- **Serviço:** o que o profissional oferece, com duração (em minutos) e preço. Pertence a um profissional.
- **Cliente:** quem marca o horário.
- **Agendamento:** um compromisso marcado, ligando cliente, profissional e serviço a uma data e hora de início. O horário de término é calculado a partir da duração do serviço.

### Regra de conflito de horário

Dois intervalos se sobrepõem quando o novo começa antes do fim do existente **e** o existente começa antes do fim do novo. Se houver sobreposição para o mesmo profissional, o agendamento é recusado e nada é salvo nem publicado.

### Autenticação e perfis

A API usa **JWT**, sem sessão no servidor (stateless). O usuário faz login, recebe um token e envia esse token no header `Authorization: Bearer <token>` nas rotas protegidas.

```mermaid
sequenceDiagram
    participant C as Cliente
    participant A as AuthController
    participant M as AuthenticationManager
    participant T as TokenService
    participant F as SecurityFilter
    C->>A: POST /auth/login (email, senha)
    A->>M: authenticate (BCrypt compara a senha)
    M-->>A: usuário autenticado
    A->>T: gera o token
    T-->>A: JWT com validade de 2 horas
    A-->>C: 200 { "token": "..." }
    C->>F: requisição + Authorization: Bearer token
    F->>F: valida o token e identifica o usuário
    F-->>C: segue para o controller (ou 401/403)
```

### Perfis

| Perfil | Como é criado |
|---|---|
| `CLIENTE` | `POST /cliente/register` |
| `PROFISSIONAL` | `POST /profissional/register` |
| `ADMIN` | Criado no startup a partir de `ADMIN_EMAIL` / `ADMIN_PASSWORD`. Não existe rota pública para virar admin |

### Quem pode o quê

| Endpoint | Acesso |
|---|---|
| `POST /auth/login` | Público |
| `POST /cliente/register` | Público |
| `POST /profissional/register` | Público |
| `POST /servico/add` | `PROFISSIONAL`, `ADMIN` |
| Listar todos os clientes | `ADMIN` |
| `POST /agendamentos/add` | Usuário autenticado; só para o próprio cliente |
| `GET /agendamentos/{id}/list_cliente` | O próprio cliente, `ADMIN` |
| `GET /agendamentos/{id}/list_profissional` | O próprio profissional, `ADMIN` |
| `PATCH /agendamentos/{id}/cancel` | O cliente ou o profissional do agendamento, `ADMIN` |

### Regra de propriedade

Cada usuário só acessa os **próprios** agendamentos; o `ADMIN` acessa todos. O sistema nunca confia no id enviado na URL ou no corpo: compara com o usuário do token. Acesso indevido retorna `403`.

## Decisões de arquitetura

### RabbitMQ para a notificação
A notificação é uma tarefa pontual: um agendamento foi criado, avisa o cliente, confirma e acabou. O RabbitMQ entrega isso com confirmação por mensagem. O consumer usa **ack manual**: só confirma depois de processar com sucesso. Se o processamento falha, a mensagem é rejeitada sem reenfileirar e vai para a **Dead Letter Queue** (`agendamento.criado.dlq`), em vez de se perder ou travar a fila.

### Kafka para a auditoria
A auditoria é um histórico: o que aconteceu precisa ficar registrado e não pode sumir depois de lido. O Kafka guarda os eventos em um log, que outros sistemas poderiam ler no futuro (relatórios, por exemplo), cada um no seu ritmo e com seu próprio consumer group.

### `profissionalId` é a chave da mensagem no Kafka
A chave decide a partição. Com o `profissionalId`, todos os eventos de um mesmo profissional ficam na mesma partição, e a ordem é garantida dentro dela. Por exemplo, "criado" nunca aparece depois de um "cancelado" do mesmo agendamento.

### Kafka com dois listeners
Quando um cliente se conecta ao Kafka, ele recebe de volta o endereço pelo qual deve falar 
com o broker (Advertised Listener), e esse endereço depende de quem está perguntando. A aplicação 
rodando no IDE, fora do Docker, acessa por ```localhost:9092```. Já a aplicação rodando em container precisa
do nome do serviço na rede do Compose, ```kafka:19092```, porque localhost dentro do container seria ele mesmo. Por isso o broker expõe dois listeners (EXTERNAL na porta 9092 e INTERNAL na 19092), além do CONTROLLER, de uso interno do Kafka. Assim, o mesmo Compose permite rodar a aplicação das duas formas.

> Este é um projeto de estudo e usa os dois brokers de propósito, para comparar na prática. Em um sistema real, eu avaliaria se os dois são necessários ou se um deles resolve.

## Como rodar

### Pré-requisitos

- JDK (versão definida no `pom.xml`),apenas para rodar aplicacao fora do docker (opção 2)
- Docker e Docker Compose

### Configuração (valido para as duas opções)

1. Clone o repositório:
   ```bash
   git clone https://github.com/GeovannaMLima/EasyAgenda.git
   cd EasyAgenda
   ```
2. Crie o arquivo de variáveis a partir do exemplo e preencha a senha:
   ```bash
   cp .env.example .env
   ```
   No PowerShell, use ```copy .env.example .env.```

| Variável | Para que serve |
|---|---|
| `DB_USER` / `DB_PASS` | Usuário e senha do PostgreSQL |
| `DB_URL` | URL de conexão com o banco (`jdbc:postgresql://localhost:5433/agendafacil` fora do Docker) |
| `JWT_SECRET` | Segredo usado para assinar os tokens JWT. Use uma string longa e aleatória (32+ caracteres) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Credenciais do administrador criado automaticamente na primeira execução (senha com 8+ caracteres) |

### Opção 1: Docker
3. Suba a infraestrutura (PostgreSQL, RabbitMQ , Kafka e API):
   ```bash
   docker compose up -d --build
   ```
### Opção 2: Aplicação IDE (desenvolvimento)
3. Suba a infraestrutura e rode a aplicação localmente:
   ```bash
   docker compose up -d postgres rabbitmq kafka
   ./mvnw spring-boot:run
   ```

A API fica em `http://localhost:8080`. O painel do RabbitMQ fica em `http://localhost:15672` (usuário e senha `guest`).

## Endpoints

As rotas de cadastro e o login são públicas. As demais exigem `Authorization: Bearer <token>`.

### 1. Cadastro e login

`POST /profissional/register`
```json
{ "nome": "Maria", "email": "maria@email.com", "senha": "senha1234" }
```

`POST /cliente/register`
```json
{ "nome": "Ana", "email": "ana@email.com", "senha": "senha1234", "telefone": "999999999" }
```

`POST /auth/login`
```json
{ "email": "maria@email.com", "senha": "senha1234" }
```
Resposta (`200 OK`): `{ "token": "..." }`

### 2. Serviço (token de PROFISSIONAL)
`POST /servico/add`
```json
{ "nome": "Corte de cabelo", "duracao": 60, "price": 50.00, "profissionalId": 1 }
```

### 3. Agendamento (token de CLIENTE)
`POST /agendamentos/add`
```json
{ "clienteId": 1, "profissionalId": 1, "servicoId": 1, "dataHoraInicio": "2026-12-10T14:00:00" }
```
Resposta (`201 Created`):
```json
{
  "id": 1,
  "nomeCliente": "Ana",
  "nomeProfissional": "Maria",
  "nomeServico": "Corte de cabelo",
  "status": "AGENDADO",
  "dataHoraInicio": "2026-12-10T14:00:00",
  "dataHoraFim": "2026-12-10T15:00:00"
}
```
A data precisa estar no futuro. Um segundo agendamento no mesmo horário para o mesmo profissional é recusado.

### 4. Listar agendamentos (paginado)
- `GET /agendamentos/{clienteId}/list_cliente`
- `GET /agendamentos/{profissionalId}/list_profissional`

Parâmetros opcionais: `status` (`AGENDADO` ou `CANCELADO`), `page` (padrão 0), `size` (padrão 10).
Exemplo: `GET /agendamentos/1/list_cliente?status=AGENDADO&page=0&size=10`

### 5. Cancelar
`PATCH /agendamentos/{id}/cancel` retorna `204 No Content`. O cancelamento é lógico (o status vira `CANCELADO`) e publica um evento de auditoria no Kafka.

## Erros

| Status | Quando |
|---|---|
| `400` | Dados inválidos ou parâmetro com valor inválido |
| `401` | Credenciais inválidas ou token ausente/inválido |
| `403` | Sem permissão para o recurso |
| `404` | Recurso não encontrado |
| `409` | Conflito: horário ocupado, agendamento já cancelado, e-mail já cadastrado |
| `500` | Erro interno |
## Testes

```bash
./mvnw test
```

`AgendamentoServiceTest` cobre a regra de negócio com Mockito, sem subir banco nem brokers:

- Cria o agendamento quando não há conflito e publica os dois eventos;
- Recusa quando há conflito e confirma que nada é salvo nem publicado.
- Nega a listagem de agendamentos de outro cliente (`403`);
- Permite que o `ADMIN` acesse qualquer recurso;
- Nega o cancelamento por quem não é dono do agendamento.

## Integração contínua (CI)
- A cada push e a cada Pull Request, o GitHub Actions executa os testes automaticamente (workflow `Java CI with Maven`).
  
- O workflow `.github/workflows/maven.yml` roda em dois jobs:

1. **build**: a cada push e Pull Request, baixa o código, configura o JDK 25 e executa os testes (`mvn -B test`).
2. **docker**: só em push na `main` e somente se os testes passaram, faz login no Docker Hub e constrói e publica a imagem `geolima2/agendafacil`, com as tags `latest` e o SHA do commit. As credenciais ficam nos GitHub Secrets.

```mermaid
flowchart LR
    G[Push ou Pull Request] --> B

    subgraph CI [GitHub Actions]
        B[Job build<br/>testes: mvn -B test]
        B -->|passou e é push na main| D[Job docker<br/>login e build da imagem]
    end

    B -. falhou .-> X[Pipeline para<br/>nada é publicado]
    S[GitHub Secrets<br/>usuário e token] -.-> D
    D -->|push| H[(Docker Hub<br/>geolima2/agendafacil)]
    H -->|docker pull| M[Qualquer máquina<br/>ou servidor]
```
Para baixar a imagem publicada:

`docker pull geolima2/agendafacil:latest`