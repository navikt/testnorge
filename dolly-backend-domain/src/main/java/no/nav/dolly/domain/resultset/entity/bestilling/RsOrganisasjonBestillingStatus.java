package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling.SyntetiskOrganisasjon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private boolean ferdig;
    private LocalDateTime sistOppdatert;
    private List<String> environments;
    private List<OrganisasjonBestillingProgress> status;
    private String organisasjonNummer;

    private SyntetiskOrganisasjon bestilling;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }
}
