package no.nav.dolly.domain.resultset.entity.bestilling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsOrganisasjonMalBestillingWrapper {

    private Map<String, List<RsOrganisasjonMalBestilling>> malbestillinger;

    public Map<String, List<RsOrganisasjonMalBestilling>> getMalbestillinger() {

        if (isNull(malbestillinger)) {
            malbestillinger = new TreeMap<>();
        }
        return malbestillinger;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsOrganisasjonMalBestilling {

        private Long id;
        private String malNavn;
        private OrganisasjonBestilling bestilling;
        private RsBrukerUtenFavoritter bruker;
    }
}
