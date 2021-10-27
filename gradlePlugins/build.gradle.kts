plugins {
    java
    `kotlin-dsl`
    id("org.sonarqube") version "3.3"
}


sonarqube {
    isSkipProject = true
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("dependencies") {
            id = "no.nav.testnav.dependencies"
            implementationClass = "no.nav.testnav.dependencies.gradle.DependenciesPlugin"
        }
    }
}