# Provisionamento Azure SQL + Azure Container Apps

Pré-requisitos: [Azure CLI](https://learn.microsoft.com/cli/azure/install-azure-cli) instalada e `az login` realizado.
A imagem já deve estar publicada no Docker Hub (feito automaticamente pelo workflow **Continuous Delivery**).

> Substitua `<rm>` pelo RM de um integrante e `murilomercadante` pelo usuário do Docker Hub.
> **Nunca** versione a senha real do banco.

## Ambiente implantado

- Resource group: `rg-rm554799-incluze` (brazilsouth)
- SQL Server: `rm554799-sql-incluze.database.windows.net` · banco `aether` (camada gratuita serverless)
- Container App: `rm554799-incluze-api`
- URL: https://rm554799-incluze-api.livelytree-72ea2755.brazilsouth.azurecontainerapps.io/

## 1. Variáveis

### PowerShell

```powershell
$env:RESOURCE_GROUP="rg-<rm>-aether"
$env:LOCATION="brazilsouth"
$env:SQL_SERVER="<rm>-sql-aether"
$env:APP_CONTAINER="<rm>-aether-api"
$env:CONTAINERAPPS_ENVIRONMENT="<rm>-aether-env"

$env:DB_SERVER_URL="<rm>-sql-aether.database.windows.net"
$env:DB_SERVER_PORT="1433"
$env:DB_SCHEMA="aether"
$env:DB_USER="sqladmin"
$env:DB_PWD="<senha-forte>"

$env:SPRING_PROFILES_ACTIVE="prd"
$env:DOCKER_IMAGE="docker.io/murilomercadante/incluze-api:latest"
```

### Bash

```bash
export RESOURCE_GROUP=rg-<rm>-aether
export LOCATION=brazilsouth
export SQL_SERVER=<rm>-sql-aether
export APP_CONTAINER=<rm>-aether-api
export CONTAINERAPPS_ENVIRONMENT=<rm>-aether-env

export DB_SERVER_URL=<rm>-sql-aether.database.windows.net
export DB_SERVER_PORT=1433
export DB_SCHEMA=aether
export DB_USER=sqladmin
export DB_PWD="<senha-forte>"

export SPRING_PROFILES_ACTIVE=prd
export DOCKER_IMAGE=docker.io/murilomercadante/incluze-api:latest
```

> Os comandos abaixo estão em sintaxe Bash. No PowerShell, troque `\` por `` ` `` no fim das linhas e `$VAR` por `$env:VAR`.

## 2. Resource Group

```bash
az group create --name $RESOURCE_GROUP --location $LOCATION
```

## 3. SQL Server (lógico)

```bash
az sql server create \
  --name $SQL_SERVER \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --admin-user $DB_USER \
  --admin-password "$DB_PWD"
```

## 4. Banco de dados (camada gratuita serverless)

```bash
az sql db create \
  --name $DB_SCHEMA \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER \
  --edition GeneralPurpose \
  --family Gen5 \
  --capacity 2 \
  --compute-model Serverless \
  --use-free-limit \
  --free-limit-exhaustion-behavior AutoPause \
  --backup-storage-redundancy Local
```

## 5. Firewall do SQL Server

Permitir serviços do Azure (necessário para o Container App acessar o banco):

```bash
az sql server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0
```

(Opcional) Permitir o seu IP para consultar o banco pelo Azure Data Studio/SSMS:

```bash
az sql server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER \
  --name MeuIP \
  --start-ip-address <seu-ip> \
  --end-ip-address <seu-ip>
```

## 6. Container Apps Environment

```bash
az extension add --name containerapp --upgrade
az provider register --namespace Microsoft.App
az provider register --namespace Microsoft.OperationalInsights

az containerapp env create \
  --name $CONTAINERAPPS_ENVIRONMENT \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --logs-destination none
```

> Se der erro ao gerar o workspace do Log Analytics, o `--logs-destination none` evita o problema. Os logs do console continuam disponíveis com `az containerapp logs show`.

## 7. Container App (senha armazenada como *secret*)

```bash
az containerapp create \
  --name $APP_CONTAINER \
  --resource-group $RESOURCE_GROUP \
  --environment $CONTAINERAPPS_ENVIRONMENT \
  --image $DOCKER_IMAGE \
  --ingress external \
  --target-port 8080 \
  --min-replicas 1 \
  --max-replicas 1 \
  --secrets "db-pwd=$DB_PWD" \
  --env-vars \
    DB_SERVER_URL="$DB_SERVER_URL" \
    DB_SERVER_PORT="$DB_SERVER_PORT" \
    DB_SCHEMA="$DB_SCHEMA" \
    DB_USER="$DB_USER" \
    DB_PWD="secretref:db-pwd" \
    SPRING_PROFILES_ACTIVE="$SPRING_PROFILES_ACTIVE"
```

## 8. URL pública da API

```bash
az containerapp show \
  --name $APP_CONTAINER \
  --resource-group $RESOURCE_GROUP \
  --query properties.configuration.ingress.fqdn \
  --output tsv
```

Acesse `https://<fqdn>/` → Swagger UI.

## 9. Status, logs e redeploy

```bash
# status
az containerapp show --name $APP_CONTAINER --resource-group $RESOURCE_GROUP \
  --query "properties.runningStatus" --output tsv

# logs
az containerapp logs show --name $APP_CONTAINER --resource-group $RESOURCE_GROUP --type console --tail 50

# redeploy forçando nova revisão com a imagem :latest
az containerapp update --name $APP_CONTAINER --resource-group $RESOURCE_GROUP \
  --image $DOCKER_IMAGE --revision-suffix r$(date +%Y%m%d%H%M%S)
```

## 10. (Opcional) Deploy pelo GitHub Actions

O workflow `.github/workflows/deploy.yml` (execução manual) atualiza o Container App. Configure no repositório:

| Tipo | Nome | Valor |
|---|---|---|
| Secret | `AZURE_CREDENTIALS` | saída de `az ad sp create-for-rbac --name aether-gha --role contributor --scopes /subscriptions/<id>/resourceGroups/$RESOURCE_GROUP --json-auth` |
| Variable | `AZURE_RESOURCE_GROUP` | nome do resource group |
| Variable | `AZURE_CONTAINER_APP` | nome do container app |

## 11. Estrutura dos recursos

```text
Azure
└── Resource Group: rg-<rm>-aether
    ├── SQL Server: <rm>-sql-aether
    │   └── Database: aether  (tabelas: usuarios, locais, avaliacoes, alertas)
    └── Container Apps Environment: <rm>-aether-env
        └── Container App: <rm>-aether-api
            └── Image: murilomercadante/incluze-api:latest
```

## 12. Fluxo

```text
Internet ──HTTPS──► Azure Container App (Spring Boot :8080)
                         │  JDBC (TLS) <rm>-sql-aether.database.windows.net:1433
                         ▼
                    Azure SQL Database "aether"
```

## 13. Limpeza (após a avaliação)

```bash
az group delete --name $RESOURCE_GROUP --yes --no-wait
```
