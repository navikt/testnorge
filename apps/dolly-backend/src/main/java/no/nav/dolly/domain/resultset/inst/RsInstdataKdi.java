package no.nav.dolly.domain.resultset.inst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsInstdataKdi {

    private List<Innsettelse> innsettelse;
    private List<Loeslatelse> loeslatelse;
    private List<AvbruddStart> avbruddStart;
    private List<AvbruddSlutt> avbruddSlutt;
    private List<ForventetLoeslatelse> forventetLoeslatelse;
    private List<Annullering> annullering;

    public List<Innsettelse> getInnsettelse() {

        if (isNull(innsettelse)) {
            innsettelse = new ArrayList<>();
        }
        return innsettelse;
    }

    public List<Loeslatelse> getLoeslatelse() {

        if (isNull(loeslatelse)) {
            loeslatelse = new ArrayList<>();
        }
        return loeslatelse;
    }

    public List<AvbruddStart> getAvbruddStart() {

        if (isNull(avbruddStart)) {
            avbruddStart = new ArrayList<>();
        }
        return avbruddStart;
    }

    public List<AvbruddSlutt> getAvbruddSlutt() {

        if (isNull(avbruddSlutt)) {
            avbruddSlutt = new ArrayList<>();
        }
        return avbruddSlutt;
    }

    public List<ForventetLoeslatelse> getForventetLoeslatelse() {

        if (isNull(forventetLoeslatelse)) {
            forventetLoeslatelse = new ArrayList<>();
        }
        return forventetLoeslatelse;
    }

    public List<Annullering> getAnnullering() {

        if (isNull(annullering)) {
            annullering = new ArrayList<>();
        }
        return annullering;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Innsettelse extends Hendelse {

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
    public static class Loeslatelse extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
        private Boolean erOverfoertTilUtlandskfengsel;
        private Boolean erOverfoertTilVaretektMedElektroniskKontroll;
    }

    @lombok.Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvbruddStart extends Hendelse {

        private String kategori;
        private String organisasjonsnummer;
        private LocalDateTime tidspunkt;
        private LocalDateTime forventetAvbruddSluttTidspunkt;
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
    public static class ForventetLoeslatelse extends Hendelse {

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

        private String annullertMeldingId;
    }

    @lombok.Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class Hendelse {

        private String meldingId;
        private String hendelseId;
        private LocalDateTime publiseringstidspunkt;
    }
}