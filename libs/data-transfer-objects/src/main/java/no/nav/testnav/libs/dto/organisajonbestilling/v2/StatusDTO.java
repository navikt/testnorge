package no.nav.testnav.libs.dto.organisajonbestilling.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatusDTO {
    Status status;
    String description;

    public StatusDTO(Status status) {
        this.status = status;
        this.description = getDescription(status);
    }

    private static String getDescription(Status status) {
        return switch (status) {
            case ERROR -> "Ukjent feil";
            case FAILED -> "Ukjent kristisk feil";
            case RUNNING -> "Bestillingen blir prosessert av EREG";
            case PENDING_COMPLETE -> "Venter på status av oppretting";
            case COMPLETED -> "Bestilling er fullført";
            case NOT_FOUND -> "Finner ikke bestilling. Loggene kan ha blitt slettet";
            case ADDING_TO_QUEUE -> "Bestillingen venter på tildeling av plass i køen";
            case IN_QUEUE_WAITING_TO_START -> "Bestillingen venter i køen";
        };
    }
}