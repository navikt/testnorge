package no.nav.dolly.domain.resultset.entity.bestilling;

import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsBestillingStatus {

    private Long id;
    private Integer antallIdenter;
    private Integer antallLevert;
    private boolean ferdig;
    private LocalDateTime sistOppdatert;
    private RsBrukerUtenFavoritter bruker;
    private long gruppeId;
    private boolean stoppet;
    private String feil;
    private Set<String> environments;
    private List<RsStatusRapport> status;

    private Long opprettetFraId;
    private Long opprettetFraGruppeId;
    private Long gjenopprettetFraIdent;
    private JsonNode bestilling;
    private String opprettFraIdenter;

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }

    public List<RsStatusRapport> getStatus() {
        if (isNull(status)) {
            status = new ArrayList<>();
        }
        return status;
    }
}