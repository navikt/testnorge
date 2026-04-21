## Purpose

Generate code for a Spring Boot + Gradle + Java backend and React (TypeScript) frontend.

## General Style

- IMPORTANT: Prefer obvious code over clever code.
- IMPORTANT: Follow the touched module's existing conventions before applying a global preference.
- IMPORTANT: DO NOT ADD ANY COMMENTS unless asked, and DO NOT REMOVE or MODIFY existing comments in the code.
- use descriptive variable names unless in a loop or other obvious context.

## Java

- Java: Spring Boot 4.x, Java 21, unless otherwise specified.
- Java tests: JUnit 5, AssertJ, Mockito; name test methods: `should<Behavior>()`.
- Null handling: prefer `Optional` only at API boundaries.
- Keep functions small; extract private helpers.
- IMPORTANT: Prefer `isNull(obj)` and `nonNull(obj)` from `java.util.Objects` when adding or refactoring null checks, especially where the surrounding code already follows that pattern.
- IMPORTANT: Prefer Apache Commons Lang3 `isBlank(str)` and `isNotBlank(str)` for blank-string checks when the module already uses Commons Lang.
- Logging: SLF4J, no `System.out`.
- Avoid deprecated Spring APIs.
- Prefer explicit Java imports and avoid wildcard imports in new or modified code.

## REST

- Controllers: slim, delegate most functionality to service layer.
- Return meaningful HTTP status codes.
- Use DTOs, not entities, at the edge.

## JS / React / TS:

- Javascript: React 19, TypeScript 5, React Router, React Hook Form, useSWR, Styled components, functional components,
  hooks unless otherwise specified.
- JavaScript tests: MSW, React Testing Library, Vitest, Playwright for e2e.
- TypeScript interfaces should be explicit for props
- Functional components, hooks only.
- Components should be:
    - Self-contained and reusable where possible
    - Focused on a single responsibility
    - Well-typed with TypeScript
- Prefer existing useSWR + custom hook patterns for frontend read-fetching, but follow the local data-flow pattern in the touched area.
- Prefer early returns for conditionals
- TypeScript strict mode assumptions.
- Uses NAV design system (@navikt/ds-react and @navikt/ds-css)
- Prefer NAV design system components and styling where the touched frontend already uses them.
- Keep components small and focused
- Follow accessibility best practices
- Reuse existing UI components; do not introduce new state libs
- Prefer existing frontend request helpers, custom hooks, and UI patterns before introducing parallel abstractions.
- Prefer creating .tsx files for components, not .ts
- Prefer following the local folder convention for component exports; use direct exports unless the touched area already uses index/barrel files.

## Error Handling React / TS:

- Use error boundaries for component errors
- Handle API errors gracefully
- Provide meaningful error messages to users

## Terminal Usage

- Chain multiple commands together using `&&` or `;` instead of running separate terminal commands
- Use background processes (`isBackground=true`) for long-running tasks, then check output later
- Minimize the number of terminal sessions created - prefer reusing existing sessions

## What NOT to do

- Do not invent APIs not present.
- Do not add libraries unless existing repo dependencies or utilities are insufficient.
