#!/usr/bin/env bash
set -euo pipefail

SPRING_PROFILE="${1:-local}"

if [ -d "/workspaces/testnorge/apps/dolly-backend" ]; then
  cd "/workspaces/testnorge/apps/dolly-backend"
else
  SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  cd "${SCRIPT_DIR}/../apps/dolly-backend"
fi

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILE}"

./gradlew bootJar

java -Duser.timezone=Europe/Oslo -Dreactor.bufferSize.small=10000 --add-opens java.base/java.lang=ALL-UNNAMED -jar build/libs/app.jar

