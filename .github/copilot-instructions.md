## Purpose

Generate code for a Spring Boot + Gradle + Java backend and React (TypeScript) frontend.

## General Style

- IMPORTANT: Prefer obvious code over clever code.
- IMPORTANT: DO NOT ADD ANY COMMENTS unless asked, but allow other users to add comments without interfering.
- Provide entire files instead of snippets, unless specified.
- use descriptive variable names unless in a loop or other obvious context.

## Java

- Java: Spring Boot 3.x, Java 21, unless otherwise specified.
- Java tests: JUnit 5, AssertJ, Mockito; name test methods: `should<Behavior>()`.
- Null handling: prefer `Optional` only at API boundaries.
- Keep functions small; extract private helpers.
- use isBlank/isNotBlank from Apache Commons Lang3.
- use isNull from java.util.Objects.
- Logging: SLF4J, no `System.out`.
- Avoid deprecated Spring APIs.
- Always use explicit, fully qualified Java imports.
- Never use wildcard imports (e.g., `import com.example.package.*`).
- Ensure all necessary classes are imported individually.

## REST

- Controllers: slim, delegate most functionality to service layer.
- Return meaningful HTTP status codes.
- Use DTOs, not entities, at the edge.

## JS / React / TS:

- Javascript: React 18, TypeScript 5, React Router, React Hook Form, useSWR, Styled components, functional components,
  hooks unless otherwise specified.
- Javascript tests: MSW, React Testing Library, Vitest, Playwright for e2e.
- TypeScript interfaces should be explicit for props
- Functional components, hooks only.
- Components should be:
    - Self-contained and reusable where possible
    - Focused on a single responsibility
    - Well-typed with TypeScript
- Prefer using useSWR for data fetching and create a custom hook.
- Prefer early returns for conditionals
- TypeScript strict mode assumptions.
- Uses NAV design system (@navikt/ds-react and @navikt/ds-css)
- Prefer NAV design system components and styling where possible
- Keep components small and focused
- Follow accessibility best practices
- Reuse existing UI components; do not introduce new state libs
- Prefer creating .tsx files for components, not .ts
- Export components directly where they are written, never in dedicated export/index files.

## Error Handling React / TS:

- Use error boundaries for component errors
- Handle API errors gracefully
- Provide meaningful error messages to users

## What NOT to do

- Do not invent APIs not present.
- Do not add libraries without minimal justification comment.
