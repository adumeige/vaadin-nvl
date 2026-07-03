# Migration Step 03: spring-boot 3.5.6 migration requirements

## Scope

- Plan: adumeige/vaadin-nvl · spring-boot 3.5.0->3.5.6 · 2026-07-03T13:21
- Step: 3 of 3
- Component: spring-boot
- Version: 3.5.5 -> 3.5.6
- Category: DEPENDENCY_CHANGED
- Kind: framework
- Depends on: spring-boot 3.5.5 migration requirements

## Objective

Update the repository so the codebase is compatible with spring-boot 3.5.6 for this migration step. Keep the change focused on the requirements listed below and avoid unrelated refactors.

## Implementation Plan

1. Inspect the repository for code, build files, configuration, tests, and documentation that reference the surfaces below.
2. Apply the required source, dependency, configuration, or test changes for this step.
3. Run the narrowest relevant verification available in the repository, then broaden only if the change touches shared behavior.
4. Commit only the files required for this step.

## Required Changes

- [3.5.6] DEPENDENCY_CHANGED: No specific breaking changes in 3.5.6 beyond bug fixes and dependency upgrades. Spring Boot 3.5.6 is a patch release from Maven Central with 43 bug fixes, documentation improvements, and dependency upgrades. No breaking API, configuration, or behavioral changes specific to this patch are documented in the evidence.
  - Search hint: patch: 3.5.6
  - Evidence: https://spring.io/blog/2025/09/18/spring-boot-3-5-6-available-now/

## Repository Search Hints

- covers 1 version
- 1 requirement
- patch: 3.5.6

## Primary Evidence

- https://spring.io/blog/2025/09/18/spring-boot-3-5-6-available-now/

## Done When

- The repository no longer contains code or configuration that violates the required changes for this step.
- Relevant tests or build checks pass, or any remaining failure is documented with the next concrete action.
- The change is committed as one focused migration step.
