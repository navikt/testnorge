package no.nav.testnav.apps.tenorsearchservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonSelectOptions.Grunnlagsdata;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorOrganisasjonRequest {

    private String organisasjonsnummer;
    private Organisasjonsform organisasjonsform;
    private Adresse forretningsadresse;
    private Boolean harUtenlandskForretningsadresse;
    private Boolean harUtenlandskPostadresse;
    private String naeringBeskrivelse;
    private String naeringKode;
    private Boolean registrertIMvaregisteret;
    private Boolean registrertIForetaksregisteret;
    private Boolean registrertIFrivillighetsregisteret;
    private EnhetStatus enhetStatuser;
    private Boolean slettetIEnhetsregisteret;
    private TenorRequest.Intervall antallAnsatte;
    private Boolean revisorer;
    private Boolean regnskapsfoerere;
    private Boolean dagligLeder;
    private Boolean styremedlemmer;
    private Boolean forretningsfoerer;
    private Boolean kontaktpersoner;
    private Boolean norsk_representant;
    private ErUnderenhet erUnderenhet;
    private Boolean harUnderenheter;
    private Integer antallUnderenheter;
    private TenorRelasjoner tenorRelasjoner;

    public enum OrganisasjonsForm {
        Alderspensjon, AlderspensjonSkjermingstillegg, AndreBeskrivelser,
        Arbeidsavklaringspenger, AvtalefestetPensjon, Bil, Bonus, DagpengerVedArbeidsloeshet, Ektefelletillegg,
        ElektroniskKommunikasjon, Fagforeningskontingent, FastBilgodtgjoerelse, Fastloenn, FastTillegg, Feriepenger,
        Foreldrepenger, IpaEllerIpsPeriodiskeYtelser, Kvalifiseringsstoenad, NyAvtalefestetPensjonPrivatSektor,
        PensjonOgLivrenterIArbeidsforhold, ReiseKostMedOvernattingPaaHybelMedKokEllerPrivat,
        ReiseKostMedOvernattingPaaHybelUtenKokEllerPensjonatEllerBrakke, Sykepenger, Timeloenn, Ufoeretrygd
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjonsform {

        private TenorOrganisasjonSelectOptions.OrganisasjonForm kode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {
        private String kommunenummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnhetStatus {

        private TenorOrganisasjonSelectOptions.EnhetStatus kode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErUnderenhet {

        private String overenhet;
    }

    @Data
    @NoArgsConstructor
    public static class TestinnsendingSkattEnhet {
        private BigInteger inntektsaar;
        private Boolean harSkattemeldingUtkast;
        private Boolean harSkattemeldingFastsatt;
        private Boolean harSelskapsmeldingUtkast;
        private Boolean harSelskapsmeldingFastsatt;
        private Grunnlagsdata manglendeGrunnlagsdata;
        private Grunnlagsdata manntall;
    }

    @Data
    @NoArgsConstructor
    public static class Arbeidsforhold {
        @Schema(type = "string", format = "YYYY-MM-DD", example = "2018-07-01")
        private LocalDate startDato;
        @Schema(type = "string", format = "YYYY-MM-DD", example = "2018-07-01")
        private LocalDate sluttDato;
        private Boolean harPermisjoner;
        private Boolean harPermitteringer;
        private Boolean harTimerMedTimeloenn;
        private Boolean harUtenlandsopphold;
        private Boolean harHistorikk;
        private TenorOrganisasjonSelectOptions.ArbeidsforholdType arbeidsforholdtype;
    }

    @Data
    @NoArgsConstructor
    public static class SamletReskontroinnsyn {
        private Boolean harKrav;
        private Boolean harInnbetaling;
    }

    @Data
    @NoArgsConstructor
    public static class TjenestepensjonsavtaleOpplysningspliktig {
        private String tjenestepensjonsinnretningOrgnr;
        private String periode;
    }

    @Data
    @NoArgsConstructor
    public static class TenorRelasjoner {
        private TestinnsendingSkattEnhet testinnsendingSkattEnhet;
        private Arbeidsforhold arbeidsforhold;
        private SamletReskontroinnsyn samletReskontroinnsyn;
        private TjenestepensjonsavtaleOpplysningspliktig tjenestepensjonsavtaleOpplysningspliktig;
    }

    @Data
    @NoArgsConstructor
    public static class Intervall {

        private BigInteger fraOgMed;
        private BigInteger tilOgMed;
    }
}