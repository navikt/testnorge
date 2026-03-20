Dev container setup specifically for dolly-backend.

The container opens at monorepo root (`/workspaces/testnorge`).

Run module builds from root, for example:

```bash
./gradlew :dolly-backend:build
```

Parity run scripts are available in `./.devcontainer`:

- PowerShell (`run-dolly-backend-parity.ps1`)
- Bash/macOS (`run-dolly-backend-parity.sh`)

These scripts are intended for a prod-like `dolly-backend` run in the dev container: they build `bootJar`, set `SPRING_PROFILES_ACTIVE`, and start the jar with the same JVM flags used in this setup.

On macOS, make the Bash script executable once:

```bash
chmod +x ./.devcontainer/run-dolly-backend-parity.sh
```

Default profile (`local`):

```powershell
./.devcontainer/run-dolly-backend-parity.ps1
bash ./.devcontainer/run-dolly-backend-parity.sh
```

Custom Spring profile:

```powershell
./.devcontainer/run-dolly-backend-parity.ps1 q2
bash ./.devcontainer/run-dolly-backend-parity.sh q2
```

