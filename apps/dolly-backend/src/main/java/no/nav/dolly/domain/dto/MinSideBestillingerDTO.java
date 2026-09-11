package no.nav.dolly.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinSideBestillingerDTO {

    private YearMonth periode;
    private Long antallNyBestillinger;
    private Long antallGjenopprettinger;
    private Integer antallNyePersoner;

    private LocalDate dato;
    private List<DashboardAdferdDTO.Entry> kriterier;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String fagsystem;
        private Integer antall;
        private Map<String, String> detaljer;
    }
}
