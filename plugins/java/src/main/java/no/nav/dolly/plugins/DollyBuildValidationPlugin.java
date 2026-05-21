package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaBasePlugin;

import java.io.File;
import java.util.Optional;
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

                    var currentProjectName = project.getName();
                    task.getCurrentProjectName().set(currentProjectName);

                    // Set settings.gradle file (located at multi-project root)
                    var rootDir = project.getRootDir();
                    task.getSettingsGradleFile().set(new File(rootDir, "settings.gradle"));

                    // Set workflow file to either an app workflow, a proxy workflow, or a synt workflow.
                    task
                            .getWorkflowFile()
                            .set(resolveWorkflowFile(rootDir, currentProjectName).orElse(null));

                    // Set our library names, based on project dependencies (e.g. build.gradle).
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

        // Make build task depend on dollyValidation task.
        project
                .getPluginManager()
                .withPlugin("java-base", javaBasePluginApplied -> project
                        .getTasks()
                        .named(JavaBasePlugin.BUILD_TASK_NAME)
                        .configure(task -> task.dependsOn(dollyValidationTaskProvider)));

    }

    private static Optional<File> resolveWorkflowFile(File rootDir, String currentProjectName) {

        var workflowsDir = new File(rootDir, "../../.github/workflows");

        var appWorkflowFile = new File(workflowsDir, "app." + currentProjectName + ".yml");
        if (appWorkflowFile.exists()) {
            return Optional.of(appWorkflowFile);
        }

        var proxyWorkflowFile = new File(workflowsDir, "proxy." + currentProjectName + ".yml");
        if (proxyWorkflowFile.exists()) {
            return Optional.of(proxyWorkflowFile);
        }


        var syntWorkflowFile = new File(workflowsDir, "synt." + currentProjectName + ".yml");
        if (syntWorkflowFile.exists()) {
            return Optional.of(syntWorkflowFile);
        }

        return Optional.empty();

    }

}
