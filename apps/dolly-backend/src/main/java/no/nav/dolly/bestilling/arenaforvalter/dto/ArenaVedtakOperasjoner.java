package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@Builder
public class ArenaVedtakOperasjoner {

    private LocalDate registrertDato;

    private Operasjon aa115;
    private Operasjon aapVedtak;
    private Operasjon dagpengeVedtak;

    @Data
    @Builder
    public static class Operasjon {

        private Periode avslutteVedtak;

        private boolean eksisterendeVedtak;

        private Periode nyttVedtak;
    }

    @Data
    @SuperBuilder
    public static class Periode {

        private LocalDate fom;
        private LocalDate tom;
    }
}
