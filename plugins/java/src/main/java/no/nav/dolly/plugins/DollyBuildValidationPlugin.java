package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaBasePlugin;

import java.util.stream.Collectors;

import static org.gradle.api.plugins.JavaBasePlugin.BUILD_TASK_NAME;

@SuppressWarnings("unused")
public class DollyBuildValidationPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        // Register dollyValidation plugin as a verification plugin.
        var dollyValidationTaskProvider = project
                .getTasks()
                .register("dollyValidation", DollyBuildValidationTask.class, task -> {
                    task.setGroup("verification");
                    task.setDescription("Analyzes dependencies and GitHub workflow triggers for " + project.getName());

                    task.getProjectName().set(project.getName());
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

        // Set dependencies between build tasks and dollyValidation.
        project
                .getPluginManager()
                .withPlugin("java", javaPlugin -> {
                    var tasks = project.getTasks();
                    tasks.named("build").configure(task -> task.dependsOn(dollyValidationTaskProvider));
                    tasks.named("processResources").configure(task -> task.dependsOn(dollyValidationTaskProvider));
                    tasks.named("processTestResources").configure(task -> task.dependsOn(dollyValidationTaskProvider));
                    tasks.named("compileJava").configure(task -> task.dependsOn(dollyValidationTaskProvider));
                });

    }

}
