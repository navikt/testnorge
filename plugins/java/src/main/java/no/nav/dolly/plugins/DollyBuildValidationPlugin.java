package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.util.stream.Collectors;

import static org.gradle.api.plugins.JavaBasePlugin.BUILD_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.PROCESS_RESOURCES_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.PROCESS_TEST_RESOURCES_TASK_NAME;

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

        // 1. Make dollyValidation depend on processResources.
        // 2. Make build depend on dollyValidation.
        project
                .getPluginManager()
                .withPlugin("java-base", plugin -> {

                    // dollyValidation depends on processResources.
                    try {
                        var processResourcesTaskProvider = project
                                .getTasks()
                                .named(PROCESS_RESOURCES_TASK_NAME, ProcessResources.class);
                        dollyValidationTaskProvider.configure(dollyValidationTask -> dollyValidationTask.dependsOn(processResourcesTaskProvider));
                    } catch (UnknownTaskException e) {
                        project.getLogger().warn("Task {} not found, skipping dependsOn configuration for dollyValidation.", PROCESS_RESOURCES_TASK_NAME);
                    }

                    // dollyValidation depends on processTestResources.
                    try {
                        var processTestResourcesTaskProvider = project
                                .getTasks()
                                .named(JavaPlugin.PROCESS_TEST_RESOURCES_TASK_NAME, ProcessResources.class);
                        dollyValidationTaskProvider.configure(dollyValidationTask -> dollyValidationTask.dependsOn(processTestResourcesTaskProvider));
                    } catch (UnknownTaskException e) {
                        project.getLogger().warn("Task {} not found, skipping dependsOn configuration for dollyValidation.", PROCESS_TEST_RESOURCES_TASK_NAME);
                    }

                    // build depends on dollyValidation.
                    project
                            .getTasks()
                            .named(BUILD_TASK_NAME)
                            .configure(buildTask -> buildTask.dependsOn(dollyValidationTaskProvider));
                });

    }

}
