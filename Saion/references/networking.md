# Networking

## Role

`Networking` owns the remote communication layer.

## Responsibilities

- Provide access paths for external APIs.
- Own transport models for requests and responses.
- Handle transport concerns such as authentication, headers, serialization, and client configuration.
- Prevent upper layers from depending on protocol or client details.

## Dependency Rules

- Expose networking through `RemoteDataSource`, not directly to `Repository`.
- Do not own domain models or domain policy.
- Keep business decisions out of the networking layer.

## Design Notes

- Keep this layer focused on communication structure.
- Keep policy decisions above this layer.
