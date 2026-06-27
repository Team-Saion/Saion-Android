# Presentation

## Role

`Presentation` is the top application layer.  
It owns screens, `ViewModel`, UI state, UI effects, user intents, and navigation composition.

## Responsibilities

- Render UI from presentation state.
- Collect user input and convert it into actions.
- Dispatch actions to `ViewModel`.
- Call `UseCase` from `ViewModel`.
- Map domain results into UI state, UI effects, and navigation decisions.

## Dependency Rules

- Screens depend on presentation state and callbacks.
- `ViewModel` depends on `UseCase`, not on `Repository`, `DataSource`, `Networking`, or `LocalStorage`.
- Navigation composition belongs to presentation.
- Navigation infrastructure can live in shared core modules, but feature flow decisions stay in presentation.
- Presentation can react to global UI events, but it must not own domain policy.

## Structure In This Repo

- App-level presentation lives in `app/ui` and `app/viewmodel`.
- Feature-level presentation lives in feature `Screen` files and feature navigation entry builders.
- Shared presentation primitives live in `core/ui/viewmodel`.

## Prohibited

- Do not access `Repository` or `DataSource` directly from screens or `ViewModel`.
- Do not reference network or storage implementations from presentation.
- Do not place business-rule ownership inside composables.
- Do not push navigation side effects into data or infrastructure layers.

## Design Notes

- Screens should render state and emit actions.
- `ViewModel` should coordinate presentation logic and call `UseCase`.
- Shared `BaseViewModel` patterns should keep state, effect, and intent flow consistent across screens.
