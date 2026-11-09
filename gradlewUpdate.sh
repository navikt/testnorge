#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

gradle wrapper

for wrapper_dir in */*/gradle/wrapper; do
  cp gradle/wrapper/gradle-wrapper.properties "$wrapper_dir/"
done
