package no.nav.dolly.plugins;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.Optional;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class DollyBuildValidationTask extends DefaultTask {

    static final String TARGET_GROUP_ID = "no.nav.testnav.libs";
    private static final Set<String> PROJECTS_WITHOUT_WORKFLOW_FILE = Set.of(
            "maskinporten-mock",
            "tokendings-mock"
    );

    @Input
    public abstract Property<String> getCurrentProjectName();

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getSettingsGradleFile();

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getWorkflowFile();

    @Input
    public abstract SetProperty<String> getOurLibraryNames();

    @TaskAction
    public void performChecks() {

        var log = getLogger();

        // Check settings.gradle against build.gradle.
        var librariesInBuildGradle = getOurLibraryNames().get();
        var errorsFromSettingsGradle = validateSettingsGradle(log, librariesInBuildGradle);

        // Check GitHub Workflow triggers against build.gradle.
        var errorsFromGitHubWorkflow = false;
        if (!PROJECTS_WITHOUT_WORKFLOW_FILE.contains(getCurrentProjectName().get())) {
            errorsFromGitHubWorkflow = validateGitHubWorkflowTriggers(log, librariesInBuildGradle);
        }

        if (errorsFromSettingsGradle || errorsFromGitHubWorkflow) {
            throw new GradleException("Dolly build validation failed. See logs for details.");
        }

    }

    private boolean validateSettingsGradle(Logger log, Set<String> librariesInBuildGradle) {

        boolean errors = false;
        var settingsGradleFile = getSettingsGradleFile().get().getAsFile();
        if (!settingsGradleFile.exists()) {
            log.warn("Missing settings.gradle at {}", settingsGradleFile.getAbsolutePath());
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
            return true;
        }

        return errors;
    }

    private boolean validateGitHubWorkflowTriggers(Logger log, Set<String> libraryNames) {

        var workflowFile = getWorkflowFile().getAsFile().getOrNull();
        if (workflowFile == null) {
            log.error("Neither app.{}.yml nor proxy.{}.yml workflow found for project {} in /.github/workflows", getCurrentProjectName().get(), getCurrentProjectName().get(), getCurrentProjectName().get());
            return true;
        }

        try {
            var workflow = getGitHubActionsWorkflowFileContents(workflowFile);
            if (workflow == null) {
                log.warn("Workflow {} is empty", workflowFile.getName());
                return true;
            }
            if (!workflow.containsKey("on")) {
                log.warn("Workflow {} does not have any 'on:' trigger configuration", workflowFile.getName());
                return true;
            }

            var triggerPaths = resolveGitHubActionsWorkflowTriggerPaths(workflow);
            if (triggerPaths.isEmpty()) {
                log.warn("Workflow {} has 'on:' trigger, but no 'push.paths'", workflowFile.getName());
                return false; // Log a warning, but don't fail the build. It might be intentionally set to only build manually.
            }
            return verifyTriggers(log, getCurrentProjectName().get(), workflowFile.getName(), libraryNames, triggerPaths);

        } catch (IOException e) {
            log.error("Error reading workflow file {}", workflowFile.getName(), e);
            return true;
        } catch (ClassCastException e) {
            log.error("Error parsing workflow file {}", workflowFile.getName(), e);
            return true;
        }

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

    @SuppressWarnings("java:S1192") // Avoid complaints about "libs/" being repeated; more readable as is.
    private boolean verifyTriggers(Logger log, String currentProjectName, String workflowFilename, Collection<String> libraryNames, Collection<String> triggerPaths) {

        var errors = false;

        // Check for missing triggers.
        for (var library : libraryNames) {
            var expected = "libs/" + library + "/**";
            var foundTrigger = triggerPaths
                    .stream()
                    .anyMatch(path -> path.equals(expected));
            if (!foundTrigger) {
                log.warn("Workflow /.github/workflows/{} is missing trigger on '{}'", workflowFilename, expected);
                errors = true;
            }
        }

        // Check for unnecessary triggers.
        var expectedTriggers = libraryNames
                .stream()
                .map(library -> "libs/" + library + "/**")
                .collect(Collectors.toSet());
        for (var path : triggerPaths) {
            if (path.startsWith("libs/") && !expectedTriggers.contains(path)) {
                log.warn("Workflow /.github/workflows/{} has unnecessary trigger on '{}'", workflowFilename, path);
                errors = true;
            }
        }

        // Check for trigger on the project itself.
        var foundTriggerOnProject = triggerPaths
                .stream()
                .anyMatch(path -> path.equals("apps/" + currentProjectName + "/**") || path.equals("proxies/" + currentProjectName + "/**"));
        if (!foundTriggerOnProject) {
            log.warn("Workflow /.github/workflows/{} is missing trigger on its own codebase", workflowFilename);
            errors = true;
        }

        // Check for trigger on plugins.
        var foundTriggerOnPlugins = triggerPaths
                .stream()
                .anyMatch(path -> path.equals("plugins/**"));
        if (!foundTriggerOnPlugins) {
            log.warn("Workflow /.github/workflows/{} is missing trigger on 'plugins/**'", workflowFilename);
            errors = true;
        }

        // Check for trigger on workflow file.
        var foundTriggerOnWorkflow = triggerPaths
                .stream()
                .anyMatch(path -> path.equals(".github/workflows/app." + currentProjectName + ".yml") || path.equals(".github/workflows/proxy." + currentProjectName + ".yml"));
        if (!foundTriggerOnWorkflow) {
            log.warn("Workflow /.github/workflows/{} is missing trigger on itself", workflowFilename);
            errors = true;
        }

        return errors;

    }

}
