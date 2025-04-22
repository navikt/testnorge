package no.nav.dolly.bestilling.inntektsmelding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaksjonmappingIdDTO {

    private String journalpostId;
    private String dokumentInfoId;
    private Dokument dokument;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dokument {

        private String journalpostId;
        private String dokumentInfoId;
    }
}
