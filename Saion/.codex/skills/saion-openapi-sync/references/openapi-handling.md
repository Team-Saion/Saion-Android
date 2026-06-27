# OpenAPI Handling

Use this guide to map the Saion OpenAPI spec into this Android project.

## Source of Truth

- primary source: `https://dev.saion.app/api/api-specs`
- format: OpenAPI 3.1 JSON
- known tags at time of writing include:
  - `Auth API`
  - `Member API`
  - `Term API`

If network access is blocked, request approval and fetch the raw spec. Do not infer contract details from the Swagger UI HTML.

## What to Extract

For each target operation, extract:

- `tag`
- `path`
- HTTP method
- `operationId`
- `summary`
- request body schema and content type
- success response schema
- auth requirement from global or operation-level `security`
- notable error codes only when they affect app behavior

Ignore verbose example payloads unless they clarify nullability or field shape.

## Mapping Rules

### Service placement

- map OpenAPI tag to a service class
- prefer existing service classes:
  - `Auth API` -> `AuthService`
  - `Member API` -> `MemberService`
  - `Term API` -> create `TermService` if needed

### Transport model placement

- request models: `core/network/model/<domain>/...Request.kt`
- response models: `core/network/model/<domain>/...Response.kt`
- keep one model per top-level transport concept
- prefer descriptive names over endpoint names when the schema is reusable

### Domain placement

- repository interface: `core/domain/repository`
- repository impl: `core/data/repository`
- use case: `core/domain/usecase/<domain>`
- domain model: `core/model/<domain>` only when upper layers need a stable app-facing model

### Return shape

- service returns `ApiResponse<WireType>`
- remote data source returns `ApiResponse<WireType>`
- repository returns `AppResult<DomainType>`
- use case returns whatever the repository returns

## Special Cases

### `Unit` success bodies

If the response envelope succeeds with `data: null`, keep the service generic as `ApiResponse<Unit>` and let `safeRequest` handle success.

### List responses

If `data` is a JSON array, model it as `ApiResponse<List<ResponseType>>` and map to the needed domain list type in the repository.

### Multipart

If content type is `multipart/form-data`:

- keep file or part construction in `network`
- keep the domain API free of Ktor multipart types
- expose a domain-friendly repository method signature

### Auth

Saion currently uses a bearer auth plugin in `HttpClientFactory`.

- auth endpoints under `/api/v1/auth` are treated specially
- verify whether a new public endpoint sits outside auth paths and should remain unauthenticated
- if the matching rule in `isAuthRequest()` becomes insufficient, update it carefully and keep the behavior explicit

## Sync Strategy

When comparing current code to spec, prefer this order:

1. search for existing path or intent
2. modify existing code if it clearly represents the same backend operation
3. add missing models or methods only when no suitable implementation exists
4. add Hilt bindings only when introducing a new type
5. add tests after the code path is complete through `UseCase`

Avoid duplicate APIs caused by minor name differences. Path and behavior matter more than current symbol names.
