plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.mockito:mockito-core:3.12.4")
    implementation("org.mockito:mockito-all:1.10.19")
}

gradlePlugin {
    plugins {
        create("dependencies") {
            id = "no.nav.testnav.dependencies"
            implementationClass = "no.nav.testnav.dependencies.gradle.DependenciesPlugin"
        }
    }
}