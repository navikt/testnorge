package no.nav.dolly.domain.resultset.inst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsInstdataKdi {

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
    public static class Innsettelse extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Loeslatelse extends Hendelse {

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
    public static class AvbruddStart extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
        private LocalDateTime forventetAvbruddStartTidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddSlutt extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForventetLoeslatelse extends Hendelse {

        private String innmeldingHendelseId;
        private LocalDateTime tidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Annullering extends Hendelse {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class Hendelse {

        private Map<String, Version> version;

        public Map<String, Version> getVersion() {

            if (isNull(version)) {
                version = new HashMap<>();
            }
            return version;
        }
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Version {

        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
    }
}