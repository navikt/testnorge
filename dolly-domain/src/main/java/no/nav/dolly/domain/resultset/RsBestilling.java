package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Set<RsIdentStatus> status;
    private List<RsBestillingProgress> bestillingProgress;
    private Long opprettetFraId;
    private String tpsfKriterier;
    private String openamSent;
    private String eksisterendeIdenter;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }

    public List<RsBestillingProgress> getBestillingProgress() {
        if (isNull(bestillingProgress)) {
            bestillingProgress = new ArrayList<>();
        }
        return bestillingProgress;
    }

    public Set<RsIdentStatus> getStatus() {
        if (isNull(status)) {
            status = new HashSet();
        }
        return status;
    }
}
