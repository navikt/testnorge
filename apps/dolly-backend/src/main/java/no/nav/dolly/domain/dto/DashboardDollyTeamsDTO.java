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
public class DashboardDollyTeamsDTO {

    private String interval;
    private Integer totaltAntallMedlemmer;
    private Integer totaltAntallTeams;
    private List<Entry> teams;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String navn;
        private String beskrivelse;
        private Integer antallMedlemmer;
    }
}
