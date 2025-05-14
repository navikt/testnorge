package no.nav.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DollyChecksPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project
                .getTasks()
                .register("dolly", DollyCheckTask.class, task -> {
                    task.setGroup("verification");
                    task.setDescription("Analyzes dependencies and GitHub workflow triggers for " + project.getName());
                });
    }

}