package no.nav.dolly.plugins;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
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
        var librariesInBuildGradle = getOurLibrariesFromBuildGradle(project);
        var errorsFromSettingsGradle = validateSettingsGradle(log, project, librariesInBuildGradle);

        // Check GitHub Workflow triggers against build.gradle.
        var errorsFromGitHubWorkflow = validateGitHubWorkflowTriggers(log, project, librariesInBuildGradle);

        if (errorsFromSettingsGradle || errorsFromGitHubWorkflow) {
            throw new GradleException("Dolly checks failed. See logs for details.");
        }

    }

    private static Set<String> getOurLibrariesFromBuildGradle(Project project) {
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

    @SuppressWarnings("unchecked")
    private static boolean validateGitHubWorkflowTriggers(Logger log, Project project, Set<String> libraryNames) {

        boolean errors = false;
        var workflowFile = findGitHubActionsWorkflowFile(project);
        if (workflowFile == null) {
            log.error("No app or proxy workflow found in {}/../../.github/workflows", project.getProjectDir());
            return true;
        }

        try {
            var workflow = getGitHubActionsWorkflowFileContents(workflowFile);
            log.warn("Content: {}", workflow);
            if (workflow == null || !workflow.containsKey("on")) {
                log.warn("Workflow {} does not have an 'on:' trigger configuration.", workflowFile.getName());
                return false;
            }

            var onConfig = workflow.get("on");
            var triggerPaths = new ArrayList<String>();

            if (onConfig instanceof Map) {
                var onMap = (Map<String, Object>) onConfig;
                if (onMap.containsKey("push")) {
                    var pushConfig = onMap.get("push");
                    if (pushConfig instanceof Map && ((Map<String, Object>) pushConfig).containsKey("paths")) {
                        triggerPaths.addAll((List<String>) ((Map<String, Object>) pushConfig).get("paths"));
                    }
                }
                // You can add checks for other triggers like 'workflow_run', 'pull_request.paths', etc.
                // e.g., if (onMap.containsKey("workflow_run")) { ... }
            }
            log.warn("Trigger paths: {}", triggerPaths);


            if (triggerPaths.isEmpty()) {
                log.warn("Workflow {} has 'on:' trigger, but no specific 'push.paths'", workflowFile.getName());
            }

            for (String libName : libraryNames) {
                // Assumption: Library 'libName' is in a directory 'libName/**' or 'libs/libName/**' relative to repo root.
                // This path convention is critical and must match your project structure.
                String expectedPath1 = libName + "/**";         // e.g., some-library/**
                String expectedPath2 = "libs/" + libName + "/**"; // e.g., libs/some-library/**

                boolean foundTrigger = triggerPaths.stream().anyMatch(p -> p.equals(expectedPath1) || p.equals(expectedPath2));

                if (!foundTrigger) {
                    log.warn("Workflow {} does not seem to have a push trigger for changes in library '{}' (expected path like '{}' or '{}').",
                            workflowFile.getName(), libName, expectedPath1, expectedPath2);
                } else {
                    log.info("Workflow {} has a trigger for library '{}'.", workflowFile.getName(), libName);
                }
            }

        } catch (IOException e) {
            log.error("Error reading or parsing workflow file {}: {}", workflowFile.getName(), e.getMessage());
        } catch (ClassCastException e) {
            log.error("Error parsing workflow file {}: structure not as expected. {}", workflowFile.getName(), e.getMessage());
        }

        return errors;

    }

    private static File findGitHubActionsWorkflowFile(Project project) {
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
            return null;
        }
        return workflowPath.toFile();
    }

    private static Map<String, Object> getGitHubActionsWorkflowFileContents(File workflowFile)
            throws IOException {
        try (var inputStream = new FileInputStream(workflowFile)) {
            var yaml = new Yaml(new GitHubActionsWorkflowConstructor());
            return yaml.load(inputStream);
        }
    }

}
