package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

@SuppressWarnings("unused")
public class DollyLibsPublishPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        var plugins = project.getPlugins();
        plugins.apply("maven-publish");
        plugins.apply("signing");

    }

}



