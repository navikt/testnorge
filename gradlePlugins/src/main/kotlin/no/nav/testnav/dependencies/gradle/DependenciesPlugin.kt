package no.nav.testnav.dependencies.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependenciesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
    }

    companion object {
        val lombokVersion = "1.18.20"
        val springBoot = "2.5.6"
        val springCloud = "2020.0.4"
        val springSession = "2021.0.3"
        val logstashLogbackEncoder = "6.6"
        val springdocOpenapi = "1.5.12"
        val jackson = "2.13.0"
        val orikaVersion = "1.5.4"
        val validationApi = "2.0.1.Final"
    }
}