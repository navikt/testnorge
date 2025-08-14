package no.nav.dolly.libs.nais;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class NaisRuntimeEnvironmentConnector {

    private final ConfigurableEnvironment environment;

    Map<String, String> getEnvironmentVariables()
            throws NaisEnvironmentException {

        if (!environment.matchesProfiles("local")) {
            throw new NaisEnvironmentException("Attempted to load environment variables from pod in non-local profile");
        }
        try {
            var applicationName = resolveApplicationName();
            var requestedKeys = resolveRequestedKeys();
            if (applicationName == null || requestedKeys.isEmpty()) {
                log.info("Application is not configured for dynamic environment variables from NAIS");
                return Map.of();
            }
            var cluster = resolveCluster();
            var pod = resolvePod(cluster, applicationName);
            return getVariables(cluster, pod, requestedKeys);
        } catch (NaisEnvironmentException e) {
            throw e;
        } catch (Exception e) {
            throw new NaisEnvironmentException("Unexpected failure", e);
        }

    }

    private List<String> resolveRequestedKeys() {

        var keys = new ArrayList<String>();
        for (int i = 0; ; i++) {
            var key = "dolly.nais.variables[%d]".formatted(i);
            if (!environment.containsProperty(key)) {
                break;
            }
            keys.add(environment.getProperty(key));
        }
        return keys;

    }

    private String resolveApplicationName() {

        return Optional
                .ofNullable(environment.getProperty("dolly.nais.name"))
                .map(Object::toString)
                .orElse(null);

    }

    private String resolveCluster() {

        return Optional
                .ofNullable(environment.getProperty("dolly.nais.cluster"))
                .map(Object::toString)
                .orElseGet(() -> {
                    log.info("Cannot determine cluster from dolly.nais.cluster, guessing on dev-gcp");
                    return "dev-gcp";
                });

    }

    private String resolvePod(String cluster, String applicationName)
            throws NaisEnvironmentException {

        var pods = getAllPodsInClusterWithName(cluster, applicationName);
        if (pods.isEmpty()) {
            throw new NaisEnvironmentException("No running pods found for application %s in %s".formatted(applicationName, cluster));
        }
        if (pods.size() > 1) {
            log.warn("Multiple pods found for {} in {}, picking {}", applicationName, cluster, pods.getFirst());
        }
        return pods.getFirst();

    }

    private Map<String, String> getVariables(String cluster, String pod, List<String> requestedKeys)
            throws NaisEnvironmentException {

        var command = "kubectl exec --cluster=%s --namespace=dolly %s -- env"
                .formatted(cluster, pod);
        var output = execute(command);
        var variables = output
                .stream()
                .map(line -> line.split("="))
                .filter(elements -> requestedKeys.contains(elements[0]))
                .collect(Collectors.toMap(
                        elements -> elements[0],
                        elements -> elements[1],
                        (a, b) -> a));
        log.info("Retrieved {}/{} keys from pod {}:{}", variables.size(), requestedKeys.size(), cluster, pod);
        return variables;

    }

    private List<String> getAllPodsInClusterWithName(String cluster, String name)
            throws NaisEnvironmentException {

        var command = "kubectl get pods --cluster=%s --namespace=dolly -l app=%s -o name"
                .formatted(cluster, name);
        return execute(command)
                .stream()
                .map(pod -> pod.substring(pod.lastIndexOf("/") + 1).trim())
                .sorted()
                .toList();

    }

    private List<String> execute(String command)
            throws NaisEnvironmentException {

        var processBuilder = new ProcessBuilder(command.split("\\s+"));
        try {
            var process = processBuilder.start();
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var output = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            var status = process.waitFor();
            if (status != 0) {
                log.warn("Command terminated with status {}: {}", status, command);
            }
            return output;
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for command: {}", command, e);
            Thread.currentThread().interrupt();
            return List.of();
        } catch (Exception e) {
            throw new NaisEnvironmentException("Failed to execute command: " + command, e);
        }

    }

}
