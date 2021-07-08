plugins {
    `kotlin-dsl`
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