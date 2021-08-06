package no.nav.registre.syntrest.domain.tps;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkdMelding {

    @JsonProperty
    String meldingsnr;
    @JsonProperty
    String endringskode;
    @JsonProperty
    String datoInnlagt;
    @JsonProperty
    String maskindato;
    @JsonProperty
    String maskintid;
    @JsonProperty
    String transtype;
    @JsonProperty
    String aarsakskode;
    @JsonProperty
    String regDato;
    @JsonProperty
    String statuskode;
    @JsonProperty
    String datoDoed;
    @JsonProperty
    String slektsnavn;
    @JsonProperty
    String fornavn;
    @JsonProperty
    String mellomnavn;
    @JsonProperty
    String slektsnavnUgift;
    @JsonProperty
    String forkortetNavn;
    @JsonProperty
    String regDatoNavn;
    @JsonProperty
    String foedekommLand;
    @JsonProperty
    String foedested;
    @JsonProperty
    String statsborgerskap;
    @JsonProperty
    String regdatoStatsb;
    @JsonProperty
    String familienummer;
    @JsonProperty
    String regdatoFamnr;
    @JsonProperty
    String personkode;
    @JsonProperty
    String spesRegType;
    @JsonProperty
    String datoSpesRegType;
    @JsonProperty
    String sivilstand;
    @JsonProperty
    String regdatoSivilstand;
    @JsonProperty
    String ektefellePartnerNavn;
    @JsonProperty
    String ektefellePartnerStatsb;
    @JsonProperty
    String regdatoAdr;
    @JsonProperty
    String flyttedatoAdr;
    @JsonProperty
    String kommunenummer;
    @JsonProperty
    String gateGaard;
    @JsonProperty
    String husBruk;
    @JsonProperty
    String bokstavFestenr;
    @JsonProperty
    String undernr;
    @JsonProperty
    String adressenavn;
    @JsonProperty
    String adressetype;
    @JsonProperty
    String tilleggsadresse;
    @JsonProperty
    String postnummer;
    @JsonProperty
    String valgkrets;
    @JsonProperty
    String adresse1;
    @JsonProperty
    String adresse2;
    @JsonProperty
    String adresse3;
    @JsonProperty
    String postadrLand;
    @JsonProperty
    String innvandretFraLand;
    @JsonProperty
    String fraLandRegdato;
    @JsonProperty
    String fraLandFlyttedato;
    @JsonProperty
    String fraKommune;
    @JsonProperty
    String fraKommRegdato;
    @JsonProperty
    String fraKommFlyttedato;
    @JsonProperty
    String utvandretTilLand;
    @JsonProperty
    String tilLandRegdato;
    @JsonProperty
    String tilLandFlyttedato;
    @JsonProperty
    String samemanntall;
    @JsonProperty
    String datoSamemanntall;
    @JsonProperty
    String umyndiggjort;
    @JsonProperty
    String datoUmyndiggjort;
    @JsonProperty
    String foreldreansvar;
    @JsonProperty
    String datoForeldreansvar;
    @JsonProperty
    String arbeidstillatelse;
    @JsonProperty
    String datoArbeidstillatelse;
    @JsonProperty
    String fremkonnummer;
    @JsonProperty
    String morsNavn;
    @JsonProperty
    String morsStatsbSkap;
    @JsonProperty
    String farsNavn;
    @JsonProperty
    String farsStatsbSkap;
    @JsonProperty
    String tidligereFnrDnr;
    @JsonProperty
    String datoTidlFnrDnr;
    @JsonProperty
    String nyttFnr;
    @JsonProperty
    String datoNyttFnr;
    @JsonProperty
    String levendeDoed;
    @JsonProperty
    String kjoenn;
    @JsonProperty
    String tildelingskode;
    @JsonProperty
    String foedselstype;
    @JsonProperty
    String morsSivilstand;
    @JsonProperty
    String ekteskPartnskNr;
    @JsonProperty
    String ektfEkteskPartnskNr;
    @JsonProperty
    String vigselstype;
    @JsonProperty
    String forsByrde;
    @JsonProperty
    String dombevilling;
    @JsonProperty
    String antallBarn;
    @JsonProperty
    String tidlSivilstand;
    @JsonProperty
    String ektfTidlSivilstand;
    @JsonProperty
    String hjemmel;
    @JsonProperty
    String fylke;
    @JsonProperty
    String vigselskomm;
    @JsonProperty
    String tidlSepDomBev;
    @JsonProperty
    String begjertAv;
    @JsonProperty
    String registrGrunnlag;
    @JsonProperty
    String doedssted;
    @JsonProperty
    String typeDoedssted;
    @JsonProperty
    String vigselsdato;
    @JsonProperty
    String medlKirken;
    @JsonProperty
    String sekvensnr;
    @JsonProperty
    String bolignr;
    @JsonProperty
    String dufId;
    @JsonProperty
    String brukerident;
    @JsonProperty
    String skolerets;
    @JsonProperty
    String tkNr;
    @JsonProperty
    String dnrHjemlandsadresse1;
    @JsonProperty
    String dnrHjemlandsadresse2;
    @JsonProperty
    String dnrHjemlandsadresse3;
    @JsonProperty
    String dnrHjemlandLandkode;
    @JsonProperty
    String dnrHjemlandRegDato;
    @JsonProperty
    String dnrIdKontroll;
    @JsonProperty
    String postadrRegDato;
    @JsonProperty
    String utvandringstype;
    @JsonProperty
    String grunnkrets;
    @JsonProperty
    String statsborgerskap2;
    @JsonProperty
    String regdatoStatsb2;
    @JsonProperty
    String statsborgerskap3;
    @JsonProperty
    String regdatoStatsb3;
    @JsonProperty
    String statsborgerskap4;
    @JsonProperty
    String regdatoStatsb4;
    @JsonProperty
    String statsborgerskap5;
    @JsonProperty
    String regdatoStatsb5;
    @JsonProperty
    String statsborgerskap6;
    @JsonProperty
    String regdatoStatsb6;
    @JsonProperty
    String statsborgerskap7;
    @JsonProperty
    String regdatoStatsb7;
    @JsonProperty
    String statsborgerskap8;
    @JsonProperty
    String regdatoStatsb8;
    @JsonProperty
    String statsborgerskap9;
    @JsonProperty
    String regdatoStatsb9;
    @JsonProperty
    String statsborgerskap10;
    @JsonProperty
    String regdatoStatsb10;
    @JsonProperty
    String bibehold;
    @JsonProperty
    String regdatoBibehold;
    @JsonProperty
    String meldingstype;
    @JsonProperty
    String fodselsdato;
    @JsonProperty
    String personnummer;
    @JsonProperty
    String farsFodselsdato;
    @JsonProperty
    String farsPersonnummer;
    @JsonProperty
    String morsFodselsdato;
    @JsonProperty
    String morsPersonnummer;
    @JsonProperty
    String ektefellePartnerFdato;
    @JsonProperty
    String ektefellePartnerPnr;
}
