package no.nav.testnav.apps.tenorsearchservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@SuppressWarnings("java:S115")
public class TenorRequest {

    public enum VergeTjenestevirksomhet {
        Bank, Forsikringsselskap, Helfo, Husbanken, Inkassoselskap, Kartverket, Kommune,
        Kredittvurderingsselskap, Namsmannen, Nav, Oevrige, Pasientreiser, Skatteetaten, StatensInnkrevingssentral,
        Statsforvalter, Tingretten
    }

    public enum CoAdressenavnType {Bostedsadresse, DeltBosted, Oppholdsadresse, Postadresse, PostadresseIUtlandet}

    public enum BostedsadresseType {Matrikkel, Veiadresse, Ukjent}

    public enum IdentifikatorType {Foedselsnummer, DNummer, DNummerOgFoedselsnr}

    public enum Kjoenn {Mann, Kvinne}

    public enum Rolle {DagligLeder}

    public enum Personstatus {Bosatt, Doed, Forsvunnet, Foedselsregistrert, IkkeBosatt, Inaktiv, Midlertidig, Opphoert, Utflyttet}

    public enum Sivilstand {EnkeEllerEnkemann, Gift, GjenlevendePartner, RegistrertPartner, Separert, SeparertPartner, Skilt, SkiltPartner, Ugift, Uoppgitt}

    public enum UtenlandskPersonIdentifikasjon {UtenlandskIdentifikasjonsnummer, TaxIdentificationNumber, SocialSecurityNumber, UtlendingsmyndighetenesIdentifikasjonsnummer}

    public enum IdentitetsgrunnlagStatus {IkkeKontrollert, IngenStatus, Kontrollert}

    public enum Adressebeskyttelse {Fortrolig, StrengtFortrolig}

    public enum AdresseGradering {Ugradert, Klientadresse, Fortrolig}

    public enum Relasjon {Barn, Far, Medmor, Mor, Partner}

    public enum Skattemeldingstype {KunUtkast, UtkastOgFastsatt}

    public enum Inntektstype {Loennsinntekt, Naeringsinntekt, PensjonEllerTrygd, YtelseFraOffentlige}

    public enum AOrdningBeskrivelse {
        Alderspensjon, AlderspensjonSkjermingstillegg, AndreBeskrivelser,
        Arbeidsavklaringspenger, AvtalefestetPensjon, Bil, Bonus, DagpengerVedArbeidsloeshet, Ektefelletillegg,
        ElektroniskKommunikasjon, Fagforeningskontingent, FastBilgodtgjoerelse, Fastloenn, FastTillegg, Feriepenger,
        Foreldrepenger, IpaEllerIpsPeriodiskeYtelser, Kvalifiseringsstoenad, NyAvtalefestetPensjonPrivatSektor,
        PensjonOgLivrenterIArbeidsforhold, ReiseKostMedOvernattingPaaHybelMedKokEllerPrivat,
        ReiseKostMedOvernattingPaaHybelUtenKokEllerPensjonatEllerBrakke, Sykepenger, Timeloenn, Ufoeretrygd
    }

    public enum Forskuddstrekk {
        OrdinaertForskuddstrekk, Barnepensjon, KildeskattPaaPensjon, Svalbard,
        JanMayenOgBilandene, BetaltTrygdeavgiftTilJanMayen
    }

    public enum Skattepliktstype {SkattepliktTilNorge, SkattepliktTilSvalbard}

    public enum SaerskiltSkatteplikt {
        KildeskattepliktPaaLoenn, KildeskattepliktPaaPensjon,
        SkattepliktAvNaeringsdriftEiendomMv, SkattepliktEtterPetroleumsskatteloven,
        SkattepliktPaaLoennFraDenNorskeStatOpptjentIUtlandet, SkattepliktSomSjoemann,
        SkattepliktSomUtenrikstjenestemann, SkattepliktVedUtenriksoppholdINorskStatstjenesteEllerNato
    }

    public enum TilleggsskattType {
        FradragForTvangsmulkt, SkjerpetTilleggsskattFraUriktigeOpplysninger, TilleggsskattFraUriktigeOpplysninger,
        TilleggsskattFraManglendeInnlevering, SkjerpetTilleggsskattFraManglendeInnlevering
    }

    public enum Arbeidsforholdstype {
        OrdinaertArbeidsforhold, MaritimtArbeidsforhold, FrilanserOppdragstakerHonorarPersonerMm
    }

    public enum Oppgjoerstype {Fastland, Svalbard, KildeskattPaaLoenn}

    public enum Stadietype {Utkast, Fastsatt, Oppgjoer}

    public enum VergemaalType {
        EnsligMindreaarigAsylsoeker, EnsligMindreaarigFlyktning, ForvaltningUtenforVergemaal, MidlertidigForMindreaarig,
        MidlertidigForVoksen, Mindreaarig, StadfestetFremtidsfullmakt, Voksen
    }

    @Schema(description = "Personidentifikator, fødselsnummer eller d-nummer")
    private String identifikator;
    private IdentifikatorType identifikatorType;
    private DatoIntervall foedselsdato;
    private DatoIntervall doedsdato;
    private Kjoenn kjoenn;
    private Personstatus personstatus;
    private Sivilstand sivilstand;
    private List<UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon;
    private IdentitetsgrunnlagStatus identitetsgrunnlagStatus;
    private Adressebeskyttelse adressebeskyttelse;
    private Boolean harLegitimasjonsdokument;
    private Boolean harFalskIdentitet;
    private Boolean harNorskStatsborgerskap;
    private Boolean harFlereStatsborgerskap;
    private Boolean harEuEoesStatsborgerskap;
    private Boolean harNordenStatsborgerskap;
    private Boolean harTredjelandsStatsborgerskap;
    private Boolean harUtgaattStatsborgerskap;
    private Boolean harStatsborgerskapHistorikk;
    private Navn navn;
    private Adresser adresser;
    private Relasjoner relasjoner;
    private Hendelser hendelser;
    private Avansert avansert;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Avansert {

        private BostedsadresseType bostedsadresseType;
        private String bostedsadresse;
        private CoAdressenavnType coAdressenavnType;
        private String fornavn;
        private String mellomnavn;
        private String etternavn;
        @Schema(description = "Verdi hentes fra felles kodeverk Landkoder")
        private String foedeland;
        private String visningnavn;
        private String barnFnr;
        private String farFnr;
        private String morFnr;
        private VergeTjenestevirksomhet vergeTjenestevirksomhet;
        private VergemaalType vergemaalType;
        @Schema(description = "Verdi hentes fra felles kodeverk Landkoder")
        private List<String> statsborgerskap;
        private Boolean harBostedsadresseHistorikk;
        private Boolean harDoedfoedtBarn;
        private Boolean harForeldreMedSammeKjoenn;
        private Boolean harInnflytting;
        private Boolean harNavnHistorikk;
        private Boolean harOpphold;
        private Boolean harPostadresseIFrittFormat;
        private Boolean harPostadressePostboks;
        private Boolean harPostadresseVegadresse;
        private Boolean harPostboks;
        private Boolean harRelatertPersonUtenFolkeregisteridentifikator;
        private Boolean harRettsligHandleevne;
        private Boolean harSivilstandHistorikk;
        private Boolean harUtenlandskAdresseIFrittFormat;
        private Boolean harUtenlandskPostadressePostboks;
        private Boolean harUtenlandskPostadresseVegadresse;
        private Boolean harUtflytting;
    }

    private List<Rolle> roller;
    private Tjenestepensjonsavtale tjenestepensjonsavtale;
    private Skattemelding skattemelding;
    private Inntekt inntekt;
    private Skatteplikt skatteplikt;
    private Tilleggsskatt tilleggsskatt;
    private Arbeidsforhold arbeidsforhold;
    private BeregnetSkatt beregnetSkatt;
    private TestinnsendingSkattPerson testinnsendingSkattPerson;
    private SamletReskontroInnsyn samletReskontroInnsyn;
    private SummertSkattegrunnlag summertSkattegrunnlag;
    private SpesisfisertSummertSkattegrunnlag spesifisertSummertSkattegrunnlag;

    public List<UtenlandskPersonIdentifikasjon> getUtenlandskPersonIdentifikasjon() {

        if (isNull(utenlandskPersonIdentifikasjon)) {
            utenlandskPersonIdentifikasjon = new ArrayList<>();
        }
        return utenlandskPersonIdentifikasjon;
    }

    public List<Rolle> getRoller() {

        if (isNull(roller)) {
            roller = new ArrayList<>();
        }
        return roller;
    }

    @Data
    @NoArgsConstructor
    public static class DatoIntervall {

        @Schema(type = "string", format = "YYYY-MM-DD", example = "2018-07-01")
        private LocalDate fraOgMed;
        @Schema(type = "string", format = "YYYY-MM-DD", example = "2020-07-01")
        private LocalDate tilOgMed;
    }

    @Data
    @NoArgsConstructor
    public static class Intervall {

        private BigInteger fraOgMed;
        private BigInteger tilOgMed;
    }

    @Data
    @NoArgsConstructor
    public static class Navn {

        private Intervall navnLengde;
        private Boolean harFlereFornavn;
        private Boolean harMellomnavn;
        private Boolean harNavnSpesialtegn;
    }

    @Data
    @NoArgsConstructor
    public static class Adresser {

        @Schema(description = "Adressesøk, fritekst")
        private AdresseGradering adresseGradering;
        private BigInteger kommunenummer;
        private Boolean harBostedsadresse;
        private Boolean harOppholdAnnetSted;
        private Boolean harPostadresseNorge;
        private Boolean harPostadresseUtland;
        private Boolean harKontaktadresseDoedsbo;
        private Boolean harAdresseSpesialtegn;
    }

    @Data
    @NoArgsConstructor
    public static class Relasjoner {

        private Relasjon relasjon;
        private Intervall antallBarn;
        private Boolean harForeldreAnsvar;
        private Intervall relasjonMedFoedselsaar;
        private Boolean harDeltBosted;
        private Boolean harVergemaalEllerFremtidsfullmakt;
        private Boolean borMedMor;
        private Boolean borMedFar;
        private Boolean borMedMedmor;
        private Boolean foreldreHarSammeAdresse;
    }

    @Data
    @NoArgsConstructor
    public static class Hendelser {

        private Hendelse hendelse;
        private Hendelse sisteHendelse;
    }

    @Data
    @NoArgsConstructor
    public static class Tjenestepensjonsavtale {

        @Schema(description = "Pensjonsinnretningen organisasjonsnummer, 9 siffre")
        private String pensjonsinnretningOrgnr;
        @Schema(type = "string", format = "YYYY-MM", example = "2020-07")
        private YearMonth periode;
    }

    @Data
    @NoArgsConstructor
    public static class Skattemelding {

        @Schema(description = "Inntektsår, 4 siffre, årene 2018, 2019, 2020, 2021, 2022 ... osv opptil i forfjor")
        private BigInteger inntektsaar;
        private Skattemeldingstype skattemeldingstype;
    }

    @Data
    @NoArgsConstructor
    public static class Inntekt {

        private MonthInterval periode;
        private BigInteger opplysningspliktig;
        private List<Inntektstype> inntektstyper;
        private AOrdningBeskrivelse beskrivelse;
        private List<Forskuddstrekk> forskuddstrekk;
        private Boolean harHistorikk;

        public List<Inntektstype> getInntektstyper() {

            if (isNull(inntektstyper)) {
                inntektstyper = new ArrayList<>();
            }
            return inntektstyper;
        }

        public List<Forskuddstrekk> getForskuddstrekk() {

            if (isNull(forskuddstrekk)) {
                forskuddstrekk = new ArrayList<>();
            }
            return forskuddstrekk;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MonthInterval {

        @Schema(type = "string", format = "YYYY-MM", example = "2020-07")
        private YearMonth fraOgMed;
        @Schema(type = "string", format = "YYYY-MM", example = "2020-07")
        private YearMonth tilOgMed;
    }

    @Data
    @NoArgsConstructor
    public static class Skatteplikt {

        @Schema(description = "Inntektsår, 4 siffre, årene 2019, 2019, 2020, 2021, 2022, 2023 ... osv opptil i fjor")
        private BigInteger inntektsaar;
        private List<Skattepliktstype> skattepliktstyper;
        private SaerskiltSkatteplikt saerskiltSkatteplikt;

        public List<Skattepliktstype> getSkattepliktstyper() {

            if (isNull(skattepliktstyper)) {
                skattepliktstyper = new ArrayList<>();
            }
            return skattepliktstyper;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Tilleggsskatt {

        private BigInteger inntektsaar;
        private List<TilleggsskattType> tilleggsskattTyper;

        public List<TilleggsskattType> getTilleggsskattTyper() {

            if (isNull(tilleggsskattTyper)) {
                tilleggsskattTyper = new ArrayList<>();
            }
            return tilleggsskattTyper;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Arbeidsforhold {

        private DatoIntervall startDatoPeriode;
        private DatoIntervall sluttDatoPeriode;
        private Boolean harPermisjoner;
        private Boolean harPermitteringer;
        private Boolean harArbeidsgiver;
        private Boolean harTimerMedTimeloenn;
        private Boolean harUtenlandsopphold;
        private Boolean harHistorikk;
        private Arbeidsforholdstype arbeidsforholdstype;
    }

    @Data
    @NoArgsConstructor
    public static class BeregnetSkatt {

        @Schema(description = "Inntektsår, 4 siffre, årene 2018, 2019, 2020, 2021, 2022, 2023 ... osv opptil i fjor")
        private BigInteger inntektsaar;
        private Oppgjoerstype oppgjoerstype;
        private Boolean pensjonsgivendeInntekt;
    }

    @Data
    @NoArgsConstructor
    @Schema(description = "Opplysninger fra skatteetatens innsendingsmiljoe")
    public static class TestinnsendingSkattPerson {

        @Schema(description = "Inntektsår, 4 siffre, årene 2020, 2021, 2022, 2023 ... osv opptil i fjor")
        private BigInteger inntektsaar;
        @Schema(description = "Skattemelding utkast, merk at false indikerer har ikke skatteMeldingUtkast")
        private Boolean harSkattemeldingUtkast;
        @Schema(description = "Skattemelding fastsatt, merk at false indikerer har ikke skatteMeldingFastsatt")
        private Boolean harSkattemeldingFastsatt;
    }

    @Data
    @NoArgsConstructor
    public static class SamletReskontroInnsyn {

        private Boolean harKrav;
        private Boolean harInnbetaling;
    }

    @Data
    @NoArgsConstructor
    public static class SummertSkattegrunnlag {

        @Schema(description = "Inntektsår, 4 siffre, årene 2019, 2020, 2021, 2022, 2023 ... osv opptil i fjor")
        private BigInteger inntektsaar;
        private Stadietype stadietype;
        private Oppgjoerstype oppgjoerstype;
        private TekniskNavn tekniskNavn;
        private Intervall alminneligInntektFoerSaerfradragBeloep;
    }

    @Data
    @NoArgsConstructor
    public static class SpesisfisertSummertSkattegrunnlag {

        @Schema(description = "Inntektsår, 4 siffre, årene 2019, 2020, 2021, 2022, 2023 ... osv opptil i fjor")
        private BigInteger inntektsaar;
        private Stadietype stadietype;
        private Oppgjoerstype oppgjoerstype;
        private TekniskNavn tekniskNavn;
        private Spesifiseringstype spesifiseringstype;
        private Intervall alminneligInntektFoerSaerfradragBeloep;
    }
}
