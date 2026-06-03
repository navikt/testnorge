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
public class DashboardTeamsDTO {

    private String interval;
    private Integer totaltUnikeBrukere;
    private Integer totaltAntallTeams;
    private List<Entry> teams;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String team;
        private Integer unikeBrukere;
    }
}
