package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record PensjonOperationResponse(List<EnvironmentStatus> status) {

    public PensjonOperationResponse {
        status = Objects.isNull(status) ? List.of() : List.copyOf(status);
    }

    public void requireSuccessful(Set<String> expectedEnvironments) {
        var actualEnvironments = status.stream()
                .filter(Objects::nonNull)
                .map(EnvironmentStatus::miljo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        var allSuccessful = status.stream()
                .filter(Objects::nonNull)
                .map(EnvironmentStatus::response)
                .allMatch(response -> Objects.nonNull(response)
                        && Objects.nonNull(response.httpStatus())
                        && Objects.nonNull(response.httpStatus().status())
                        && response.httpStatus().status() >= 200
                        && response.httpStatus().status() < 300);

        if (!actualEnvironments.equals(expectedEnvironments)
                || status.size() != expectedEnvironments.size()
                || status.stream().anyMatch(Objects::isNull)
                || !allSuccessful) {
            throw new IllegalStateException("Pensjon-operasjonen returnerte ugyldig miljøstatus.");
        }
    }

    public record EnvironmentStatus(String miljo, OperationStatus response) {
    }

    public record OperationStatus(HttpStatus httpStatus, String message, String path) {
    }

    public record HttpStatus(String reasonPhrase, Integer status) {
    }
}
