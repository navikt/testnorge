package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisasjonStatusDTO {

    public enum Status {
        NOT_FOUND, ADDING_TO_QUEUE, IN_QUEUE_WAITING_TO_START, RUNNING, COMPLETED, ERROR, FAILED
    }

    private Status status;
    private String description;
}
