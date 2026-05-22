package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstdataKdiDTO {

    private String environment;
    private Data data;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private List<Innsettelse> innsettelse;
        private List<Loeslatelse> loeslatelse;
        private List<AvbruddStart> avbruddStart;
        private List<AvbruddSlutt> avbruddSlutt;
        private List<ForventetLoeslatelse> forventetLoeslatelse;
        private List<Annullering> annullering;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Innsettelse {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Loeslatelse {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
        private Boolean erOverfoertTilUtlandskfengsel;
        private Boolean erOverfoertTilVaretektMedElektroniskKontroll;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddStart {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
        private LocalDateTime forventetAvbruddStartTidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddSlutt {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForventetLoeslatelse {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String innmeldingHendelseId;
        private String norskident;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Annullering {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
    }
}