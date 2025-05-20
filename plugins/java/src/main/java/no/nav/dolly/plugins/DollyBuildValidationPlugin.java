package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DollyBuildValidationPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project
                .getTasks()
                .register("dollyValidation", DollyBuildValidationTask.class, task -> {
                    task.setGroup("verification");
                    task.setDescription("Analyzes dependencies and GitHub workflow triggers for " + project.getName());
                });
    }

}
