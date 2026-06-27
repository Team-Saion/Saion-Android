# Saion Project Patterns

Use these patterns as the default implementation style for this repository.

## Layer Mapping

- `core/network`
  - owns Ktor request code
  - owns wire request and response models
  - exposes service classes and remote data sources
- `core/data`
  - implements domain repositories
  - calls `safeRequest`
  - converts wire responses into domain results
- `core/domain`
  - owns repository interfaces
  - owns use cases
- `core/model`
  - owns domain-facing models and result types

## Existing Network Pattern

`core/network/api/MemberService.kt`

- one class per tag grouping
- one method per endpoint
- request body built with `setBody(...)`
- returns `ApiResponse<T>` through `toApiResponse()`

`core/network/datasource/MemberRemoteDataSource.kt`

- interface mirrors service capability
- no business logic

`core/network/datasource/DefaultMemberRemoteDataSource.kt`

- thin service delegation
- constructor-injected service

## Existing Repository Pattern

`core/data/repository/MemberRepositoryImpl.kt`

- call `safeRequest(request = { ... })`
- convert transport response to `AppResult.Success(...)`
- perform source coordination inside the repository
- keep local side effects such as token clearing here, not in `network`

`core/data/repository/AuthRepositoryImpl.kt`

- persist tokens in repository after successful auth response
- return `AppResult<Unit>` upward when upper layers do not need the raw token model

## Error Handling

`core/data/util/SafeRequest.kt`

- `401` maps to `AppError.Unauthorized()`
- `4xx` with `errorCode` maps to `BusinessErrorType` when possible
- `5xx` maps to `AppError.ServerUnavailable()`
- `IOException` maps to `AppError.NetworkUnavailable`
- `SocketTimeoutException` maps to `AppError.Timeout`

Keep these semantics unchanged unless the project already changed them elsewhere.

## Serialization Pattern

`core/network/model/common/BaseResponse.kt`

- backend envelope shape is:
  - `isSuccess`
  - `data`
  - `errorCode`
  - `message`
  - `timestamp`

`core/network/model/common/ApiResponse.kt`

- Ktor response is decoded into `BaseResponse<T>`
- `statusCode` is carried separately from the JSON body

All wire models should use `@Serializable`. Add `@SerialName` when the JSON field name is not a direct Kotlin property match or when you want to keep consistency with existing code.

## DI Pattern

`core/network/di/NetworkModule.kt`

- provide each service as a singleton from the shared `HttpClient`

`core/network/di/DataSourceModule.kt`

- bind each `Default*RemoteDataSource` to its `*RemoteDataSource` interface

`core/data/di/DataModule.kt`

- bind each `*RepositoryImpl` to its domain repository interface

If the new endpoint fits an existing service or repository, extend the existing type rather than introducing a new one.

## Use Case Pattern

Use cases are simple constructor-injected classes with `operator fun invoke(...)`.

Examples:

- `GetMyInfoUseCase`
- `CompleteNicknameUseCase`
- `LoginWithKakaoUseCase`

Name the use case by user intent, not transport details.
