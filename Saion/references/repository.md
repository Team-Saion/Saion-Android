# Repository

## Role

`Repository` is the boundary between the domain layer and the data-access layer.  
Keep interfaces in the domain layer. Keep implementations in the data layer.

## Responsibilities

- Define data access contracts for the domain layer.
- Combine remote and local data when needed.
- Convert lower-layer outputs into results for upper layers.
- Normalize failure output before returning it upward.

## Dependency Rules

- Put interfaces in `domain`.
- Put implementations in `data`.
- Make upper layers depend on interfaces only.
- Limit data source coordination to `Repository`.

## Design Notes

- Finish source selection inside `Repository`.
- Finish model conversion at the `Repository` boundary when possible.
- Do not leak lower-layer structure to upper layers.
