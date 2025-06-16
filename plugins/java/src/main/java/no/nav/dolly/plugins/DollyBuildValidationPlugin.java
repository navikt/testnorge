package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

import java.util.List;
import java.util.stream.Collectors;

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

        // Setting up tasks that depends on dollyValidation.
        project
                .getPluginManager()
                .withPlugin("java", javaPlugin -> List.of(
                                "compileJava",
                                "processResources",
                                "processTestResources",
                                "wsImport1",
                                "xjcGenerate",
                                "xjcGenerateTest"
                        )
                        .forEach(optionalTaskName -> project
                                .getTasks()
                                .matching(task -> task.getName().equals(optionalTaskName))
                                .configureEach(task -> task.dependsOn(dollyValidationTaskProvider))));

    }

}
