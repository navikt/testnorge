package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling.SyntetiskOrganisasjon;
import no.nav.dolly.domain.resultset.RsOrganisasjonStatusRapport;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class RsOrganisasjonBestillingStatus {

    private Long id;
    private Integer antallLevert;
    private String feil;
    private Boolean ferdig;
    private LocalDateTime sistOppdatert;
    private String malBestillingNavn;
    private Set<String> environments;
    private String organisasjonNummer;

    private List<RsOrganisasjonStatusRapport> status;

    private SyntetiskOrganisasjon bestilling;

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }
}
