package no.nav.dolly.libs.nais;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
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
            var requestedSecrets = resolveRequestedSecrets();
            if (applicationName == null || (requestedKeys.isEmpty() && requestedSecrets.isEmpty())) {
                log.info("Application is not configured for dynamic environment variables from NAIS");
                return Map.of();
            }
            var cluster = resolveCluster();
            var pod = resolvePod(cluster, applicationName);
            var environmentVariables = getVariablesFromEnvironment(cluster, pod, requestedKeys);
            var secretVariables = getVariablesFromSecrets(cluster, requestedSecrets, requestedKeys);
            var result = new HashMap<String, String>();
            if (!environmentVariables.isEmpty()) {
                result.putAll(environmentVariables);
            }
            if (!secretVariables.isEmpty()) {
                result.putAll(secretVariables);
            }
            return result;
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

    private List<String> resolveRequestedSecrets() {

        var keys = new ArrayList<String>();
        for (int i = 0; ; i++) {
            var key = "dolly.nais.secrets[%d]".formatted(i);
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

    private Map<String, String> getVariablesFromEnvironment(String cluster, String pod, List<String> requestedKeys)
            throws NaisEnvironmentException {

        if (requestedKeys.isEmpty()) {
            return Map.of();
        }
        var command = "kubectl debug --cluster=%s --namespace=dolly -it %s --image=\"europe-north1-docker.pkg.dev/nais-io/nais/images/debug:latest\" --profile=restricted -- cat /proc/1/environ"
                .formatted(cluster, pod);
        var output = execute(command);
        if (output.size() < 2) {
            throw new NaisEnvironmentException("Failed to retrieve environment variables from pod %s:%s".formatted(cluster, pod));
        }
        var raw = output.get(1).split("\0");
        var variables = Arrays
                .stream(raw)
                .map(line -> line.split("="))
                .filter(elements -> requestedKeys.contains(elements[0]))
                .collect(Collectors.toMap(
                        elements -> elements[0],
                        elements -> elements[1],
                        (a, b) -> a));
        log.info("Retrieved {}/{} key(s) from pod {}:{}", variables.size(), requestedKeys.size(), cluster, pod);
        return variables;

    }

    private Map<String, String> getVariablesFromSecrets(String cluster, List<String> secrets, List<String> requestedKeys)
            throws NaisEnvironmentException {

        if (secrets.isEmpty() || requestedKeys.isEmpty()) {
            return Map.of();
        }
        var variables = new HashMap<String, String>();
        for (String secret : secrets) {

            var command = "kubectl get secret %s --cluster=%s --namespace=dolly -o jsonpath='{.data}'"
                    .formatted(secret, cluster);
            var output = execute(command);
            if (output.isEmpty()) {
                log.warn("Failed to retrieve secret {} from cluster {}", secret, cluster);
                continue;
            }
            var raw = output
                    .getFirst()
                    .replace("'", "")
                    .replaceAll("[{}\"]", "")
                    .split(",");
            Arrays
                    .stream(raw)
                    .map(entry -> entry.split(":", 2))
                    .filter(elements -> elements.length == 2 && requestedKeys.contains(elements[0]))
                    .forEach(elements -> {
                        var decodedValue = new String(Base64.getDecoder().decode(elements[1]));
                        variables.put(elements[0], decodedValue);
                    });

        }
        log.info("Retrieved {}/{} key(s) from secrets in {}", variables.size(), requestedKeys.size(), cluster);
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
                var errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));
                log.error("Command terminated with status {}: {}\n{}", status, command, errorOutput);
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
