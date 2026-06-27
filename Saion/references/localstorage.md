# LocalStorage

## Role

`LocalStorage` owns the device-local persistence layer.

## Responsibilities

- Provide read, write, and delete paths for local state.
- Store persistent data such as session state, tokens, and cache.
- Prevent upper layers from depending on storage technology.

## Dependency Rules

- Expose local persistence through `LocalDataSource`.
- Keep file format and storage mechanism details inside this layer.
- Keep business decisions out of the local storage layer.

## Design Notes

- Keep this layer replaceable.
- Keep storage details hidden from upper layers.
