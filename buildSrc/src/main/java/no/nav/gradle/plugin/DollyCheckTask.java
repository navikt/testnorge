package no.nav.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DollyCheckTask extends DefaultTask {

    private static final String TARGET_GROUP_ID = "no.nav.testnav.libs";

    @TaskAction
    public void performChecks() {

        var project = getProject();
        getLogger().lifecycle("Starting Dolly checks for project: {}", project.getName());

        // --- Part A: Check Dependencies and settings.gradle ---
        var localLibraries = getLocalLibraries(project);
        if (localLibraries.isEmpty()) {
            getLogger().info("No local libraries (group: {}) found in project {}.", TARGET_GROUP_ID, project.getName());
        } else {
            getLogger().info("Found local libraries from group {}: {}", TARGET_GROUP_ID, localLibraries);
            checkLibrariesInSettingsGradle(project, localLibraries);
        }

        // --- Part B: Check GitHub Workflow Triggers ---
        // Assuming the library name is its directory name under a common 'libs' or root directory
        // And the workflow file name follows the pattern app.<project-name>.yml
        var workflowFileName = "app." + project.getName() + ".yml";
        Path workflowPath = project.getProjectDir().toPath().resolve(".github/workflows/" + workflowFileName);
        checkWorkflowTriggers(project, workflowPath, localLibraries);

        getLogger().lifecycle("Dolly checks completed for project: {}", project.getName());
        // Potentially fail the build if checks don't pass:
        // if (errorsFound) {
        //     throw new GradleException("Dolly checks failed. See logs for details.");
        // }
    }

    private Set<String> getLocalLibraries(Project project) {
        // Consider configurations like 'implementation', 'api', etc.
        return project
                .getConfigurations()
                .stream()
                .filter(Configuration::isCanBeResolved) // Only check resolvable configurations
                .flatMap(conf -> conf.getAllDependencies().stream())
                .filter(dep -> TARGET_GROUP_ID.equals(dep.getGroup()) && dep.getName() != null)
                .map(Dependency::getName) // This is the artifactId, e.g., "some-library"
                .collect(Collectors.toSet());
    }

    private void checkLibrariesInSettingsGradle(Project project, Set<String> libraryNames) {
        var settingsFile = project.getRootDir().toPath().resolve("settings.gradle").toFile(); // Or settings.gradle.kts
        if (!settingsFile.exists()) {
            settingsFile = project.getRootDir().toPath().resolve("settings.gradle.kts").toFile();
        }

        if (!settingsFile.exists()) {
            getLogger().warn("Root settings.gradle or settings.gradle.kts not found at {}", project.getRootDir());
            return;
        }

        try {
            var settingsContent = new String(Files.readAllBytes(settingsFile.toPath()));
            for (var libName : libraryNames) {
                // Assumption: module is included as ':libName' or ':libs:libName' or 'libName'
                // This pattern might need to be more sophisticated based on your conventions
                String expectedIncludePatternSimple = "include\\s*['\"]:" + libName + "['\"]";
                // A more general pattern if libs are in a subdirectory like 'libs/'
                // String expectedIncludePatternSubdir = "include\\s*['\"]:libs:" + libName + "['\"]";


                // A simple check (can be improved with regex for more accuracy)
                if (!settingsContent.contains(":" + libName)) {
                    getLogger().warn("Library '{}' (from group {}) found in {}'s dependencies but might be missing from root settings.gradle.",
                            libName, TARGET_GROUP_ID, project.getName());
                } else {
                    getLogger().info("Library '{}' is included in root settings.gradle.", libName);
                }
            }
        } catch (IOException e) {
            getLogger().error("Error reading settings.gradle file: {}", e.getMessage());
        }
    }

    private void checkWorkflowTriggers(Project project, Path workflowPath, Set<String> libraryNames) {
        if (!Files.exists(workflowPath)) {
            getLogger().warn("GitHub workflow file not found at: {}", workflowPath);
            return;
        }

        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(workflowPath.toFile())) {
            Map<String, Object> workflowConfig = yaml.load(fis);
            if (workflowConfig == null || !workflowConfig.containsKey("on")) {
                getLogger().warn("Workflow {} does not have an 'on:' trigger configuration.", workflowPath.getFileName());
                return;
            }

            Object onConfig = workflowConfig.get("on");
            List<String> triggerPaths = new ArrayList<>();

            if (onConfig instanceof Map) {
                Map<String, Object> onMap = (Map<String, Object>) onConfig;
                if (onMap.containsKey("push")) {
                    Object pushConfig = onMap.get("push");
                    if (pushConfig instanceof Map && ((Map<String, Object>) pushConfig).containsKey("paths")) {
                        triggerPaths.addAll((List<String>) ((Map<String, Object>) pushConfig).get("paths"));
                    }
                }
                // You can add checks for other triggers like 'workflow_run', 'pull_request.paths', etc.
                // e.g., if (onMap.containsKey("workflow_run")) { ... }
            }


            if (triggerPaths.isEmpty()) {
                getLogger().info("Workflow {} has 'on:' trigger, but no specific 'push.paths' found to analyze for library triggers.", workflowPath.getFileName());
            }

            for (String libName : libraryNames) {
                // Assumption: Library 'libName' is in a directory 'libName/**' or 'libs/libName/**' relative to repo root.
                // This path convention is critical and must match your project structure.
                String expectedPath1 = libName + "/**";         // e.g., some-library/**
                String expectedPath2 = "libs/" + libName + "/**"; // e.g., libs/some-library/**

                boolean foundTrigger = triggerPaths.stream().anyMatch(p -> p.equals(expectedPath1) || p.equals(expectedPath2));

                if (!foundTrigger) {
                    getLogger().warn("Workflow {} does not seem to have a push trigger for changes in library '{}' (expected path like '{}' or '{}').",
                            workflowPath.getFileName(), libName, expectedPath1, expectedPath2);
                } else {
                    getLogger().info("Workflow {} has a trigger for library '{}'.", workflowPath.getFileName(), libName);
                }
            }

        } catch (IOException e) {
            getLogger().error("Error reading or parsing workflow file {}: {}", workflowPath.getFileName(), e.getMessage());
        } catch (ClassCastException e) {
            getLogger().error("Error parsing workflow file {}: structure not as expected. {}", workflowPath.getFileName(), e.getMessage());
        }
    }
}