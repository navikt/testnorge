package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;
import org.springframework.boot.gradle.tasks.bundling.BootJar;

import java.util.stream.Collectors;

import static org.gradle.api.plugins.JavaBasePlugin.BUILD_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.*;

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

        // Configure task dependencies and input wiring for dollyValidation, and ensure the build task depends on dollyValidation.
        project
                .getPluginManager()
                .withPlugin("java", javaPluginApplied -> {

                    var processResourcesProvider = project.getTasks().named(PROCESS_RESOURCES_TASK_NAME, ProcessResources.class);
                    var processTestResourcesProvider = project.getTasks().named(PROCESS_TEST_RESOURCES_TASK_NAME, ProcessResources.class);
                    var compileJavaProvider = project.getTasks().named(COMPILE_JAVA_TASK_NAME, JavaCompile.class);
                    var compileTestJavaProvider = project.getTasks().named(COMPILE_TEST_JAVA_TASK_NAME, JavaCompile.class);
                    var testProvider = project.getTasks().named(TEST_TASK_NAME, Test.class);
                    var jarProvider = project.getTasks().named(JAR_TASK_NAME, Jar.class);

                    dollyValidationTaskProvider.configure(dollyValidationTask -> {

                        // Wire main resources.
                        dollyValidationTask.getConsumedMainResources().from(processResourcesProvider.map(ProcessResources::getOutputs));
                        dollyValidationTask.dependsOn(processResourcesProvider);

                        // Wire test resources.
                        dollyValidationTask.getConsumedTestResources().from(processTestResourcesProvider.map(ProcessResources::getOutputs));
                        dollyValidationTask.dependsOn(processTestResourcesProvider);

                        // Wire compileJava outputs.
                        dollyValidationTask.getCompiledJavaOutputs().from(compileJavaProvider.map(JavaCompile::getOutputs));
                        dollyValidationTask.dependsOn(compileJavaProvider);

                        // Wire compileTestJava outputs.
                        dollyValidationTask.getCompiledTestOutputs().from(compileTestJavaProvider.map(JavaCompile::getOutputs));
                        dollyValidationTask.dependsOn(compileTestJavaProvider);

                        // Wire test task outputs.
                        dollyValidationTask.getTestTaskOutputs().from(testProvider.map(Test::getOutputs));
                        dollyValidationTask.dependsOn(testProvider);

                        // Wire jar task output.
                        dollyValidationTask.getJarFile().set(jarProvider.flatMap(Jar::getArchiveFile));
                        dollyValidationTask.dependsOn(jarProvider);

                    });

                    // Specifics for the Spring Boot plugin.
                    project
                            .getPluginManager()
                            .withPlugin("org.springframework.boot", springBootPluginApplied -> {

                        var bootJarProvider = project.getTasks().named(SpringBootPlugin.BOOT_JAR_TASK_NAME, BootJar.class);
                        var resolveMainClassNameProvider = project.getTasks().named(SpringBootPlugin.RESOLVE_MAIN_CLASS_NAME_TASK_NAME);

                        dollyValidationTaskProvider.configure(dollyValidationTask -> {

                            // Wire bootJar output.
                            dollyValidationTask.getBootJarFile().set(bootJarProvider.flatMap(BootJar::getArchiveFile));
                            dollyValidationTask.dependsOn(bootJarProvider);

                            // Wire resolveMainClassName output.
                            dollyValidationTask.dependsOn(resolveMainClassNameProvider);

                        });

                    });

                    // build depends on dollyValidation.
                    project
                            .getTasks()
                            .named(BUILD_TASK_NAME)
                            .configure(buildTask -> buildTask.dependsOn(dollyValidationTaskProvider));

                });

    }

}
