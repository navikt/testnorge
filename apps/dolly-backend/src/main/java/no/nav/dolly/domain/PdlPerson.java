package no.nav.dolly.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.dolly.domain.deserialization.PersonStatusEnumDeserializer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.PdlPerson.Rolle.BARN;
import static no.nav.dolly.domain.PdlPerson.Rolle.FAR;
import static no.nav.dolly.domain.PdlPerson.Rolle.MEDMOR;
import static no.nav.dolly.domain.PdlPerson.Rolle.MOR;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson {

    private Data data;

    public enum Gruppe {AKTORID, FOLKEREGISTERIDENT, NPID}

    public enum Rolle {FAR, MOR, MEDMOR, BARN}

    public enum SivilstandType {
        UOPPGITT, UGIFT, GIFT, ENKE_ELLER_ENKEMANN, SKILT, SEPARERT, REGISTRERT_PARTNER,
        SEPARERT_PARTNER, SKILT_PARTNER, GJENLEVENDE_PARTNER
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private HentIdenter hentIdenter;
        private Person hentPerson;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentIdenter {

        private List<Identer> identer;

        public List<Identer> getIdenter() {
            if (isNull(identer)) {
                identer = new ArrayList<>();
            }
            return identer;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identer {

        private String ident;
        private boolean historisk;
        private Gruppe gruppe;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {

        private List<PdlPerson.Navn> navn;
        private List<PdlPerson.Foedselsdato> foedselsdato;
        private List<FoedestedDTO> foedested;
        private List<PdlPerson.ForelderBarnRelasjon> forelderBarnRelasjon;
        private List<PdlPerson.Sivilstand> sivilstand;
        private List<PdlPerson.Doedsfall> doedsfall;
        private List<PdlPerson.UtflyttingFraNorge> utflyttingFraNorge;
        private List<InnflyttingDTO> innflyttingTilNorge;
        private List<PdlPerson.PdlKjoenn> kjoenn;
        private List<PdlPerson.Folkeregisteridentifikator> folkeregisteridentifikator;
        private List<BostedadresseDTO> bostedsadresse;
        private List<Kontaktadresse> kontaktadresse;
        private List<OppholdsadresseDTO> oppholdsadresse;
        private List<DeltBostedDTO> deltBosted;
        private List<ForeldreansvarDTO> foreldreansvar;
        private List<KontaktinformasjonForDoedsboDTO> kontaktinformasjonForDoedsbo;
        private List<UtenlandskIdentifikasjonsnummerDTO> utenlandskIdentifikasjonsnummer;
        private FalskIdentitetDTO falskIdentitet;
        private List<AdressebeskyttelseDTO> adressebeskyttelse;
        private List<FolkeregisterPersonstatus> folkeregisterpersonstatus;
        private List<TilrettelagtKommunikasjonDTO> tilrettelagtKommunikasjon;
        private List<PdlPerson.Statsborgerskap> statsborgerskap;
        private List<OppholdDTO> opphold;
        private List<TelefonnummerDTO> telefonnummer;
        private List<FullmaktDTO> fullmakt;
        private List<Vergemaal> vergemaalEllerFremtidsfullmakt;
        private List<SikkerhetstiltakDTO> sikkerhetstiltak;
        private List<DoedfoedtBarnDTO> doedfoedtBarn;

        public List<PdlPerson.Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }

        public List<PdlPerson.ForelderBarnRelasjon> getForelderBarnRelasjon() {
            if (isNull(forelderBarnRelasjon)) {
                forelderBarnRelasjon = new ArrayList<>();
            }
            return forelderBarnRelasjon;
        }

        public List<PdlPerson.Sivilstand> getSivilstand() {
            if (isNull(sivilstand)) {
                sivilstand = new ArrayList<>();
            }
            return sivilstand;
        }

        public List<PdlPerson.Doedsfall> getDoedsfall() {
            if (isNull(doedsfall)) {
                doedsfall = new ArrayList<>();
            }
            return doedsfall;
        }

        public List<PdlPerson.UtflyttingFraNorge> getUtflyttingFraNorge() {
            if (isNull(utflyttingFraNorge)) {
                utflyttingFraNorge = new ArrayList<>();
            }
            return utflyttingFraNorge;
        }

        public List<PdlPerson.PdlKjoenn> getKjoenn() {
            if (isNull(kjoenn)) {
                kjoenn = new ArrayList<>();
            }
            return kjoenn;
        }

        public List<PdlPerson.Folkeregisteridentifikator> getFolkeregisteridentifikator() {
            if (isNull(folkeregisteridentifikator)) {
                folkeregisteridentifikator = new ArrayList<>();
            }
            return folkeregisteridentifikator;
        }

        public List<BostedadresseDTO> getBostedsadresse() {
            if (isNull(bostedsadresse)) {
                bostedsadresse = new ArrayList<>();
            }
            return bostedsadresse;
        }

        public List<Kontaktadresse> getKontaktadresse() {
            if (isNull(kontaktadresse)) {
                kontaktadresse = new ArrayList<>();
            }
            return kontaktadresse;
        }

        public List<OppholdsadresseDTO> getOppholdsadresse() {
            if (isNull(oppholdsadresse)) {
                oppholdsadresse = new ArrayList<>();
            }
            return oppholdsadresse;
        }

        public List<DeltBostedDTO> getDeltBosted() {
            if (isNull(deltBosted)) {
                deltBosted = new ArrayList<>();
            }
            return deltBosted;
        }

        public List<ForeldreansvarDTO> getForeldreansvar() {
            if (isNull(foreldreansvar)) {
                foreldreansvar = new ArrayList<>();
            }
            return foreldreansvar;
        }

        public List<KontaktinformasjonForDoedsboDTO> getKontaktinformasjonForDoedsbo() {
            if (isNull(kontaktinformasjonForDoedsbo)) {
                kontaktinformasjonForDoedsbo = new ArrayList<>();
            }
            return kontaktinformasjonForDoedsbo;
        }

        public List<UtenlandskIdentifikasjonsnummerDTO> getUtenlandskIdentifikasjonsnummer() {
            if (isNull(utenlandskIdentifikasjonsnummer)) {
                utenlandskIdentifikasjonsnummer = new ArrayList<>();
            }
            return utenlandskIdentifikasjonsnummer;
        }

        public List<AdressebeskyttelseDTO> getAdressebeskyttelse() {
            if (isNull(adressebeskyttelse)) {
                adressebeskyttelse = new ArrayList<>();
            }
            return adressebeskyttelse;
        }

        public List<FolkeregisterPersonstatus> getFolkeregisterpersonstatus() {
            if (isNull(folkeregisterpersonstatus)) {
                folkeregisterpersonstatus = new ArrayList<>();
            }
            return folkeregisterpersonstatus;
        }

        public List<TilrettelagtKommunikasjonDTO> getTilrettelagtKommunikasjon() {
            if (isNull(tilrettelagtKommunikasjon)) {
                tilrettelagtKommunikasjon = new ArrayList<>();
            }
            return tilrettelagtKommunikasjon;
        }

        public List<PdlPerson.Statsborgerskap> getStatsborgerskap() {
            if (isNull(statsborgerskap)) {
                statsborgerskap = new ArrayList<>();
            }
            return statsborgerskap;
        }

        public List<OppholdDTO> getOpphold() {
            if (isNull(opphold)) {
                opphold = new ArrayList<>();
            }
            return opphold;
        }

        public List<TelefonnummerDTO> getTelefonnummer() {
            if (isNull(telefonnummer)) {
                telefonnummer = new ArrayList<>();
            }
            return telefonnummer;
        }

        public List<FullmaktDTO> getFullmakt() {
            if (isNull(fullmakt)) {
                fullmakt = new ArrayList<>();
            }
            return fullmakt;
        }

        public List<Vergemaal> getVergemaalEllerFremtidsfullmakt() {
            if (isNull(vergemaalEllerFremtidsfullmakt)) {
                vergemaalEllerFremtidsfullmakt = new ArrayList<>();
            }
            return vergemaalEllerFremtidsfullmakt;
        }

        public List<SikkerhetstiltakDTO> getSikkerhetstiltak() {
            if (isNull(sikkerhetstiltak)) {
                sikkerhetstiltak = new ArrayList<>();
            }
            return sikkerhetstiltak;
        }

        public List<DoedfoedtBarnDTO> getDoedfoedtBarn() {
            if (isNull(doedfoedtBarn)) {
                doedfoedtBarn = new ArrayList<>();
            }
            return doedfoedtBarn;
        }

        public List<InnflyttingDTO> getInnflyttingTilNorge() {
            if (isNull(innflyttingTilNorge)) {
                innflyttingTilNorge = new ArrayList<>();
            }
            return innflyttingTilNorge;
        }

        public List<Foedselsdato> getFoedselsdato() {
            if (isNull(foedselsdato)) {
                foedselsdato = new ArrayList<>();
            }
            return foedselsdato;
        }

        public List<FoedestedDTO> getFoedested() {
            if (isNull(foedested)) {
                return foedested;
            }
            return foedested;
        }
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregisteridentifikator extends DbVersjonDTO {

        private String identifikasjonsnummer;
        private String type;
        private String status;

        public boolean isOpphoert() {
            return "OPPHOERT".equals(status);
        }
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Navn extends MetadataInformasjon {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlKjoenn extends DbVersjonDTO {

        private String kjoenn;
    }

    @lombok.Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foedsel extends DbVersjonDTO {

        private String foedekommune;
        private String foedeland;
        private String foedested;
        private Integer foedselsaar;
        private LocalDate foedselsdato;
    }

    @lombok.Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foedselsdato extends DbVersjonDTO {

        private Integer foedselsaar;
        private LocalDate foedselsdato;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForelderBarnRelasjon extends DbVersjonDTO {

        private String relatertPersonsIdent;
        private Rolle relatertPersonsRolle;
        private Rolle minRolleForPerson;
        private RelatertBiPersonDTO relatertPersonUtenFolkeregisteridentifikator;

        public boolean isForelder() {
            return MOR == getRelatertPersonsRolle() ||
                    FAR == getRelatertPersonsRolle() ||
                    MEDMOR == getRelatertPersonsRolle();
        }

        public boolean isBarn() {
            return BARN == getRelatertPersonsRolle();
        }
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sivilstand extends MetadataInformasjon {

        private SivilstandType type;
        private LocalDate gyldigFraOgMed;
        private LocalDate bekreftelsesdato;
        private String relatertVedSivilstand;

        public boolean isGift() {
            return type == SivilstandType.GIFT ||
                    type == SivilstandType.SEPARERT ||
                    type == SivilstandType.REGISTRERT_PARTNER ||
                    type == SivilstandType.SEPARERT_PARTNER;
        }

        public boolean isTidligereGift() {
            return type == SivilstandType.SKILT ||
                    type == SivilstandType.SKILT_PARTNER ||
                    type == SivilstandType.ENKE_ELLER_ENKEMANN ||
                    type == SivilstandType.GJENLEVENDE_PARTNER;
        }

        public boolean isUgift() {
            return type == SivilstandType.UGIFT ||
                    type == SivilstandType.UOPPGITT;
        }

        public boolean isGjenlevende() {
            return type == SivilstandType.ENKE_ELLER_ENKEMANN ||
                    type == SivilstandType.GJENLEVENDE_PARTNER;
        }
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Doedsfall extends DbVersjonDTO {

        private LocalDate doedsdato;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtflyttingFraNorge extends DbVersjonDTO {

        private String tilflyttingsland;
        private LocalDateTime utflyttingsdato;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statsborgerskap extends DbVersjonDTO {

        private String land;
        private LocalDateTime gyldigFraOgMed;
        private LocalDateTime gyldigTilOgMed;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vergemaal extends DbVersjonDTO {

        private String embete;
        private String type;
        private VergeEllerFullmektig vergeEllerFullmektig;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VergeEllerFullmektig extends DbVersjonDTO {

        private NavnDTO navn;
        private String motpartsPersonident;
        private Boolean omfangetErInnenPersonligOmraade;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostadresseIFrittFormat implements Serializable {

        private String adresselinje1;
        private String adresselinje2;
        private String adresselinje3;
        private String postnummer;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtenlandskAdresseIFrittFormat implements Serializable {

        private String adresselinje1;
        private String adresselinje2;
        private String adresselinje3;
        private String postkode;
        private String byEllerStedsnavn;
        private String landkode;
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolkeregisterPersonstatus extends DbVersjonDTO {

        private Personstatus status;

        @Getter
        @JsonDeserialize(using = PersonStatusEnumDeserializer.class)
        public enum Personstatus {
            BOSATT("bosatt"),
            UTFLYTTET("utflyttet"),
            FORSVUNNET("forsvunnet"),
            DOED("doed"),
            OPPHOERT("opphoert"),
            FOEDSELSREGISTRERT("foedselsregistrert"),
            IKKE_BOSATT("ikkeBosatt"),
            MIDLERTIDIG("midlertidig"),
            INAKTIV("inaktiv");

            private final String beskrivelse;

            Personstatus(String camelCaseValue) {
                this.beskrivelse = camelCaseValue;
            }
        }
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kontaktadresse extends AdresseDTO {

        private VegadresseDTO vegadresse;
        private UtenlandskAdresseDTO utenlandskAdresse;
        private KontaktadresseDTO.PostboksadresseDTO postboksadresse;
        private PostadresseIFrittFormat postadresseIFrittFormat;
        private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;

        @Override
        @JsonIgnore
        public boolean isAdresseNorge() {

            return nonNull(vegadresse) || nonNull(postboksadresse);
        }

        @Override
        @JsonIgnore
        public boolean isAdresseUtland() {

            return nonNull(utenlandskAdresse);
        }
    }

    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataInformasjon extends DbVersjonDTO implements Serializable {

        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata implements Serializable {

        private Boolean historisk;
    }
}
