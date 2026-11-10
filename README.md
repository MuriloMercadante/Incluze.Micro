# CitizenTech de Acessibilidade — API (Grupo Aether)

> Global Solution 2026 · 3SIR · Microservice and Web Engineering & IT Services

| Evidência | Link |
|---|---|
| **API no Azure (Swagger)** | `https://<fqdn-do-container-app>/` |
| **Docker Hub** | `https://hub.docker.com/r/murilomercadante/aether-citizentech-api` |
| **GitHub** | https://github.com/MuriloMercadante/Incluze.Micro |
| **GitHub Actions** | https://github.com/MuriloMercadante/Incluze.Micro/actions |

## 1. Objetivo da solução

As cidades e os estabelecimentos foram desenhados para um usuário "padrão". Para pessoas com deficiência física, visual, auditiva ou neurodivergências, ir a um restaurante, hospital ou repartição pública envolve incerteza, porque a informação de acessibilidade costuma ser binária, autodeclarada e muitas vezes irreal.

A **Plataforma CitizenTech** é uma solução de **crowdsourcing**: a própria comunidade (pessoas com deficiência, familiares e simpatizantes) **mapeia, avalia e reporta** as condições reais de acessibilidade de espaços públicos e privados. Este microsserviço é o back-end da plataforma e oferece:

- **Mapeamento de inclusão invisível**: além de rampa, cada local registra atendimento em **Libras**, **cardápio em Braille**, aceitação de **cão-guia** e **ambiente de baixo estímulo** (espectro autista).
- **Alertas temporários ("Waze da inclusão")**: barreiras passageiras como *elevador quebrado hoje* ou *obra bloqueando o piso tátil*, com data de expiração e desativação automática.
- **Gamificação**: +10 pontos por avaliação e +5 por alerta reportado (os pontos são estornados se o registro for excluído).
- **Nota média por local**, recalculada automaticamente a cada avaliação.

Alinhamento com a ONU: **ODS 10** (Redução das Desigualdades) e **ODS 11** (Cidades e Comunidades Sustentáveis).

## 2. Arquitetura

```text
Controller  ──►  Service  ──►  Repository  ──►  SQL Server
   │  (DTOs)        │ regras de negócio      (Spring Data JPA)
   │                └─► Mapper (ModelMapper): Request DTO → Entity → Response DTO
   └─► GlobalExceptionHandler: erros padronizados (400/404/409/500)
```

| Camada | Pacote | Responsabilidade |
|---|---|---|
| Controller | `controller` | Endpoints REST `/api/v1/...`, validação (`@Valid`) e status HTTP. **Não acessa Repository.** |
| Service | `service` | Regras de negócio (pontos, nota média, unicidade, vínculos) e transações |
| Repository | `repository` | Persistência no SQL Server (`JpaRepository`) |
| Entity | `model` | Entidades JPA mapeadas para tabelas no plural |
| DTO | `dto` | `*Request` (entrada), `*Response` (saída) e `ErrorResponse` |
| Mapper | `mapper` | Conversão DTO ⇄ Entity com **ModelMapper** |
| Exception | `exception` | `ResourceNotFoundException`, `BusinessException` e handler global |

**Stack:** Java 17 · Spring Boot 4.0.3 · Spring Data JPA · Microsoft SQL Server (`mssql-jdbc`) · Bean Validation · ModelMapper 3.1.1 · springdoc-openapi 3.0.2 · Docker · GitHub Actions · Azure Container Apps + Azure SQL Database.

### Modelo de dados

```text
usuarios 1 ──── N avaliacoes N ──── 1 locais
usuarios 1 ──── N alertas    N ──── 1 locais
```

| Tabela | Atributos | Obrigatórios |
|---|---|---|
| `usuarios` | id, nome, email (único), tipo_deficiencia, cidade, pontos, data_cadastro | nome, email, pontos, data_cadastro |
| `locais` | id, nome, endereco, cidade, categoria, possui_rampa, atendimento_libras, cardapio_braille, aceita_cao_guia, ambiente_baixo_estimulo, nota_media, data_cadastro | nome, endereco, cidade, categoria, recursos (booleanos), data_cadastro |
| `avaliacoes` | id, nota (1–5), comentario, acessibilidade_confirmada, data_avaliacao, usuario_id (FK), local_id (FK) | nota, acessibilidade_confirmada, data_avaliacao, usuario_id, local_id |
| `alertas` | id, titulo, descricao, tipo, ativo, data_criacao, data_expiracao, usuario_id (FK), local_id (FK) | titulo, tipo, ativo, data_criacao, data_expiracao, usuario_id, local_id |

As tabelas são criadas automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## 3. Endpoints (v1)

A documentação interativa (Swagger UI) é aberta na **rota raiz `/`**. A especificação OpenAPI fica em `/v3/api-docs`.

| Recurso | Base |
|---|---|
| Usuários | `/api/v1/usuarios` |
| Locais | `/api/v1/locais` |
| Avaliações | `/api/v1/avaliacoes` |
| Alertas | `/api/v1/alertas` |

Cada recurso expõe:

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| `POST` | `/` | **201 Created** | 400, 404 (FK inexistente), 409 |
| `GET` | `/` | **200 OK** | — |
| `GET` | `/{id}` | **200 OK** | 400 (id não numérico), 404 |
| `PUT` | `/{id}` | **200 OK** | 400, 404, 409 |
| `DELETE` | `/{id}` | **204 No Content** | 404, 409 (registro com vínculos) |

**409 Conflict** é usado de forma consistente para violações de regra de negócio: e-mail duplicado, usuário avaliando o mesmo local duas vezes e exclusão de usuário/local que ainda possui avaliações ou alertas.

### Formato padronizado de erro

Nenhuma resposta expõe stack trace, SQL ou mensagem interna de exceção.

```json
{
  "timestamp": "2026-11-10T14:32:10.123",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados de entrada inválidos",
  "caminho": "/api/v1/usuarios",
  "campos": [
    { "campo": "email", "mensagem": "E-mail inválido" }
  ]
}
```

## 4. Variáveis de ambiente

| Variável | Descrição | Exemplo (dev) |
|---|---|---|
| `DB_SERVER_URL` | Host do SQL Server | `localhost` |
| `DB_SERVER_PORT` | Porta | `1433` |
| `DB_SCHEMA` | Nome do banco | `aether` |
| `DB_USER` | Usuário | `sa` |
| `DB_PWD` | Senha (**obrigatória, sem valor padrão**) | — |
| `SPRING_PROFILES_ACTIVE` | `dev` (local, sem TLS) ou `prd` (Azure SQL, com TLS) | `dev` |

Copie `.env.example` para `.env` (o `.env` está no `.gitignore`).

## 5. Execução local

### Opção A — Docker Compose (SQL Server + API, recomendado)

```bash
cp .env.example .env        # edite DB_PWD com uma senha forte (mín. 8 caracteres, maiúscula, minúscula, número e símbolo)
docker compose up --build
```

O compose sobe o SQL Server 2022, cria o banco `aether` e inicia a API em **http://localhost:8080**.

### Opção B — SQL Server em container + Spring Boot via Maven

1. Suba o SQL Server:

   ```bash
   docker run -d --name aether-sqlserver -p 1433:1433 \
     -e ACCEPT_EULA=Y -e MSSQL_SA_PASSWORD="<senha-forte>" \
     mcr.microsoft.com/mssql/server:2022-latest
   ```

2. Crie o banco:

   ```bash
   docker exec aether-sqlserver /opt/mssql-tools18/bin/sqlcmd \
     -S localhost -U sa -P "<senha-forte>" -C -Q "CREATE DATABASE aether"
   ```

3. Defina as variáveis e execute:

   **Linux/macOS**

   ```bash
   export DB_SERVER_URL=localhost DB_SERVER_PORT=1433 DB_SCHEMA=aether DB_USER=sa DB_PWD="<senha-forte>" SPRING_PROFILES_ACTIVE=dev
   ./mvnw spring-boot:run
   ```

   **Windows PowerShell**

   ```powershell
   $env:DB_SERVER_URL="localhost"; $env:DB_SERVER_PORT="1433"; $env:DB_SCHEMA="aether"
   $env:DB_USER="sa"; $env:DB_PWD="<senha-forte>"; $env:SPRING_PROFILES_ACTIVE="dev"
   .\mvnw.cmd spring-boot:run
   ```

### Testes automatizados

```bash
./mvnw test
```

## 6. Docker e Docker Hub

**Imagem:** `murilomercadante/aether-citizentech-api`
**Tags publicadas a cada push na `main`:** `latest`, `v<número-da-execução>` e `<sha-curto-do-commit>`

```bash
# obter a imagem
docker pull murilomercadante/aether-citizentech-api:latest

# executar (SQL Server rodando no host)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DB_SERVER_URL=host.docker.internal -e DB_SERVER_PORT=1433 \
  -e DB_SCHEMA=aether -e DB_USER=sa -e DB_PWD="<senha-forte>" \
  murilomercadante/aether-citizentech-api:latest

# build local
docker build -t aether-citizentech-api:local .
```

O `Dockerfile` usa **multi-stage build**: Maven/JDK 17 compila o projeto e uma imagem JRE 17 Alpine, com usuário não-root, executa o `app.jar`.

## 7. Continuous Delivery (GitHub Actions)

Arquivo: [`.github/workflows/continuous_delivery.yml`](.github/workflows/continuous_delivery.yml)

Disparado **automaticamente a cada push na branch `main`**:

1. Checkout do código
2. Setup do Java 17 e `./mvnw clean package` (compila e roda os testes)
3. Geração das tags (`v<run_number>`, `<sha>`, `latest`)
4. Login no Docker Hub
5. Build e **push** da imagem

Configuração necessária em *Settings → Secrets and variables → Actions*:

| Tipo | Nome | Valor |
|---|---|---|
| Variable | `DOCKER_USER` | usuário do Docker Hub |
| Secret | `DOCKER_TOKEN` | Access Token do Docker Hub (*Account settings → Personal access tokens*, permissão Read & Write) |

O deploy no Azure é feito conforme [DEPLOY_AZURE.md](DEPLOY_AZURE.md). Há também um workflow manual opcional, `deploy.yml`.

## 8. Implantação no Azure

- **Aplicação:** Azure Container Apps, com ingress externo na porta 8080 e imagem vinda do Docker Hub
- **Banco:** Azure SQL Database (SQL Server), conectado com TLS (`encrypt=true`) pelo profile `prd`
- **Credenciais:** a senha é guardada como *secret* do Container App e nunca vai para o repositório

Passo a passo completo: **[DEPLOY_AZURE.md](DEPLOY_AZURE.md)**.

**URL:** `https://<fqdn-do-container-app>/`

## 9. Como testar

Pelo **Swagger** (`/`), use *Try it out* em cada endpoint, ou pelo terminal. Defina a URL base:

```bash
API=http://localhost:8080          # ou https://<fqdn-do-container-app>
```

### Roteiro completo (CRUD + regras de negócio + persistência)

```bash
# 1. Criar usuário → 201
curl -i -X POST $API/api/v1/usuarios -H "Content-Type: application/json" -d '{
  "nome": "Ana Souza", "email": "ana.souza@email.com",
  "tipoDeficiencia": "VISUAL", "cidade": "São Paulo"}'

# 2. Criar local → 201
curl -i -X POST $API/api/v1/locais -H "Content-Type: application/json" -d '{
  "nome": "Restaurante Sabor da Vila", "endereco": "Rua Augusta, 1500",
  "cidade": "São Paulo", "categoria": "RESTAURANTE",
  "possuiRampa": true, "atendimentoLibras": true, "cardapioBraille": false,
  "aceitaCaoGuia": true, "ambienteBaixoEstimulo": false}'

# 3. Avaliar o local → 201 (usuário ganha 10 pontos e o local passa a ter nota média)
curl -i -X POST $API/api/v1/avaliacoes -H "Content-Type: application/json" -d '{
  "nota": 4, "comentario": "Atendente fluente em Libras.",
  "acessibilidadeConfirmada": true, "usuarioId": 1, "localId": 1}'

# 4. Reportar alerta temporário → 201 (+5 pontos)
curl -i -X POST $API/api/v1/alertas -H "Content-Type: application/json" -d '{
  "titulo": "Elevador da entrada fora de serviço", "tipo": "ELEVADOR_QUEBRADO",
  "descricao": "Usar a entrada lateral.", "dataExpiracao": "2026-12-31T18:00:00",
  "usuarioId": 1, "localId": 1}'

# 5. Consultar → 200 (pontos = 15, notaMedia = 4.0)
curl -i $API/api/v1/usuarios/1
curl -i $API/api/v1/locais/1
curl -i $API/api/v1/avaliacoes
curl -i $API/api/v1/alertas

# 6. Atualizar → 200
curl -i -X PUT $API/api/v1/avaliacoes/1 -H "Content-Type: application/json" -d '{
  "nota": 5, "comentario": "Melhorou!", "acessibilidadeConfirmada": true, "usuarioId": 1, "localId": 1}'

# 7. Excluir → 204
curl -i -X DELETE $API/api/v1/alertas/1
```

### Cenários de erro

```bash
curl -i $API/api/v1/locais/9999                                    # 404 recurso inexistente
curl -i $API/api/v1/locais/abc                                     # 400 id inválido
curl -i -X POST $API/api/v1/usuarios -H "Content-Type: application/json" \
     -d '{"nome":"A","email":"invalido"}'                          # 400 com lista de campos
curl -i -X POST $API/api/v1/usuarios -H "Content-Type: application/json" -d '{
  "nome":"Ana Souza","email":"ana.souza@email.com"}'               # 409 e-mail duplicado
curl -i -X POST $API/api/v1/avaliacoes -H "Content-Type: application/json" -d '{
  "nota":4,"acessibilidadeConfirmada":true,"usuarioId":1,"localId":1}'  # 409 avaliação repetida
curl -i -X DELETE $API/api/v1/usuarios/1                           # 409 usuário com vínculos
curl -i -X POST $API/api/v1/alertas -H "Content-Type: application/json" -d '{
  "titulo":"Obra na calçada","tipo":"INEXISTENTE"}'                # 400 enum inválido
```

### Comprovar a persistência no SQL Server

Os dados continuam disponíveis após reiniciar o container ou criar uma nova revisão no Azure. Para consultá-los diretamente:

```sql
SELECT * FROM usuarios;
SELECT * FROM locais;
SELECT * FROM avaliacoes;
SELECT * FROM alertas;
```

Use o *Query editor* do Azure SQL no Portal, ou o Azure Data Studio/SSMS. Localmente:
`docker exec -it aether-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "<senha>" -C -d aether -Q "SELECT * FROM usuarios"`.

## 10. Estrutura do projeto

```text
.
├── .github/workflows/
│   ├── continuous_delivery.yml   # build + push Docker Hub (push na main)
│   └── deploy.yml                # deploy Azure (manual, opcional)
├── src/main/java/br/com/fiap/aether/
│   ├── config/        # OpenAPI e CORS
│   ├── controller/    # UsuarioController, LocalController, AvaliacaoController, AlertaController
│   ├── dto/           # *Request, *Response, ErrorResponse
│   ├── exception/     # exceções de domínio + GlobalExceptionHandler
│   ├── mapper/        # ModelMapper (DTO ⇄ Entity)
│   ├── model/         # Usuario, Local, Avaliacao, Alerta + enums
│   ├── repository/    # Spring Data JPA
│   └── service/       # regras de negócio
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prd.properties
├── Dockerfile
├── docker-compose.yml
├── DEPLOY_AZURE.md
└── README.md
```

## 11. Integrantes

| Nome | RM |
|---|---|
| `<nome completo>` | `<RM>` |
| `<nome completo>` | `<RM>` |
| `<nome completo>` | `<RM>` |
