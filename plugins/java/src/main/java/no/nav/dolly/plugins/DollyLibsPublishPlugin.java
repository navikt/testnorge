package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

@SuppressWarnings("unused")
public class DollyLibsPublishPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        var plugins = project.getPlugins();
        plugins.apply("maven-publish");
        plugins.apply("signing");

        if (project.hasProperty("releaseVersion")) {
            project.setVersion(project.getProperties().get("releaseVersion"));
        }

        var publishing = project.getExtensions().getByType(PublishingExtension.class);
        publishing.getRepositories().maven(maven -> {
            maven.setName("github");
            maven.setUrl("https://maven.pkg.github.com/navikt/testnorge");
            maven.credentials(creds -> {
                creds.setUsername(System.getenv("GITHUB_ACTOR"));
                creds.setPassword(System.getenv("GITHUB_TOKEN"));
            });
        });
        publishing.getPublications().create("gpr", MavenPublication.class, pub ->
                pub.from(project.getComponents().getByName("java"))
        );

    }

}

