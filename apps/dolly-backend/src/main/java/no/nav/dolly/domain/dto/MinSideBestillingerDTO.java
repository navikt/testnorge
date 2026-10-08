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
    private LocalDate dato;

    private Long antallNyeBestillinger;
    private Long antallGjenopprettedeBestillinger;
    private Integer antallNyePersoner;
    private Integer andelOpprettedeDollyPersoner;
    private Integer andelImporterteTestnorgePersoner;

    private List<Entry> kriterier;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String fagsystem;
        private Integer antall;
        private Map<String, Object> detaljer;
    }
}
