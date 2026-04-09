# AGENTS Guide for testnorge

## Big picture
- Monorepo is a Gradle composite build; root `settings.gradle` auto-includes builds from `apps/`, `libs/`, `mocks/`, `plugins/`, `proxies/`, `xsd/`.
- Root `build.gradle` fans out `build`, `clean`, `assemble`, `sonarqube`, and `dollyValidation` to included builds.
- `apps/` are deployables, `libs/` are shared modules, `proxies/` are integration gateways; follow this flow as a baseline: `apps/dolly-frontend` -> `apps/dolly-backend` -> consumers in `apps/dolly-backend/src/main/resources/application.yml`.
- Runtime boundaries are codified in NAIS manifests (`config.yml`, `config.test.yml`) through `accessPolicy`, ingresses, and secrets.

## How these instructions work together
- `AGENTS.md` covers repo structure, build/test workflow, architecture, and operational constraints for this monorepo.
- `.github/copilot-instructions.md` covers code style, implementation patterns, and language-specific expectations.
- Use `AGENTS.md` first to understand where and how to change the repo, then apply `.github/copilot-instructions.md` when writing or refactoring code.

## Build and validation workflow
- Day-to-day development is usually run from the specific app or proxy directory rather than repo root.
- Before running shell commands, determine the developer OS from explicit environment metadata if available; otherwise probe once using the active shell, and if still unclear provide both PowerShell and bash/zsh variants instead of guessing.
- PowerShell:
```powershell
Set-Location apps/dolly-backend
./gradlew build
```
- bash/zsh:
```bash
cd apps/dolly-backend
./gradlew build
```
- `build` normally runs `dollyValidation` for apps and proxies, so `dollyValidation` does not need to be executed explicitly during normal local development.
- `dollyValidation` (`plugins/java/src/main/java/no/nav/dolly/plugins/DollyBuildValidationTask.java`) enforces consistency between:
  - module `build.gradle` dependencies on `no.nav.testnav.libs:*`
  - module `settings.gradle` `includeBuild ../../libs/<lib>` entries
  - `.github/workflows/app.<name>.yml` / `proxy.<name>.yml` `on.push.paths`
- When adding/removing shared libs, update all three files above in the same PR.
- `iTest` exists for supported modules, but is not normally run locally during day-to-day development.
- On macOS, tests or local runs that rely on Docker/Testcontainers may require the environment variables documented in root `README.md`.

## Comment style
- Do not add code comments unless asked, and do not remove or modify existing comments.
- If an agent adds code comments, write them as complete sentences and terminate them with appropriate punctuation (normally `.`).

## Backend, proxy, and frontend patterns
- Shared service plugins are `dolly-apps` and `dolly-proxies` (`plugins/java/src/main/groovy`); they standardize Java 21, WebFlux + Undertow, test setup, and artifact packaging.
- Integrations are config-driven: app consumers live in `application.yml` (`consumers`), network permissions in `config.yml` (`accessPolicy.inbound/outbound`).
- Cross-tenant proxy cases (NAV + Trygdeetaten) use two `AzureAdApplication` resources and dual secrets; reference `proxies/dolly-proxy/config.yml`.
- Frontend source is under `apps/*/src/main/js`; build path is pnpm/Vite first, then Gradle packaging (`.github/workflows/common.workflow.frontend.yml`).
- `apps/dolly-frontend` uses mixed Redux + Router + SWR (`src/main/js/src/Store.tsx`, `src/main/js/src/RootComponent.tsx`); extend existing state patterns instead of introducing new ones.
- Frontend prefers NAV design system components (`@navikt/ds-react`, `@navikt/ds-css`) and reuse of existing UI components before introducing new ones.
- API utilities are centralized in `apps/dolly-frontend/src/main/js/src/api/index.ts`; MSW is only enabled in test mode via `apps/dolly-frontend/src/main/js/src/index.tsx`.
- Frontend tests use Vitest, React Testing Library, MSW, and Playwright.

## CI/CD and ops details
- App/proxy workflows delegate to reusable workflows in `.github/workflows/common.workflow.*.yml`.
- Non-master deploys are commit-message driven (`#deploy-...`, `#deploy-test-...`), and `#nodeploy` disables deploy.
- Frontend module commands.
- PowerShell:
```powershell
Set-Location apps/dolly-frontend/src/main/js
pnpm install --frozen-lockfile
pnpm start
pnpm run test:vitest-run
pnpm run test:playwright-run
```
- bash/zsh:
```bash
cd apps/dolly-frontend/src/main/js
pnpm install --frozen-lockfile
pnpm start
pnpm run test:vitest-run
pnpm run test:playwright-run
```
- Private npm packages require a GitHub token in user `.npmrc` (`apps/dolly-frontend/README.md`).
- Proxy table in `docs/modules/ROOT/pages/index.adoc` is generated; regenerate after proxy config changes.
- PowerShell:
```powershell
python .\scripts\generate_proxy_table.py
```
- bash/zsh:
```bash
python ./scripts/generate_proxy_table.py
```




