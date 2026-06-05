# Spring Oracle Users API

API REST de usuários e endereços (relacionamento 1:N) construída com **Spring Boot** e **Oracle Database**, explorando os dois caminhos para a regra de negócio: na aplicação (Spring Data JPA) e no banco (views, procedures, triggers e packages PL/SQL).

> 📝 Este projeto é o código-fonte do artigo **"Regra de negócio no banco de dados: até que ponto faz sentido em 2026?"** — [link do artigo aqui]

## Stack

- Java 21
- Spring Boot 3.4 (Web, Data JPA, Validation)
- Oracle Database (ojdbc11)
- Maven

## O que o projeto demonstra

| Recurso | Onde |
|---|---|
| CRUD com Spring Data JPA e relacionamento `@OneToMany` / `@ManyToOne` | `model/`, `repository/` |
| DTOs com records e Bean Validation (`@Valid` em lista aninhada) | `dto/` |
| View Oracle mapeada como entidade somente leitura (`@Immutable`) | `vw_users_addresses` |
| Procedures e packages PL/SQL chamados via `SimpleJdbcCall` | `pkg_user`, `pkg_address` |
| Tradução de erros Oracle (`ORA-20001`, `ORA-20002`) para HTTP 404/400 | `repository/` + `GlobalExceptionHandler` |
| Trigger de auditoria registrando INSERT/UPDATE/DELETE | `trg_addresses_audit` |
| Handler global de exceções com status HTTP semânticos | `controller/GlobalExceptionHandler` |

## Estrutura

```
src/main/java/.../
 ├─ controller/    → UserController, AddressController, GlobalExceptionHandler
 ├─ service/       → UserService, AddressService
 ├─ repository/    → repositórios JPA + UserProcedureRepository, AddressProcedureRepository
 ├─ dto/           → UserRequest, UserResponse, AddressRequest, AddressResponse
 └─ model/         → User, Address, UserAddressView
sql/
 ├─ 01_tables.sql      → tabelas users, addresses e addresses_audit
 ├─ 02_views.sql       → vw_users_addresses
 ├─ 03_pkg_user.sql    → package de usuário (SPEC + BODY)
 ├─ 04_pkg_address.sql → package de endereço (SPEC + BODY)
 └─ 05_triggers.sql    → trg_addresses_audit
```

## Como rodar

### 1. Pré-requisitos

- Java 21+
- Maven 3.9+
- Oracle Database (local, Docker ou Oracle XE/Free)

Subir um Oracle Free via Docker, se precisar:

```bash
docker run -d --name oracle-free -p 1521:1521 \
  -e ORACLE_PASSWORD=senha123 \
  gvenzl/oracle-free
```

### 2. Banco de dados

Execute os scripts da pasta `sql/` **na ordem numérica** (no DBeaver, selecione o script inteiro e use `Alt+X` para executar blocos PL/SQL completos).

Confira se os objetos compilaram:

```sql
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type IN ('VIEW', 'PACKAGE', 'PACKAGE BODY', 'TRIGGER');
-- todos devem estar VALID
```

### 3. Configuração

Ajuste o `src/main/resources/application.yml` com as suas credenciais:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/FREEPDB1
    username: SEU_USUARIO
    password: SUA_SENHA
```

### 4. Subir a aplicação

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

## Endpoints

| Método | Rota | Descrição | Implementação |
|---|---|---|---|
| `POST` | `/api/users` | Cria usuário (com endereços opcionais) | procedure `pkg_user.create_user` |
| `GET` | `/api/users` | Lista usuários com endereços | JPA |
| `GET` | `/api/users/{id}` | Busca usuário por ID | JPA |
| `DELETE` | `/api/users/{id}` | Remove usuário e endereços | JPA (cascade) |
| `POST` | `/api/users/{userId}/addresses` | Adiciona endereço a um usuário | procedure `pkg_address.add_address` |
| `GET` | `/api/users/view` | Consulta achatada usuário+endereço | view `vw_users_addresses` |
| `GET` | `/api/users/view?city=...` | Mesma consulta filtrada por cidade | view + WHERE |

## Exemplos (curl)

**Criar usuário com endereços:**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "addresses": [
      {"street": "Rua das Flores", "numberAddress": "123",
       "neighborhood": "Centro", "city": "São Paulo",
       "state": "SP", "zipCode": "01000-000"}
    ]
  }'
```

**Adicionar endereço a usuário existente (via procedure):**

```bash
curl -X POST http://localhost:8080/api/users/1/addresses \
  -H "Content-Type: application/json" \
  -d '{"street": "Av. Paulista", "numberAddress": "900", "city": "São Paulo", "state": "SP"}'
```

**Consultar a view:**

```bash
curl "http://localhost:8080/api/users/view?city=São Paulo"
```

## Tratamento de erros

Os erros lançados pelas procedures (`RAISE_APPLICATION_ERROR`) são traduzidos na camada de repositório para exceções de domínio, que o handler global converte em respostas HTTP:

| Código Oracle | Significado | HTTP |
|---|---|---|
| `ORA-20001` | Usuário não encontrado | `404` |
| `ORA-20002` | E-mail já cadastrado | `400` |
| Validação de DTO (`@NotBlank`, `@Email`...) | Dados inválidos | `400` |

Exemplo de resposta de erro:

```json
HTTP 404
{ "error": "Usuário não encontrado: 999" }
```

## Auditoria

A trigger `trg_addresses_audit` registra automaticamente toda operação na tabela `addresses` — inclusive as feitas fora da API:

```sql
SELECT * FROM addresses_audit ORDER BY changed_at DESC;
```

## Licença

MIT — use, estude e adapte à vontade.
