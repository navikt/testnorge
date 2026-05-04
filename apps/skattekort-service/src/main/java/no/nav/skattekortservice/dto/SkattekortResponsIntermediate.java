package no.nav.skattekortservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("java:S115")
public class SkattekortResponsIntermediate {

    private SkattekortTilArbeidsgiver skattekortTilArbeidsgiver;

    @Getter
    @RequiredArgsConstructor
    public enum Resultatstatus {

        ikkeSkattekort("IKKE_SKATTEKORT"),
        vurderArbeidstillatelse("VURDER_ARBEIDSTILLATELSE"),
        ikkeTrekkplikt("IKKE_TREKKPLIKT"),
        skattekortopplysningerOK("SKATTEKORTOPPLYSNINGER_OK"),
        ugyldigOrganisasjonsnummer("UGYLDIG_ORGANISASJONSNUMMER"),
        ugyldigFoedselsEllerDnummer("UGYLDIG_FOEDSELS_ELLER_DNUMMER"),
        utgaattDnummerSkattekortForFoedselsnummerErLevert("UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT");

        private final String value;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Tilleggsopplysning {

        oppholdPaaSvalbard("OPPHOLD_PAA_SVALBARD"),
        kildeskattpensjonist("KILDESKATTPENSJONIST"),
        oppholdITiltakssone("OPPHOLD_I_TILTAKSSONE"),
        kildeskattPaaLoenn("KILDESKATT_PAA_LOENN");

        private final String value;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Tabelltype {

        trekktabellForPensjon("TREKKTABELL_FOR_PENSJON"),
        trekktabellForLoenn("TREKKTABELL_FOR_LOENN");

        private final String value;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Trekkode {

        loennFraHovedarbeidsgiver("LOENN_FRA_HOVEDARBEIDSGIVER"),
        loennFraBiarbeidsgiver("LOENN_FRA_BIARBEIDSGIVER"),
        loennFraNAV("LOENN_FRA_NAV"),
        pensjon("PENSJON"),
        pensjonFraNAV("PENSJON_FRA_NAV"),
        loennTilUtenrikstjenestemann("LOENN_TIL_UTENRIKSTJENESTEMANN"),
        loennKunTrygdeavgiftTilUtenlandskBorger("LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER"),
        loennKunTrygdeavgiftTilUtenlandskBorgerSomGrensegjenger("LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER"),
        ufoeretrygdFraNAV("UFOERETRYGD_FRA_NAV"),
        ufoereytelserFraAndre("UFOEREYTELSER_FRA_ANDRE"),
        introduksjonsstoenad("INTRODUKSJONSSTOENAD");

        private final String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkattekortTilArbeidsgiver {

        private List<Arbeidsgiver> arbeidsgiver;

        public List<Arbeidsgiver> getArbeidsgiver() {

            if (isNull(arbeidsgiver)) {
                arbeidsgiver = new ArrayList<>();
            }
            return arbeidsgiver;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsgiver {

        private IdentifikatorForEnhetEllerPerson arbeidsgiveridentifikator;
        private List<Skattekortmelding> arbeidstaker;

        public List<Skattekortmelding> getArbeidstaker() {

            if (isNull(arbeidstaker)) {
                arbeidstaker = new ArrayList<>();
            }
            return arbeidstaker;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skattekortmelding {

        private String arbeidstakeridentifikator;
        private Resultatstatus resultatPaaForespoersel;
        private Skattekort skattekort;
        private List<Tilleggsopplysning> tilleggsopplysning;
        private Integer inntektsaar;

        public List<Tilleggsopplysning> getTilleggsopplysning() {

            if (isNull(tilleggsopplysning)) {
                tilleggsopplysning = new ArrayList<>();
            }
            return tilleggsopplysning;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skattekort {

        private LocalDate utstedtDato;
        private Long skattekortidentifikator;
        private List<Forskuddstrekk> forskuddstrekk;

        public List<Forskuddstrekk> getForskuddstrekk() {

            if (isNull(forskuddstrekk)) {
                forskuddstrekk = new ArrayList<>();
            }
            return forskuddstrekk;
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "xsi:type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Frikort.class, name = "Frikort"),
            @JsonSubTypes.Type(value = Trekktabell.class, name = "Trekktabell"),
            @JsonSubTypes.Type(value = Trekkprosent.class, name = "Trekkprosent")
    })
    public abstract static class Forskuddstrekk {

        private Trekkode trekkode;
        private String type;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Frikort extends Forskuddstrekk {

        private Integer frikortbeloep;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Trekktabell extends Forskuddstrekk {

        private Tabelltype tabelltype;
        private String tabellnummer;
        private Integer prosentsats;
        private Integer antallMaanederForTrekk;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Trekkprosent extends Forskuddstrekk {

        private Trekkode trekkode;

        private Integer prosentsats;
        private Integer antallMaanederForTrekk;
    }
}
