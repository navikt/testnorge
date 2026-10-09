package no.nav.dolly.bestilling.henvendelse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HenvendelseResponse {

    private HttpStatus status;
    private String type;
    private String feilmelding;

    @Builder.Default
    private List<Meldinger> meldinger = new ArrayList<>();

    @Builder.Default
    private List<Info> data = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {

        private String henvendelseType;
        private String fnr;
        private String aktorId;
        private LocalDateTime avsluttetDato;
        private LocalDateTime kasseringsDato;
        private String avsluttetAv;
        private String sattTilSladdingAv;
        private Boolean sladding;
        private Boolean feilsendt;
        private String kjedeId;
        private String gjeldendeTemagruppe;
        private String gjeldendeTema;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meldinger {

    }
}