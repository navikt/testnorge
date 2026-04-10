package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DollySonarPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        var plugins = project.getPlugins();
        plugins.apply("jacoco");
        plugins.apply("org.sonarqube");

        project.afterEvaluate(p -> {
            var sonarqubeExt = p.getExtensions().findByName("sonarqube");
            if (sonarqubeExt != null) {
                p.getExtensions().getByName("sonarqube");
            }
        });

    }

}




