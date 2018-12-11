package no.nav.dolly.domain.resultset;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RsBestilling {

    private Long id;
    private int antallIdenter;
    private boolean ferdig;
    private LocalDateTime sistOppdatert;
    private long gruppeId;
    private boolean stoppet;
    private List<String> environments;
    private List<RsBestillingProgress> personStatus;

    public List<String> getEnvironments() {
        if (environments == null) {
            environments = new ArrayList<>();
        }
        return environments;
    }

    public List<RsBestillingProgress> getPersonStatus() {
        if (personStatus == null) {
            personStatus = new ArrayList<>();
        }
        return personStatus;
    }
}
