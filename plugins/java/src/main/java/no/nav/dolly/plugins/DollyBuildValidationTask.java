package no.nav.dolly.plugins;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DollyBuildValidationTask extends DefaultTask {

    static final String TARGET_GROUP_ID = "no.nav.testnav.libs";

    private final Property<String> projectName;
    private final DirectoryProperty projectDir;
    private final DirectoryProperty rootDir;
    private final SetProperty<String> ourLibraryNames;

    public DollyBuildValidationTask() {
        this.projectName = getProject().getObjects().property(String.class);
        this.projectDir = getProject().getObjects().directoryProperty();
        this.rootDir = getProject().getObjects().directoryProperty();
        this.ourLibraryNames = getProject().getObjects().setProperty(String.class);
    }

    @Input
    public Property<String> getProjectName() {
        return projectName;
    }

    @InputDirectory
    public DirectoryProperty getProjectDirectory() {
        return projectDir;
    }

    @InputDirectory
    public DirectoryProperty getRootDirectory() {
        return rootDir;
    }

    @Input
    public SetProperty<String> getOurLibraryNames() {
        return ourLibraryNames;
    }

    @TaskAction
    public void performChecks() {

        var log = getLogger();

        // Check settings.gradle against build.gradle.
        var librariesInBuildGradle = getOurLibraryNames().get();
        var errorsFromSettingsGradle = validateSettingsGradle(log, librariesInBuildGradle);

        // Check GitHub Workflow triggers against build.gradle.
        var errorsFromGitHubWorkflow = validateGitHubWorkflowTriggers(log, librariesInBuildGradle);

        if (errorsFromSettingsGradle || errorsFromGitHubWorkflow) {
            throw new GradleException("Dolly build validation failed. See logs for details.");
        }

    }

    private boolean validateSettingsGradle(Logger log, Set<String> librariesInBuildGradle) {

        boolean errors = false;
        var rootDirAsFile = getRootDirectory()
                .get()
                .getAsFile();
        var settingsGradleFile = rootDirAsFile
                .toPath()
                .resolve("settings.gradle")
                .toFile();
        if (!settingsGradleFile.exists()) {
            log.warn("Missing settings.gradle at {}", rootDirAsFile.getAbsolutePath());
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

        var workflowFile = findGitHubActionsWorkflowFile();
        if (workflowFile == null) {
            var expected = getProjectDirectory()
                    .get()
                    .dir("../../.github/workflows")
                    .getAsFile()
                    .getAbsolutePath();
            log.error("No app.{}.yml or proxy.{}.yml workflow found in {}}", getProjectName().get(), getProjectName().get(), expected);
            return true;
        }

        try {
            var workflow = getGitHubActionsWorkflowFileContents(workflowFile);
            if (workflow == null) {
                log.warn("Workflow ../../.github/workflows/{} is empty", workflowFile.getName());
                return true;
            }
            if (!workflow.containsKey("on")) {
                log.warn("Workflow ../../.github/workflows/{} does not have any 'on:' trigger configuration", workflowFile.getName());
                return true;
            }

            var triggerPaths = resolveGitHubActionsWorkflowTriggerPaths(workflow);
            if (triggerPaths.isEmpty()) {
                log.warn("Workflow ../../.github/workflows/{} has 'on:' trigger, but no 'push.paths'", workflowFile.getName());
                return false; // Log a warning, but don't fail the build. It might be intentionally set to only build manually.
            }
            return verifyTriggers(log, getProjectName().get(), workflowFile.getName(), libraryNames, triggerPaths);

        } catch (IOException e) {
            log.error("Error reading workflow file ../../.github/workflows/{}", workflowFile.getName(), e);
            return true;
        } catch (ClassCastException e) {
            log.error("Error parsing workflow file ../../.github/workflows/{}", workflowFile.getName(), e);
            return true;
        }

    }

    private File findGitHubActionsWorkflowFile() {

        var workflowDir = getProjectDirectory()
                .get()
                .dir("../../.github/workflows")
                .getAsFile()
                .toPath();
        var currentProjectName = getProjectName().get();
        var appWorkflowFile = workflowDir
                .resolve("app." + currentProjectName + ".yml")
                .toFile();
        if (appWorkflowFile.exists()) {
            return appWorkflowFile;
        }
        var proxyWorkflowFile = workflowDir
                .resolve("proxy." + currentProjectName + ".yml")
                .toFile();
        if (proxyWorkflowFile.exists()) {
            return proxyWorkflowFile;

        }
        return null;

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

    private boolean verifyTriggers(Logger log, String currentProjectName, String workflowFilename, Collection<String> libraryNames, Collection<String> triggerPaths) {

        var errors = false;

        // Check for missing triggers.
        for (var library : libraryNames) {
            var expected = "libs/" + library + "/**";
            var foundTrigger = triggerPaths
                    .stream()
                    .anyMatch(path -> path.equals(expected));
            if (!foundTrigger) {
                log.warn("Workflow ../../.github/workflows/{} is missing trigger on '{}'", workflowFilename, expected);
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
                log.warn("Workflow ../../.github/workflows/{} has unnecessary trigger on '{}'", workflowFilename, path);
                errors = true;
            }
        }

        // Check for trigger on the project itself.
        var foundTriggerOnProject = triggerPaths
                .stream()
                .anyMatch(path -> path.equals("apps/" + currentProjectName + "/**") || path.equals("proxies/" + currentProjectName + "/**"));
        if (!foundTriggerOnProject) {
            log.warn("Workflow ../../.github/workflows/{} is missing trigger on either 'apps/{}/**' or 'proxies/{}/**'", workflowFilename, currentProjectName, currentProjectName);
            errors = true;
        }

        // Check for trigger on plugins.
        var foundTriggerOnPlugins = triggerPaths
                .stream()
                .anyMatch(path -> path.equals("plugins/**"));
        if (!foundTriggerOnPlugins) {
            log.warn("Workflow ../../.github/workflows/{} is missing trigger on 'plugins/**'", workflowFilename);
            errors = true;
        }

        // Check for trigger on workflow file.
        var foundTriggerOnWorkflow = triggerPaths
                .stream()
                .anyMatch(path -> path.equals(".github/workflows/app." + currentProjectName + ".yml") || path.equals(".github/workflows/proxy." + currentProjectName + ".yml"));
        if (!foundTriggerOnWorkflow) {
            log.warn("Workflow ../../.github/workflows/{} is missing trigger on either '.github/workflows/app.{}.yml' or '.github/workflows/proxy.{}.yml'", currentProjectName, currentProjectName, currentProjectName);
            errors = true;
        }

        return errors;

    }

}
