package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdResponse {

    private Ansettelsesperiode ansettelsesperiode;
    private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;
    private List<Arbeidsavtale> arbeidsavtaler;
    private String arbeidsforholdId;
    private Arbeidsgiver arbeidsgiver;
    private Arbeidstaker arbeidstaker;
    private boolean innrapportertEtterAOrdningen;
    private Long navArbeidsforholdId;
    private Arbeidsgiver opplysningspliktig;
    private Fartoy fartoy;
    private List<PermisjonPermittering> permisjonPermitteringer;
    private LocalDateTime registrert;
    private LocalDateTime sistBekreftet;
    private String type;
    private List<Utenlandsopphold> utenlandsopphold;

    public List<PermisjonPermittering> getPermisjonPermitteringer() {
        if (isNull(permisjonPermitteringer)) {
            permisjonPermitteringer = new ArrayList<>();
        }
        return permisjonPermitteringer;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Utenlandsopphold {

        private String landkode;
        private Periode periode;
        private String rapporteringsperiode; // yyyy-mm
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisjonPermittering {

        private Periode periode;
        private String permisjonPermitteringId;
        private Double prosent;
        private String type;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsgiver {

        private Aktoer type;
        private String organisasjonsnummer;
        private String offentligIdent;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidstaker {

        private Aktoer type;
        private String offentligIdent;
        private String aktoerId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsavtale {

        private Double antallTimerPrUke;
        private String ansettelsesform;
        private String arbeidstidsordning;
        private Double beregnetAntallTimerPrUke;
        private Periode bruksperiode;
        private Periode gyldighetsperiode;
        private String sistLoennsendring;
        private LocalDate sistStillingsendring;
        private Double stillingsprosent;
        private String yrke;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AntallTimerForTimeloennet {

        private Double antallTimer;
        private Periode periode;
        private String rapporteringsperiode; // yyyy-mm
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ansettelsesperiode {

        private Periode bruksperiode;
        private Periode periode;
        private String sluttaarsak;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        private LocalDate fom;
        private LocalDate tom;
    }
}


