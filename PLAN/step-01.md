# Migration Step 01: spring-boot 3.5.1 migration requirements

## Scope

- Plan: adumeige/vaadin-nvl · spring-boot 3.5.0->3.5.6 · 2026-07-03T13:21
- Step: 1 of 3
- Component: spring-boot
- Version: 3.5.0 -> 3.5.1
- Category: BEHAVIORAL_CHANGE
- Kind: bump

## Objective

Update the repository so the codebase is compatible with spring-boot 3.5.1 for this migration step. Keep the change focused on the requirements listed below and avoid unrelated refactors.

## Implementation Plan

1. Inspect the repository for code, build files, configuration, tests, and documentation that reference the surfaces below.
2. Apply the required source, dependency, configuration, or test changes for this step.
3. Run the narrowest relevant verification available in the repository, then broaden only if the change touches shared behavior.
4. Commit only the files required for this step.

## Required Changes

- [3.5.1] BEHAVIORAL_CHANGE: Avoid Spring Boot 3.5.1 due to known regression. Spring Boot 3.5.1 contains a regression that was addressed in 3.5.3. The 3.5.3 release should be used instead of 3.5.1. Review your dependency version and upgrade directly to 3.5.3.
  - Search hint: component: spring-boot
  - Search hint: version: 3.5.1
  - Search hint: action: skip to 3.5.3
  - Evidence: https://spring.io/blog/2025/06/19/spring-boot-3-5-1-available-now/

## Repository Search Hints

- covers 1 version
- 1 requirement
- component: spring-boot
- version: 3.5.1

## Primary Evidence

- https://spring.io/blog/2025/06/19/spring-boot-3-5-1-available-now/

## Done When

- The repository no longer contains code or configuration that violates the required changes for this step.
- Relevant tests or build checks pass, or any remaining failure is documented with the next concrete action.
- The change is committed as one focused migration step.
