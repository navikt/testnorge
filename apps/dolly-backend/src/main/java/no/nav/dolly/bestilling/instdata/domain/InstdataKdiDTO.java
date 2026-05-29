package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Innsettelse extends Hendelse{

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Loeslatelse extends Hendelse{

        private String kategori;
        private String organisasjonsnummer;
        private Boolean erOverfoertTilUtlandskfengsel;
        private Boolean erOverfoertTilVaretektMedElektroniskKontroll;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddStart extends Hendelse{

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime forventetAvbruddSluttTidspunkt;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddSlutt extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForventetLoeslatelse extends Hendelse{

        private String innmeldingHendelseId;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Annullering extends Hendelse {

        private LocalDateTime annullertMeldingId;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class Hendelse {

        private String meldingId;
        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
        private String norskident;
    }
}