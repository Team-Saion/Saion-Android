# Project Architecture Guide

Use this layer flow as the default rule:
`Presentation -> UseCase -> Repository -> DataSource -> Networking/LocalStorage`

## Layer Rules

- `Presentation` handles user input, UI state, and navigation triggering.
- `UseCase` represents one application action and depends only on `Repository`.
- `Repository` defines the domain boundary and combines one or more `DataSource` implementations.
- `DataSource` hides external access details.
- `Networking` owns remote communication structure.
- `LocalStorage` owns local persistence structure.

## Dependency Direction

- Higher layers must not depend on lower-layer implementation details.
- `domain` must not know `data`, `network`, or `datastore` implementations.
- `data` implements `domain` contracts and coordinates `network` and `datastore`.
- `network` and `datastore` each keep a single infrastructure responsibility.

## Prohibited

- Do not call `DataSource`, `Service`, or `DataStore` directly from `Presentation`.
- Do not reference `Networking` or `LocalStorage` implementations from `UseCase`.
- Do not combine data sources, convert models, or choose storage outside `Repository`.
- Do not make domain decisions inside `Networking` or `LocalStorage`.

## References

- [Presentation](references/presentation.md)
- [UseCase](references/usecase.md)
- [Repository](references/repository.md)
- [DataSource](references/datasource.md)
- [Networking](references/networking.md)
- [LocalStorage](references/localstorage.md)
