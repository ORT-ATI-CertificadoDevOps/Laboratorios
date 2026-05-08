# Secrets Management con AWS Secrets Manager

Las variables de entorno son mejor que hardcodear secretos en el código, pero no son suficientes: están visibles en los logs de ECS task definitions, en los archivos `.env` que viajan en el repositorio, y en las variables de GitHub Actions exportadas sin protección.

**AWS Secrets Manager** centraliza el almacenamiento de secretos (contraseñas, API keys, cadenas de conexión), controla quién puede acceder a ellos mediante IAM, y puede rotarlos automáticamente sin cambiar la aplicación.

## 7.1 Crear un secreto

**Desde la consola AWS:**

1. Ir a **AWS Secrets Manager** en la consola
2. Hacer clic en **Store a new secret**
3. Seleccionar el tipo:
   - **Credentials for Amazon RDS** — para cadenas de conexión a RDS (soporta rotación automática)
   - **Other type of secret** — para API keys y secretos arbitrarios
4. Ingresar el secreto como pares clave/valor:
   ```json
   {
     "DB_PASSWORD": "mi-contraseña-segura",
     "DB_HOST": "mi-db.abcdef.us-east-1.rds.amazonaws.com"
   }
   ```
5. Asignar un nombre descriptivo: `mi-app/produccion/db`
6. Configurar rotación (opcional en este lab, ver sección 7.5)
7. Finalizar la creación

**Desde la CLI:**

```bash
aws secretsmanager create-secret \
  --name "mi-app/produccion/db" \
  --description "Credenciales de base de datos para producción" \
  --secret-string '{"DB_PASSWORD":"mi-contraseña-segura","DB_HOST":"mi-db.ejemplo.rds.amazonaws.com"}'
```

## 7.2 Recuperar un secreto desde la aplicación

**Node.js:**

```javascript
const { SecretsManagerClient, GetSecretValueCommand } = require("@aws-sdk/client-secrets-manager");

async function getSecret(secretName) {
  const client = new SecretsManagerClient({ region: "us-east-1" });
  const response = await client.send(
    new GetSecretValueCommand({ SecretId: secretName })
  );
  return JSON.parse(response.SecretString);
}

// Uso
const secrets = await getSecret("mi-app/produccion/db");
const dbPassword = secrets.DB_PASSWORD;
```

**Python:**

```python
import boto3
import json

def get_secret(secret_name):
    client = boto3.client("secretsmanager", region_name="us-east-1")
    response = client.get_secret_value(SecretId=secret_name)
    return json.loads(response["SecretString"])

secrets = get_secret("mi-app/produccion/db")
db_password = secrets["DB_PASSWORD"]
```

**Importante:** la aplicación nunca maneja el secreto directamente en código — solo lo lee en runtime desde Secrets Manager. Si alguien accede al código fuente, no encuentra credenciales.

## 7.3 Permisos IAM para acceder al secreto

La aplicación (corriendo en EC2 o ECS) necesita un IAM Role con permiso para leer el secreto. Ejemplo en Terraform:

```hcl
resource "aws_iam_policy" "read_secret" {
  name = "mi-app-read-db-secret"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["secretsmanager:GetSecretValue"]
      Resource = "arn:aws:secretsmanager:us-east-1:123456789:secret:mi-app/produccion/db-*"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "app_secret" {
  role       = aws_iam_role.app_role.name
  policy_arn = aws_iam_policy.read_secret.arn
}
```

El ARN usa `*` al final porque Secrets Manager agrega un sufijo aleatorio al nombre del secreto.

## 7.4 Usar secretos en GitHub Actions

Para pipelines de CI que necesitan acceder a secretos de AWS (por ejemplo, para hacer deploy), la práctica recomendada es usar **OIDC** para autenticarse en AWS sin almacenar credenciales como secrets de GitHub.

Si se usan secrets de GitHub, agregarlos en **Settings → Secrets and variables → Actions** del repositorio y referenciarlos en el workflow:

```yaml
env:
  AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
  AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
```

Para recuperar un secret de Secrets Manager dentro de un job:

```yaml
- name: Get database credentials
  run: |
    SECRET=$(aws secretsmanager get-secret-value \
      --secret-id mi-app/produccion/db \
      --query SecretString \
      --output text)
    DB_PASSWORD=$(echo $SECRET | jq -r '.DB_PASSWORD')
    echo "::add-mask::$DB_PASSWORD"   # enmascara el valor en los logs
    echo "DB_PASSWORD=$DB_PASSWORD" >> $GITHUB_ENV
```

El comando `::add-mask::` es crítico: sin él, el valor del secreto aparece en texto plano en los logs del workflow.

## 7.5 Rotación automática de secretos

Secrets Manager puede rotar credenciales de RDS automáticamente. Al habilitar la rotación:

1. Secrets Manager genera una nueva contraseña
2. La aplica en RDS
3. Actualiza el secreto almacenado

La aplicación que usa `GetSecretValue` obtiene la nueva contraseña automáticamente en la próxima llamada — sin necesidad de redeploy.

**Habilitar rotación en la consola:**

1. Abrir el secreto en Secrets Manager
2. Hacer clic en **Edit rotation**
3. Seleccionar **Automatic rotation**
4. Elegir el período (30, 60, 90 días)
5. Seleccionar la Lambda de rotación (AWS provee lambdas predefinidas para RDS MySQL, PostgreSQL, etc.)

**Desde la CLI:**

```bash
aws secretsmanager rotate-secret \
  --secret-id "mi-app/produccion/db" \
  --rotation-rules AutomaticallyAfterDays=30
```

## 7.6 Cachear secretos para reducir latencia

Llamar a Secrets Manager en cada request de la aplicación agrega latencia y costo. La práctica recomendada es cachear el secreto en memoria con TTL:

```javascript
const { SecretsManagerClient, GetSecretValueCommand } = require("@aws-sdk/client-secrets-manager");

const cache = {};
const CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutos

async function getSecret(secretName) {
  const now = Date.now();
  if (cache[secretName] && (now - cache[secretName].ts) < CACHE_TTL_MS) {
    return cache[secretName].value;
  }
  const client = new SecretsManagerClient({ region: "us-east-1" });
  const response = await client.send(
    new GetSecretValueCommand({ SecretId: secretName })
  );
  const value = JSON.parse(response.SecretString);
  cache[secretName] = { value, ts: now };
  return value;
}
```

AWS también provee el [AWS Secrets Manager Agent](https://docs.aws.amazon.com/secretsmanager/latest/userguide/secrets-manager-agent.html), un sidecar que corre junto a la aplicación y expone los secretos en HTTP local con caché.

## 7.7 Comparación con Parameter Store

AWS ofrece dos servicios para gestionar secretos y configuración:

| | Secrets Manager | Parameter Store (SSM) |
|---|---|---|
| Precio | ~0.40 USD/secreto/mes | Gratis (Standard tier) |
| Rotación automática | ✅ Nativa con Lambda | ❌ Requiere implementación propia |
| Tipos de valor | String, Binary | String, SecureString, StringList |
| Versionado | ✅ Automático | ✅ Manual |
| Caso de uso | Contraseñas, API keys que necesitan rotación | Configuración de aplicación, secretos sin rotación |

Para credenciales de base de datos y API keys que deben rotar: Secrets Manager. Para configuración de ambiente (feature flags, URLs de servicios internos): Parameter Store.

## 7.8 Resumen

| Acción | Comando / Configuración |
|--------|------------------------|
| Crear secreto | `aws secretsmanager create-secret --name ...` |
| Leer secreto | `aws secretsmanager get-secret-value --secret-id ...` |
| Rotación automática | Consola → Edit rotation, o `rotate-secret` CLI |
| Permisos IAM | Policy con `secretsmanager:GetSecretValue` sobre el ARN |
| En GitHub Actions | `::add-mask::` para enmascarar en logs |

---

## Resumen del módulo T06

```
Código        → SAST (SonarCloud)         → vulnerabilidades en código fuente
Commits/PRs   → Secret Scanning (Gitleaks) → secretos hardcodeados en git
Imágenes      → Container Scan (Trivy)     → CVEs en OS y dependencias
Dependencias  → Dependency Scan (Dependabot/npm audit) → librerías vulnerables
IaC           → IaC Security (Checkov)     → misconfigurations en Terraform
App en runtime → DAST (OWASP ZAP)          → vulnerabilidades detectadas en ejecución
Secretos      → Secrets Manager            → rotación y acceso controlado
```

Cada capa cubre un vector de ataque distinto. Un pipeline DevSecOps completo integra todas.
