package no.nav.dolly.domain.resultset.entity.bestilling;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class RsBestilling {

    private Long id;
    private Integer antallIdenter;
    private boolean ferdig;
    private LocalDateTime sistOppdatert;
    private long gruppeId;
    private boolean stoppet;
    private String feil;
    private List<String> environments;
    private List<RsStatusRapport> status;

    private Long opprettetFraId;
    private String tpsfKriterier;
    private String bestKriterier;
    private String openamSent;
    private String opprettFraIdenter;

    private String malBestillingNavn;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }

    public List<RsStatusRapport> getStatus() {
        if (isNull(status)) {
            status = new ArrayList();
        }
        return status;
    }
}
