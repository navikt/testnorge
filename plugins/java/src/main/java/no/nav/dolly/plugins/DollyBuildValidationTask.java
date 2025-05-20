package no.nav.dolly.plugins;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DollyBuildValidationTask extends DefaultTask {

    private static final String TARGET_GROUP_ID = "no.nav.testnav.libs";

    @TaskAction
    public void performChecks() {

        var log = getLogger();
        var project = getProject();

        // Check settings.gradle against build.gradle.
        var librariesInBuildGradle = getOurLibraries(project);
        boolean errors = validateSettingsGradle(log, project, librariesInBuildGradle);

        // Check GitHub Workflow triggers against build.gradle.
        errors = errors && checkWorkflowTriggers(log, project, librariesInBuildGradle);

        if (errors) {
            throw new GradleException("Dolly checks failed. See logs for details.");
        }

    }

    private static Set<String> getOurLibraries(Project project) {
        return project
                .getConfigurations()
                .stream()
                .filter(Configuration::isCanBeResolved)
                .flatMap(c -> c.getAllDependencies().stream())
                .filter(d -> TARGET_GROUP_ID.equals(d.getGroup()))
                .map(Dependency::getName)
                .collect(Collectors.toSet());
    }

    private static boolean validateSettingsGradle(Logger log, Project project, Set<String> librariesInBuildGradle) {

        boolean errors = false;
        var settingsGradleFile = project
                .getRootDir()
                .toPath()
                .resolve("settings.gradle")
                .toFile();
        if (!settingsGradleFile.exists()) {
            log.warn("Missing settings.gradle at {}", project.getRootDir());
            return true;
        }

        try {
            var settingsGradle = new String(Files.readAllBytes(settingsGradleFile.toPath()));

            // Check for entries in build.gradle that are missing from settings.gradle.
            for (var library : librariesInBuildGradle) {
                if (!settingsGradle.contains("../../libs/" + library)) {
                    log.warn("Library {}:{} found in build.gradle but not in settings.gradle", TARGET_GROUP_ID, library);
                    errors = true;
                }
            }

            // Check for entries in settings.gradle that are missing from build.gradle.
            var includeBuildMatcher = Pattern
                    .compile("includeBuild [\"']../../libs/([\\w-]+)[\"']")
                    .matcher(settingsGradle);
            while (includeBuildMatcher.find()) {
                var libraryFromSettingsGradle = includeBuildMatcher.group(1);
                if (!librariesInBuildGradle.contains(libraryFromSettingsGradle)) {
                    log.warn("Library {}:{} found in settings.gradle but not in build.gradle", TARGET_GROUP_ID, libraryFromSettingsGradle);
                    errors = true;
                }
            }


        } catch (IOException e) {
            log.error("Error reading settings.gradle", e);
            return false;
        }

        return errors;
    }

    private static boolean checkWorkflowTriggers(Logger log, Project project, Set<String> libraryNames) {

        boolean errors = false;
        var workflowPath = project
                .getProjectDir()
                .toPath()
                .resolve("../../.github/workflows/app." + project.getName() + ".yml"); // Look for an app workflow.
        if (!workflowPath.toFile().exists()) {
            workflowPath = project
                    .getProjectDir()
                    .toPath()
                    .resolve("../../.github/workflows/proxy." + project.getName() + ".yml"); // Look for a proxy workflow.
        }
        if (!workflowPath.toFile().exists()) {
            log.error("No app or proxy workflow found in {}/../../.github/workflows", project.getProjectDir());
            return false;
        }

        var yaml = new Yaml();
        try (var inputStream = new FileInputStream(workflowPath.toFile())) {
            Map<String, Object> workflowConfig = yaml.load(inputStream);
            if (workflowConfig == null || !workflowConfig.containsKey("on")) {
                log.warn("Workflow {} does not have an 'on:' trigger configuration.", workflowPath.getFileName());
                return false;
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
                log.info("Workflow {} has 'on:' trigger, but no specific 'push.paths' found to analyze for library triggers.", workflowPath.getFileName());
            }

            for (String libName : libraryNames) {
                // Assumption: Library 'libName' is in a directory 'libName/**' or 'libs/libName/**' relative to repo root.
                // This path convention is critical and must match your project structure.
                String expectedPath1 = libName + "/**";         // e.g., some-library/**
                String expectedPath2 = "libs/" + libName + "/**"; // e.g., libs/some-library/**

                boolean foundTrigger = triggerPaths.stream().anyMatch(p -> p.equals(expectedPath1) || p.equals(expectedPath2));

                if (!foundTrigger) {
                    log.warn("Workflow {} does not seem to have a push trigger for changes in library '{}' (expected path like '{}' or '{}').",
                            workflowPath.getFileName(), libName, expectedPath1, expectedPath2);
                } else {
                    log.info("Workflow {} has a trigger for library '{}'.", workflowPath.getFileName(), libName);
                }
            }

        } catch (IOException e) {
            log.error("Error reading or parsing workflow file {}: {}", workflowPath.getFileName(), e.getMessage());
        } catch (ClassCastException e) {
            log.error("Error parsing workflow file {}: structure not as expected. {}", workflowPath.getFileName(), e.getMessage());
        }

        return errors;

    }

}
