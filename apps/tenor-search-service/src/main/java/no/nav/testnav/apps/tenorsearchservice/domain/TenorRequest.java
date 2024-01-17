package no.nav.testnav.apps.tenorsearchservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class TenorRequest {

    public enum IdentifikatorType {FNR, DNR, FNR_TIDLIGERE_DNR}

    public enum Kjoenn {Mann, Kvinne}

    public enum Roller {DAGLIG_LEDER}

    public enum Personstatus {Bosatt, Doed, Forsvunnet, Foedselsregistrert, IkkeBosatt, Inaktiv, Midlertidig, Opphoert, Utflyttet}

    public enum Sivilstatus {EnkeEllerEnkemann, Gift, GjenlevendePartner, RegistrertPartner, Separert, SeparertPartner, Skilt, SkiltPartner, Ugift, Uoppgitt}

    public enum UtenlandskPersonIdentifikasjon {UtenlandskIdentifikasjonsnummer, TaxIdentificationNumber, SocialSecurityNumber, UtlendingsmyndighetenesIdentifikasjonsnummer}

    public enum IdentitetsgrunnlagStatus {IkkeKontrollert, IngenStatus, Kontrollert}

    public enum Adressebeskyttelse {Fortrolig, StrengtFortrolig}

    public enum AdresseGradering {Ugradert, Klientadresse, Fortrolig}

    public enum Relasjon {Barn, Far, Medmor, Mor, Partner}

    public enum Hendelse {
        EndringIAdressebeskyttelse, EndringIBostedsadresse, EndringIBostedsadresseUtenFlytting,
        EndringIBrukAvSamiskSpraak, EndringIDeltBosted, EndringIDoedsfall, EndringIFalskIdentitet, EndringIFamilierelasjon,
        EndringIForeldreansvar, EndringIFratattRettsligHandleevne, EndringIFoedsel, EndringIFoedselINorge,
        EndringIIdentifikasjonsnummer, EndringIIdentitetsgrunnlag, EndringIInnflytting, EndringIKjoenn,
        EndringIKontaktinformasjon, EndringIKontaktopplysningerForDoedsbo, EndringILegitimasjonsdokument,
        EndringINavn, EndringIOpphold, EndringIOppholdPaaSvalbard, EndringIOppholdsadresse, EndringIPerson,
        EndringIRettsligHandleevne, EndringISametingetsValgmanntall, EndringISivilstand, EndringIStatsborgerskap,
        EndringIStatus, EndringIUtenlandskPersonidentifikasjon, EndringIUtflytting,
        EndringIUtlendingsmyndighetenesIdentifikasjonsnummer, EndringIVergemaal, PersonErBosatt, PersonErDod,
        PersonErEndretVedSplitting, PersonErGjenopprettetVedSplitting, PersonErOppdatert, PersonErOpphoert,
        PersonErOpphoertSomDublett, PersonErOpprettet, PersonErReaktivert, PersonErUtflyttet,
        PersonErViderefoertSomGjeldendeVedSammenslaaing
    }

    public enum BostedsadresseType {Vegadresse, Matrikkeladresse, Ukjent}

    public enum CoAdresseType {Bostedsadresse, DeltBosted, Oppholdsadresse, Postadresse, PostadresseIUtlandet}

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

    @Schema(description = "Personidentifikator, fødselsnummer eller d-nummer")
    private String identifikator;
    private IdentifikatorType identifikatorType;
    private DatoIntervall foedselsdato;
    private DatoIntervall doedsdato;
    private Kjoenn kjoenn;
    private Personstatus personstatus;
    private Sivilstatus sivilstatus;
    private List<UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon;
    private IdentitetsgrunnlagStatus identitetsgrunnlagStatus;
    private Adressebeskyttelse adressebeskyttelse;
    private Boolean harLegitimasjonsdokument;
    private Boolean harFalskIdentitet;
    private Boolean harNorskStatsborgerskap;
    private Boolean harFlereStatsborgerskap;
    private Navn navn;
    private Adresser adresser;
    private Relasjoner relasjoner;
    private Hendelser hendelser;

    private List<Roller> roller;
    private Tjenestepensjonsavtale tjenestepensjonsavtale;
    private Skattemelding skattemelding;
    private InntektAordningen inntektAordningen;
    private Skatteplikt skatteplikt;
    private Tilleggsskatt tilleggsskatt;
    private Arbeidsforhold arbeidsforhold;
    private BeregnetSkatt beregnetSkatt;
    private SummertSkattegrunnlag summertSkattegrunnlag;
    private OpplysningerFraSkatteetatensInnsendingsmiljoe opplysningerFraSkatteetatensInnsendingsmiljoe;
    private SamletReskontroInnsyn samletReskontroInnsyn;
    private SpesisfisertSummertSkattegrunnlag spesisfisertSummertSkattegrunnlag;

    public List<UtenlandskPersonIdentifikasjon> getUtenlandskPersonIdentifikasjon() {

        if (isNull(utenlandskPersonIdentifikasjon)) {
            utenlandskPersonIdentifikasjon = new ArrayList<>();
        }
        return utenlandskPersonIdentifikasjon;
    }

    public List<Roller> getRoller() {

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

        private Integer fraOgMed;
        private Integer tilOgMed;
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
        private String bostedsadresseFritekst;
        private AdresseGradering adresseGradering;
        private Integer kommunenummer;
        private Boolean harBostedsadresse;
        private Boolean harOppholdAnnetSted;
        private Boolean harPostadresseNorge;
        private Boolean harPostadresseUtland;
        private Boolean harKontaktadresseDoedsbo;
        private Boolean harAdresseSpesialtegn;
        private BostedsadresseType bostedsadresseType;
        private CoAdresseType coAdresseType;
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
        private Integer inntektsaar;
        private Skattemeldingstype skattemeldingstype;
    }

    @Data
    @NoArgsConstructor
    public static class InntektAordningen {

        private MonthInterval periode;
        private String opplysningspliktig;
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
        private Integer inntektsaar;
        private List<Skattepliktstype> Skattepliktstyper;
        private SaerskiltSkatteplikt saerskiltSkatteplikt;

        public List<Skattepliktstype> getSkattepliktstyper() {

            if (isNull(Skattepliktstyper)) {
                Skattepliktstyper = new ArrayList<>();
            }
            return Skattepliktstyper;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Tilleggsskatt {

        private Integer inntektsaar;
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
        private Integer inntektsaar;
        private Oppgjoerstype oppgjoerstype;
        private Boolean pensjonsgivendeInntekt;
    }

    @Data
    @NoArgsConstructor
    public static class SummertSkattegrunnlag {
    }

    @Data
    @NoArgsConstructor
    public static class OpplysningerFraSkatteetatensInnsendingsmiljoe {
    }

    @Data
    @NoArgsConstructor
    public static class SamletReskontroInnsyn {
    }

    @Data
    @NoArgsConstructor
    public static class SpesisfisertSummertSkattegrunnlag {
    }
}
