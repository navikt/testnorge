package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.sonarqube.gradle.SonarExtension;

public class DollySonarPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        var plugins = project.getPlugins();
        plugins.apply("jacoco");
        plugins.apply("org.sonarqube");

        var sonar = project.getExtensions().getByType(SonarExtension.class);
        sonar.properties(props -> {
            props.property("sonar.dynamicAnalysis", "reuseReports");
            props.property("sonar.host.url", "https://sonarcloud.io");
            props.property("sonar.java.coveragePlugin", "jacoco");
            props.property("sonar.language", "java");
            props.property("sonar.organization", "navikt");
            props.property("sonar.project.monorepo.enabled", true);
            props.property("sonar.sourceEncoding", "UTF-8");
            props.property("sonar.token", System.getenv("SONAR_TOKEN"));
        });

    }

}
