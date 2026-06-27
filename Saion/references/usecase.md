# UseCase

## Role

`UseCase` is the entry point for one application action.  
Upper layers call a `UseCase`. The `UseCase` delegates data work to `Repository`.

## Responsibilities

- Represent one action as one callable unit.
- Accept input for that action.
- Return a result that upper layers can use.
- Keep reusable domain behavior out of UI code.

## Dependency Rules

- Depend only on `Repository` interfaces.
- Do not reference UI, network, or storage implementations.
- Name each `UseCase` by intent, not by technical detail.

## Design Notes

- Use constructor injection for required repositories.
- Keep data source knowledge out of the caller.
- Keep one `UseCase` focused on one responsibility.
