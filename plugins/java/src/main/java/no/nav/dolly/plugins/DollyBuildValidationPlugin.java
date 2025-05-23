package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DollyBuildValidationPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project
                .getTasks()
                .register("dollyValidation", DollyBuildValidationTask.class, task -> {
                    task.setGroup("verification");
                    task.setDescription("Analyzes dependencies and GitHub workflow triggers for " + project.getName());

                    task.getProjectName().set(project.getName());
                    task.getProjectDirectory().set(project.getLayout().getProjectDirectory());
                    task.getRootDirectory().set(project.getRootDir());

                    var libraries = project
                            .getConfigurations()
                            .stream()
                            .filter(Configuration::isCanBeResolved)
                            .flatMap(configuration -> configuration.getAllDependencies().stream())
                            .filter(dependency -> DollyBuildValidationTask.TARGET_GROUP_ID.equals(dependency.getGroup()))
                            .map(Dependency::getName)
                            .collect(Collectors.toSet());
                    task.getOurLibraryNames().set(libraries);
                });
    }

}
