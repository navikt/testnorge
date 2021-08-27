package no.nav.testnav.libs.dto.organiasjonbestilling.v2;

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
        switch (status) {
            case ERROR:
                return "Ukjent feil.";
            case FAILED:
                return "Ukjent kristisk feil.";
            case RUNNING:
                return "Bestillingen blir prosessert av EREG.";
            case COMPLETED:
                return "Bestilling er fullført.";
            case NOT_FOUND:
                return "Finner ikke bestilling. Loggene kan ha blitt slettet.";
            case ADDING_TO_QUEUE:
                return "Venter på at bestillingen får tildelt en position i køen.";
            case INN_QUEUE_WAITING_TO_START:
                return "Bestillingen venter i en køen.";
            default:
                return null;
        }
    }


}