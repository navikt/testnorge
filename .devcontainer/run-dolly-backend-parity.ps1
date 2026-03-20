param(
    [string]$SpringProfile = "local"
)

$ErrorActionPreference = "Stop"

Set-Location "/workspaces/testnorge/apps/dolly-backend"
$env:SPRING_PROFILES_ACTIVE = $SpringProfile

./gradlew bootJar

java "-Duser.timezone=Europe/Oslo" "-Dreactor.bufferSize.small=10000" "--add-opens" "java.base/java.lang=ALL-UNNAMED" "-jar" "build/libs/app.jar"


