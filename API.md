# API Conecta

Contrato atual da API REST para integração com o frontend React Native.

## 1. Acesso

- **Base URL local:** `http://localhost:8080/api`
- **Celular físico:** `http://<IP-DO-COMPUTADOR>:8080/api`
- **Emulador Android:** normalmente `http://10.0.2.2:8080/api`
- O servidor escuta em `0.0.0.0:8080`.
- As respostas e os nomes dos campos usam JSON em `camelCase`.
- As rotas abaixo, exceto cadastro e login, exigem:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

O token é retornado por `register` e `login`. Ele expira em 43.200 segundos
(12 horas); não existe endpoint de refresh. Quando expirar, faça login
novamente. No desenvolvimento, se `JWT_SECRET` não estiver configurado, o
segredo muda ao reiniciar o backend e os tokens anteriores deixam de valer.

## 2. Resumo das rotas

| Método | Rota | Auth | Descrição | Sucesso |
|---|---|---:|---|---:|
| POST | `/auth/register` | Não | Cria a única conta do responsável | `201` |
| POST | `/auth/login` | Não | Autentica e retorna JWT | `200` |
| GET | `/dashboard?periodo={periodo}` | Sim | Retorna o dashboard da conta autenticada | `200` |
| GET | `/clientes` | Sim | Lista clientes em ordem alfabética | `200` |
| GET | `/produtos` | Sim | Lista produtos ativos | `200` |
| GET | `/produtos/{id}` | Sim | Detalha um produto ativo | `200` |
| POST | `/produtos` | Sim | Cadastra produto ativo | `201` |
| GET | `/vendas` | Sim | Lista vendas mais recentes primeiro | `200` |
| POST | `/vendas` | Sim | Cria venda, baixa estoque e registra movimentação | `201` |

Não há atualmente rotas de atualização ou exclusão (`PUT`, `PATCH` ou
`DELETE`).

## 3. Autenticação

### POST `/auth/register`

Cria a conta do responsável e já retorna um token. O backend permite somente
uma conta: depois que qualquer usuário existe, novas tentativas retornam
`409 Conflict`.

**Request**

```json
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "telefone": "(11) 99999-9999",
  "senha": "Senha-segura-123",
  "negocio": "Doces da Maria"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---:|---|
| `nome` | string | Sim | Não vazia; até 100 caracteres |
| `email` | string | Sim | E-mail válido; até 100 caracteres |
| `telefone` | string | Sim | De 10 a 20 caracteres |
| `senha` | string | Sim | De 8 a 72 caracteres; máximo efetivo de 72 bytes UTF-8; pelo menos uma letra maiúscula, uma letra minúscula e um número |
| `negocio` | string | Sim | Não vazia; até 150 caracteres |

**Response `201 Created`**

```json
{
  "tokenType": "Bearer",
  "accessToken": "<jwt>",
  "expiresIn": 43200,
  "usuario": {
    "id": 1,
    "nome": "Maria Silva",
    "email": "maria@exemplo.com",
    "negocio": "Doces da Maria"
  }
}
```

O e-mail é normalizado com `trim` e convertido para minúsculas. Se o cadastro
já tiver sido feito, a API retorna `409` com a mensagem informando que a conta
do responsável já existe.

### POST `/auth/login`

**Request**

```json
{
  "email": "maria@exemplo.com",
  "senha": "Senha-segura-123"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---:|---|
| `email` | string | Sim | E-mail válido; até 100 caracteres |
| `senha` | string | Sim | Não vazia; até 72 caracteres e 72 bytes UTF-8 |

**Response `200 OK`**

Usa o mesmo formato de resposta do cadastro.

E-mail ou senha incorretos retornam `401 Unauthorized` com a mensagem
`E-mail ou senha inválidos.`. O frontend deve tratar credenciais inválidas
sem diferenciar qual dos dois campos falhou.

## 4. Dashboard

### GET `/dashboard?periodo={periodo}`

Requer JWT. Os períodos disponíveis são `hoje`, `semana` e `mes`.

Exemplo:

```http
GET /api/dashboard?periodo=hoje
Authorization: Bearer <jwt>
```

**Response `200 OK`**

```json
{
  "usuario": {
    "nome": "Maria Silva",
    "negocio": "Doces da Maria"
  },
  "recebido": {
    "valor": 1250.00,
    "meta": 3000.00,
    "pedidos": 12
  },
  "alertas": [
    {
      "tipo": "ESTOQUE_BAIXO",
      "quantidade": 3
    }
  ],
  "proximasEntregas": [
    {
      "id": "PED-001",
      "nome": "Cliente",
      "dia": "Hoje",
      "hora": "14:00"
    }
  ],
  "metricas": {
    "lucro": 800.00,
    "custo": 450.00,
    "investido": 200.00
  },
  "lucroMensal": [
    {
      "mes": "Jan",
      "valor": 800.00
    }
  ]
}
```

Os valores e listas do dashboard são dados de demonstração armazenados em
tabelas próprias; não são calculados em tempo real a partir das vendas.
`periodo` ausente ou diferente de `hoje`, `semana` e `mes` resulta em `400`.

## 5. Clientes

### GET `/clientes`

Requer JWT. Não recebe parâmetros.

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "nome": "Ana Souza",
    "telefone": "(11) 98888-8888",
    "email": "ana@example.test"
  }
]
```

A lista é ordenada pelo nome, ignorando maiúsculas/minúsculas. Não existe
endpoint para criar, editar ou excluir clientes nesta versão.

## 6. Produtos

### GET `/produtos`

Requer JWT e não recebe parâmetros.

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "nome": "Brigadeiro",
    "descricao": "Brigadeiro tradicional",
    "preco": 3.50,
    "categoria": "Doces",
    "estoque": 20
  }
]
```

Somente produtos ativos são retornados, ordenados pelo nome. O campo
`precoCusto` não é retornado nas consultas.

### GET `/produtos/{id}`

Requer JWT. Retorna o mesmo objeto de produto do endpoint de listagem.

Produto inexistente ou inativo resulta em `404` com a mensagem
`Produto não encontrado`.

### POST `/produtos`

Requer JWT.

**Request**

```json
{
  "nome": "Brigadeiro",
  "descricao": "Brigadeiro tradicional",
  "precoCusto": 1.20,
  "preco": 3.50,
  "categoria": "Doces",
  "estoque": 20
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---:|---|
| `nome` | string | Sim | Não vazia |
| `descricao` | string | Não | Pode ser nula |
| `precoCusto` | number | Sim | Maior ou igual a `0` |
| `preco` | number | Sim | Maior ou igual a `0.01` |
| `categoria` | string | Sim | Não vazia |
| `estoque` | integer | Sim | Maior ou igual a `0` |

**Response `201 Created`**

Retorna o produto criado no mesmo formato da listagem. O estoque inicial é o
valor enviado e o produto começa ativo.

## 7. Vendas

### GET `/vendas`

Requer JWT e não recebe parâmetros.

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "cliente": "Ana Souza",
    "data": "2026-10-28T13:20:21",
    "total": 14.00,
    "status": "CONCLUIDA",
    "formaPagamento": "PIX"
  }
]
```

As vendas são retornadas da mais recente para a mais antiga. `data` é uma
data/hora ISO-8601 sem indicação de fuso no formato serializado pelo backend.

### POST `/vendas`

Requer JWT.

**Request**

```json
{
  "clienteId": 1,
  "formaPagamento": "PIX",
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    }
  ]
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---:|---|
| `clienteId` | integer | Sim | Deve existir |
| `formaPagamento` | string | Sim | `PIX`, `DINHEIRO`, `CARTAO` ou `FIADO` |
| `itens` | array | Sim | Não vazio |
| `itens[].produtoId` | integer | Sim | Deve existir e estar ativo |
| `itens[].quantidade` | integer | Sim | Inteiro maior que `0` |

**Response `201 Created`**

Retorna a venda criada no formato usado pela listagem:

```json
{
  "id": 2,
  "cliente": "Ana Souza",
  "data": "2026-10-28T13:20:21",
  "total": 7.00,
  "status": "CONCLUIDA",
  "formaPagamento": "PIX"
}
```

O total é calculado no backend com o preço atual de venda dos produtos. A
venda é criada com status `CONCLUIDA`, reduz o estoque e registra uma
movimentação financeira. Se o mesmo `produtoId` aparecer mais de uma vez, as
quantidades são somadas antes da validação e da baixa de estoque.

Possíveis falhas de negócio:

| Status | Situação |
|---|---|
| `404` | Cliente ou produto não encontrado |
| `400` | Produto inativo |
| `400` | Estoque insuficiente |
| `400` | Corpo inválido, item ausente ou enum `formaPagamento` inválido |

## 8. Erros e validação

Não há um `@ControllerAdvice` customizado no projeto. Portanto, erros de
validação do `@Valid`, JSON malformado, enum inválido e autenticação podem usar
o corpo padrão de erro do Spring Boot, que pode variar conforme a versão e
configuração. O frontend deve usar principalmente o status HTTP e apresentar
uma mensagem de fallback quando o corpo não tiver um campo de mensagem.

Comportamento mínimo esperado:

| Status | Tratamento no frontend |
|---|---|
| `400` | Corrigir dados enviados ou mostrar a mensagem de negócio |
| `401` | Limpar o token e voltar para login |
| `403` | Tratar como acesso não autorizado |
| `404` | Informar que o recurso não existe |
| `409` | Mostrar conflito, principalmente no cadastro único |
| `5xx` | Mostrar indisponibilidade temporária e permitir tentar novamente |

## 9. React Native: configuração

Não fixe `localhost` no app quando ele for executado em um celular físico.
Defina a base URL por ambiente/configuração:

```ts
const API_BASE_URL = "http://192.168.0.10:8080/api";
```

O celular e o computador precisam estar na mesma rede Wi-Fi, e a porta TCP
8080 precisa estar acessível no firewall do computador. O backend aceita
origens locais e redes privadas de desenvolvimento via CORS; CORS não
substitui o header `Authorization` nem deve ser interpretado como mecanismo de
autenticação.
