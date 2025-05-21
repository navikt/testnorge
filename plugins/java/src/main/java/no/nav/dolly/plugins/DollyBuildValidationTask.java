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

    private static boolean validateGitHubWorkflowTriggers(Logger log, Project project, Set<String> libraryNames) {

        boolean errors = false;
        var workflowFile = findGitHubActionsWorkflowFile(project);
        if (workflowFile == null) {
            log.error("No app.{}.yml or proxy.{}.yml workflow found in /../../.github/workflows", project.getName(), project.getName());
            return true;
        }

        try {
            var workflow = getGitHubActionsWorkflowFileContents(workflowFile);
            if (workflow == null) {
                log.warn("Workflow /../../.github/workflows/{} is empty", workflowFile.getName());
                return true;
            }
            if (!workflow.containsKey("on")) {
                log.warn("Workflow /../../.github/workflows/{} does not have any 'on:' trigger configuration", workflowFile.getName());
                return true;
            }

            var triggerPaths = resolveGitHubActionsWorkflowTriggerPaths(workflow);
            if (triggerPaths.isEmpty()) {
                log.warn("Workflow /../../.github/workflows/{} has 'on:' trigger, but no 'push.paths'", workflowFile.getName());
                return true;
            }
            for (var library : libraryNames) {
                var expected = "libs/" + library + "/**"; // e.g., libs/some-library/**
                var foundTrigger = triggerPaths
                        .stream()
                        .anyMatch(path -> path.equals(expected));
                if (!foundTrigger) {
                    log.warn("Workflow /../../.github/workflows/{} is missing trigger on '{}'", workflowFile.getName(), expected);
                    errors = true;
                }
            }

        } catch (IOException e) {
            log.error("Error reading workflow file /../../.github/workflows/{}", workflowFile.getName(), e);
        } catch (ClassCastException e) {
            log.error("Error parsing workflow file /../../.github/workflows/{}", workflowFile.getName(), e);
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

    @SuppressWarnings("unchecked")
    private static List<String> resolveGitHubActionsWorkflowTriggerPaths(Map<String, Object> workflow) {
        var paths = new ArrayList<String>();
        var on = workflow.get("on");
        if (on instanceof Map) {
            var onMap = (Map<String, Object>) on;
            if (onMap.containsKey("push")) {
                var push = onMap.get("push");
                if (push instanceof Map && ((Map<String, Object>) push).containsKey("paths")) {
                    paths.addAll((List<String>) ((Map<String, Object>) push).get("paths"));
                }
            }
        }
        return paths;
    }

}
