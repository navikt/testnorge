package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.PeriodeDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PermisjonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arbeidsforhold {

    private RsPeriodeAareg ansettelsesPeriode;

    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    private RsArbeidsavtale arbeidsavtale;

    private String arbeidsforholdID;

    private Long arbeidsforholdIDnav;

    private String arbeidsforholdstype;

    private RsAktoer arbeidsgiver;

    private List<RsFartoy> fartoy;

    private RsPersonAareg arbeidstaker;

    private List<PermisjonDTO> permisjon;

    private List<RsUtenlandsopphold> utenlandsopphold;

    public List<RsFartoy> getFartoy() {
        if (isNull(fartoy)) {
            fartoy = new ArrayList<>();
        }
        return fartoy;
    }

    public List<RsAntallTimerIPerioden> getAntallTimerForTimeloennet() {
        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    public List<RsUtenlandsopphold> getUtenlandsopphold() {
        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public List<PermisjonDTO> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsPeriodeAareg {

        @Schema(description = "Dato fra-og-med",
                type = "LocalDateTime",
                required = true)
        private LocalDate fom;

        @Schema(description = "Dato til-og-med",
                type = "LocalDateTime")
        private LocalDate tom;

        @Schema(description = "Samlet liste over periode",
                type = "List")
        private List<String> periode;

        public List<String> getPeriode() {
            if (isNull(periode)) {
                periode = new ArrayList<>();
            }
            return periode;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsArbeidsavtale {

        private Integer antallKonverterteTimer;

        @Schema(description = "Gyldige verdier finnes i kodeverk 'Arbeidstidsordninger'",
                type = "String",
                required = true)
        private String arbeidstidsordning;

        private Double avtaltArbeidstimerPerUke;

        private String avloenningstype;

        @Schema(type = "LocalDateTime")
        private LocalDate endringsdatoStillingsprosent;

        private LocalDate sisteLoennsendringsdato;

        private Double stillingsprosent;

        @Schema(description = "Gyldige verdier finnes i kodeverk 'Yrker'",
                type = "String",
                required = true)
        private String yrke;

        @Schema(description = "Gyldige verdier finnes i kodeverk 'AnsettelsesformAaareg'",
                type = "String")
        private String ansettelsesform;

        @Schema(type = "LocalDateTime")
        private LocalDate endringsdatoLoenn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsAntallTimerIPerioden {

        @Schema(required = true)
        private Arbeidsforhold.RsPeriodeAareg periode;

        @Schema(required = true)
        private BigDecimal antallTimer;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "aktoertype")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = RsOrganisasjon.class, name = "ORG"),
            @JsonSubTypes.Type(value = RsAktoerPerson.class, name = "PERS")
    })
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static abstract class RsAktoer {

        @Schema(description = "Type av aktør er ORG eller PERS for organisasjon eller person",
                required = true)

        private String aktoertype;

        public abstract String getAktoertype();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsAktoerPerson extends RsAktoer {

        @Schema(required = true,
                description = "Personident/fødselsnummer")
        private String ident;

        @Schema(required = true,
                description = "Gyldige verdier finnes i kodeverk 'Personidenter'")
        private String identtype;

        @Override
        public String getAktoertype() {
            return "PERS";
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsOrganisasjon extends RsAktoer {

        @Schema(description = "Organisasjonsnummer, må finnes i EREG",
                required = true)
        private String orgnummer;

        @Override
        public String getAktoertype() {
            return "ORG";
        }

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Permisjon {

        private String permisjonsId;

        private RsPeriodeAareg permisjonsPeriode;

        private BigDecimal permisjonsprosent;

        private String permisjonOgPermittering;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsFartoy {

        private String skipsregister;
        private String skipstype;
        private String fartsomraade;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsPersonAareg {

        @Schema(description = "Personident/fødselsnummer")
        private String ident;

        @Schema(description = "Gyldige verdier finnes i kodeverk 'Personidenter'")
        private String identtype;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsUtenlandsopphold {

        @Schema(required = true)
        private PeriodeDTO periode;

        @Schema(description = "Gyldige verdier finnes i kodeverk 'LandkoderISO2'",
                required = true)
        private String land;
    }
}
