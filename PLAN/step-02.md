# Migration Step 02: spring-boot 3.5.5 migration requirements

## Scope

- Plan: adumeige/vaadin-nvl · spring-boot 3.5.0->3.5.6 · 2026-07-03T13:21
- Step: 2 of 3
- Component: spring-boot
- Version: 3.5.1 -> 3.5.5
- Category: DEPENDENCY_CHANGED
- Kind: framework
- Depends on: spring-boot 3.5.1 migration requirements

## Objective

Update the repository so the codebase is compatible with spring-boot 3.5.5 for this migration step. Keep the change focused on the requirements listed below and avoid unrelated refactors.

## Implementation Plan

1. Inspect the repository for code, build files, configuration, tests, and documentation that reference the surfaces below.
2. Apply the required source, dependency, configuration, or test changes for this step.
3. Run the narrowest relevant verification available in the repository, then broaden only if the change touches shared behavior.
4. Commit only the files required for this step.

## Required Changes

- [3.5.5] NEW_FEATURE: Annotations to Register Filter and Servlet. New annotations `@RegisterFilter` and `@RegisterServlet` are introduced to simplify registration of servlets and filters. Migrate from manually defined `FilterRegistrationBean`/`ServletRegistrationBean` to these annotations where applicable.
  - Search hint: package: org.springframework.boot.web.servlet
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/
- [3.5.5] NEW_FEATURE: AsyncTaskExecutor auto-configuration. Custom AsyncTaskExecutor can now be auto-configured. If you use `@EnableAsync` with a custom executor, you may need to align with the new auto-configuration, or override the bean definition if you have an existing one.
  - Search hint: package: org.springframework.scheduling.annotation
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/
- [3.5.5] NEW_FEATURE: Auto-configuration for Bean Background Initialization. Beans can now be initialized lazily in background after the application context startup. Review bean initialization logic to opt-in for specific beans using `@BackgroundInitialization` annotation.
  - Search hint: package: org.springframework.boot.context
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/
- [3.5.5] NEW_FEATURE: Load Properties From Environment Variables. Application properties can now be loaded from environment variables using prefix patterns. Update your environment variable naming convention if you use externalized configuration.
  - Search hint: config: environment variables
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/
- [3.5.5] NEW_FEATURE: SSL Support for Service Connections. Service connections (e.g., database, Redis) now support SSL configuration natively. Update your application properties to include SSL settings if you connect to services over TLS.
  - Search hint: config: spring.*.ssl
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/
- [3.5.5] DEPENDENCY_CHANGED: Spring Boot dependency upgrades in 3.5. Dependency upgrades included in this release. Verify that all Spring dependencies and third-party libraries are compatible. Specifically, check if any deprecated method you used is now removed or behaves differently.
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/
- [3.5.5] BEHAVIORAL_CHANGE: Structured Logging format changes. Structured logging format and output have changed. Review your logging configuration files (e.g., logback-spring.xml) and adjust for the new structured format.
  - Search hint: config: logging.
  - Search hint: since: 3.5
  - Evidence: https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/

## Repository Search Hints

- covers 4 versions
- 7 requirements
- 3 prior quiet versions
- config: logging.
- since: 3.5

## Primary Evidence

- https://spring.io/blog/2025/05/22/spring-boot-3-5-0-available-now/

## Done When

- The repository no longer contains code or configuration that violates the required changes for this step.
- Relevant tests or build checks pass, or any remaining failure is documented with the next concrete action.
- The change is committed as one focused migration step.
