package no.nav.dolly.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOrganisasjonerDTO {

    private String interval;
    private Integer totaltUnikeBrukere;
    private Integer totaltAntallOrganisasjoner;
    private List<Entry> organisasjoner;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String organisasjonsnummer;
        private String navn;
        private Integer unikeBrukere;
    }
}
