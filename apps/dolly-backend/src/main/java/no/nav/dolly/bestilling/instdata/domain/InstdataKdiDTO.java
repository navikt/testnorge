package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstdataKdiDTO {

    private String ident;
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
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Innsettelse extends Hendelse{

        private String kategori;
        private String organisasjonsnummer;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Loeslatelse extends Hendelse{

        private String kategori;
        private String organisasjonsnummer;
        private Boolean erOverfoertTilUtlandskfengsel;
        private Boolean erOverfoertTilVaretektMedElektroniskKontroll;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddStart extends Hendelse{

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime forventetAvbruddStartTidspunkt;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddSlutt extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForventetLoeslatelse extends Hendelse{

        private String innmeldingHendelseId;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Annullering {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class Hendelse {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
        private LocalDateTime tidspunkt;
    }
}