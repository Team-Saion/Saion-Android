# DataSource

## Role

`DataSource` encapsulates access to external systems.  
`Repository` uses `DataSource` to reach remote APIs, local storage, or other infrastructure.

## Responsibilities

- Keep one access path in one place.
- Provide low-level read, write, and request operations.
- Prevent upper layers from depending on transport or storage technology.

## Types

- `RemoteDataSource`: communicates with remote systems.
- `LocalDataSource`: reads from and writes to device-local storage.

## Dependency Rules

- Place `DataSource` below `Repository`.
- Do not reference `DataSource` directly from `UseCase` or UI layers.
- Let `Repository` choose and combine data sources.

## Design Notes

- Hide the access mechanism and expose only the needed operations.
- Do not own domain policy or UI policy.
