---
name: saion-openapi-sync
description: Compare Saion Android's current API implementation against the Saion OpenAPI spec and add or update code through the UseCase layer. Use when Codex needs to inspect `https://dev.saion.app/api/api-specs`, detect missing or changed endpoints, and reflect them in this project's `network`, `data`, and `domain` layers while preserving the architecture `Presentation -> UseCase -> Repository -> DataSource -> Networking/LocalStorage`.
---

# Saion OpenAPI Sync

Use this skill to sync the Android project with the Saion backend contract.

Prefer `https://dev.saion.app/api/api-specs`. Do not use `swagger-ui/index.html` as the source of truth because it is only the Swagger UI shell and does not contain the full contract.

## Workflow

1. Fetch the raw OpenAPI JSON from `https://dev.saion.app/api/api-specs`.
2. Narrow the task to the relevant `tag`, `path`, or `operationId`.
3. Search the repo for existing implementations before adding anything.
4. Compare the spec to the current code and classify each difference:
   - endpoint missing entirely
   - request model changed
   - response model changed
   - HTTP method or path changed
   - auth or content type changed
   - implementation exists but stops short of `UseCase`
5. Apply changes in this order:
   - `core/network/model`
   - `core/network/api`
   - `core/network/datasource`
   - `core/network/di`
   - `core/model` only if a domain model is required and no existing one fits
   - `core/domain/repository`
   - `core/data/repository`
   - `core/data/di`
   - `core/domain/usecase`
6. Add or update tests for use case behavior only. Do not add DataSource or Repository tests, and remove any related tests introduced as part of the sync work.
7. Run targeted Gradle validation for affected modules.

## Project Rules

- Preserve the layer flow: `Presentation -> UseCase -> Repository -> DataSource -> Networking/LocalStorage`.
- Do not call `Service`, `DataSource`, or `DataStore` from `Presentation`.
- Do not make domain decisions in `network` or `datastore`.
- Keep repository interfaces in `core/domain`.
- Keep repository implementations in `core/data`.
- Keep transport request and response models in `core/network/model`.
- Finish transport-to-domain conversion in the repository boundary.

## Repo Pattern

Read these files before editing:

- `references/saion-project-patterns.md`
- `references/openapi-handling.md`

Current examples worth mirroring:

- `core/network/api/AuthService.kt`
- `core/network/api/MemberService.kt`
- `core/network/datasource/MemberRemoteDataSource.kt`
- `core/network/datasource/DefaultMemberRemoteDataSource.kt`
- `core/data/repository/AuthRepositoryImpl.kt`
- `core/data/repository/MemberRepositoryImpl.kt`
- `core/domain/usecase/member/GetMyInfoUseCase.kt`

## Implementation Rules

- Group service classes by OpenAPI tag. Extend an existing service for the same tag instead of creating a second service for that tag.
- Keep service methods thin. They should only build the request and return `ApiResponse<T>`.
- Keep remote data sources as one-step abstractions over services.
- Use `safeRequest` in repositories to convert `ApiResponse<T>` into `AppResult<T>`.
- Return `AppResult<Unit>` for write operations that do not expose a useful payload upward.
- Reuse existing domain models if the new response only needs a subset already represented in `core/model`.
- If the backend returns a richer transport model than the app currently needs, keep the full wire model in `core/network/model` and map only the needed fields into the domain layer.
- If the endpoint is auth-free, verify that the existing `HttpClientFactory.isAuthRequest()` behavior does not accidentally force bearer auth. Update it only when the spec requires it.
- For multipart endpoints, keep binary or form-data specifics in `network`; do not leak them into `domain`.
- Write tests only for `core/domain/usecase`.
- Do not create `core/data` repository tests or `core/network/datasource` tests for this sync workflow.
- If this sync work previously added repository or data source tests, remove them as part of the change.
- Name test functions in Korean using backticks.
- If a test function contains multiple assertions, wrap them with `assertAll`, `softly`, or an equivalent grouped-assertion style instead of leaving standalone repeated assertions.

## Suggested Commands

- Fetch and inspect the spec:
  - `./.codex/skills/saion-openapi-sync/scripts/fetch_openapi_spec.sh`
  - `python3 ./.codex/skills/saion-openapi-sync/scripts/inspect_openapi.py --help`
- Search current implementation:
  - `rg -n "operationName|path segment|Repository|UseCase|RemoteDataSource" core`
- Validate:
  - `./gradlew :core:network:compileDebugKotlin :core:data:compileDebugKotlin :core:domain:compileKotlin`
  - run targeted `core:domain:test` tasks for touched use cases

## Acceptance Checklist

- OpenAPI source was `api-specs`, not Swagger UI HTML.
- Every changed endpoint is reflected through `UseCase`.
- Hilt providers and binds are updated if a new service, data source, or repository type was introduced.
- No DataSource or Repository tests were added or left behind by this sync work.
- Use case tests cover delegation for the new or changed action.
- Use case test names are written in Korean, and multi-assert tests use grouped assertions.
- Gradle validation completed for affected modules.
