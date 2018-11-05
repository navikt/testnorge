package no.nav.registre.hodejegeren.skdmelding;

import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("t1")
@Builder(toBuilder=true)
public class RsMeldingstype1Felter extends RsMeldingstype {
    
    @Size(max = 6)
    private String fodselsdato;
    @Size(max = 5)
    private String personnummer;
    @Size(max = 8)
    private String regDato;
    @Size(max = 1)
    private String statuskode;
    @Size(max = 8)
    private String datoDoed;
    @Size(max = 50)
    private String slektsnavn;
    @Size(max = 50)
    private String fornavn;
    @Size(max = 50)
    private String mellomnavn;
    @Size(max = 50)
    private String slektsnavnUgift;
    @Size(max = 25)
    private String forkortetNavn;
    @Size(max = 8)
    private String regDatoNavn;
    @Size(max = 4)
    private String foedekommLand;
    @Size(max = 20)
    private String foedested;
    @Size(max = 11)
    private String familienummer;
    @Size(max = 8)
    private String regdatoFamnr;
    @Size(max = 1)
    private String personkode;
    @Size(max = 1)
    private String spesRegType;
    @Size(max = 8)
    private String datoSpesRegType;
    @Size(max = 1)
    private String sivilstand;
    @Size(max = 8)
    private String regdatoSivilstand;
    @Size(max = 6)
    private String ektefellePartnerFdato;
    @Size(max = 5)
    private String ektefellePartnerPnr;
    @Size(max = 50)
    private String ektefellePartnerNavn;
    @Size(max = 3)
    private String ektefellePartnerStatsb;
    @Size(max = 8)
    private String regdatoAdr;
    @Size(max = 8)
    private String postadrRegDato;
    @Size(max = 8)
    private String flyttedatoAdr;
    @Size(max = 4)
    private String kommunenummer;
    @Size(max = 5)
    private String gateGaard;
    @Size(max = 4)
    private String husBruk;
    @Size(max = 4)
    private String bokstavFestenr;
    @Size(max = 3)
    private String undernr;
    @Size(max = 25)
    private String adressenavn;
    @Size(max = 1)
    private String adressetype;
    @Size(max = 25)
    private String tilleggsadresse;
    @Size(max = 4)
    private String postnummer;
    @Size(max = 4)
    private String valgkrets;
    @Size(max = 30)
    private String adresse1;
    @Size(max = 30)
    private String adresse2;
    @Size(max = 30)
    private String adresse3;
    @Size(max = 3)
    private String postadrLand;
    @Size(max = 3)
    private String innvandretFraLand;
    @Size(max = 8)
    private String fraLandRegdato;
    @Size(max = 8)
    private String fraLandFlyttedato;
    @Size(max = 4)
    private String fraKommune;
    @Size(max = 8)
    private String fraKommRegdato;
    @Size(max = 8)
    private String fraKommFlyttedato;
    @Size(max = 3)
    private String utvandretTilLand;
    @Size(max = 8)
    private String tilLandRegdato;
    @Size(max = 8)
    private String tilLandFlyttedato;
    @Size(max = 1)
    private String samemanntall;
    @Size(max = 8)
    private String datoSamemanntall;
    @Size(max = 1)
    private String umyndiggjort;
    @Size(max = 8)
    private String datoUmyndiggjort;
    @Size(max = 1)
    private String foreldreansvar;
    @Size(max = 8)
    private String datoForeldreansvar;
    @Size(max = 1)
    private String arbeidstillatelse;
    @Size(max = 8)
    private String datoArbeidstillatelse;
    @Size(max = 8)
    private String fremkonnummer;
    @Size(max = 6)
    private String morsFodselsdato;
    @Size(max = 5)
    private String morsPersonnummer;
    @Size(max = 50)
    private String morsNavn;
    @Size(max = 3)
    private String morsStatsbSkap;
    @Size(max = 6)
    private String farsFodselsdato;
    @Size(max = 5)
    private String farsPersonnummer;
    @Size(max = 50)
    private String farsNavn;
    @Size(max = 3)
    private String farsStatsbSkap;
    @Size(max = 11)
    private String tidligereFnrDnr;
    @Size(max = 8)
    private String datoTidlFnrDnr;
    @Size(max = 11)
    private String nyttFnr;
    @Size(max = 8)
    private String datoNyttFnr;
    @Size(max = 1)
    private String levendeDoed;
    @Size(max = 1)
    private String kjoenn;
    @Size(max = 1)
    private String tildelingskode;
    @Size(max = 2)
    private String foedselstype;
    @Size(max = 1)
    private String morsSiviltilstand;
    @Size(max = 1)
    private String ekteskPartnskNr;
    @Size(max = 1)
    private String ektfEkteskPartnskNr;
    @Size(max = 1)
    private String vigselstype;
    @Size(max = 2)
    private String forsByrde;
    @Size(max = 1)
    private String dombevilling;
    @Size(max = 2)
    private String antallBarn;
    @Size(max = 1)
    private String tidlSivilstand;
    @Size(max = 1)
    private String ektfTidlSivilstand;
    @Size(max = 1)
    private String hjemmel;
    @Size(max = 2)
    private String fylke;
    @Size(max = 4)
    private String vigselskomm;
    @Size(max = 1)
    private String tidlSepDomBev;
    @Size(max = 1)
    private String begjertAv;
    @Size(max = 1)
    private String registrGrunnlag;
    @Size(max = 4)
    private String doedssted;
    @Size(max = 1)
    private String typeDoedssted;
    @Size(max = 8)
    private String vigselsdato;
    @Size(max = 1)
    private String medlKirken;
    @Size(max = 5)
    private String bolignr;
    @Size(max = 12)
    private String dufId;
    @Size(max = 8)
    private String brukerident;
    @Size(max = 4)
    private String skolerets;
    @Size(max = 4)
    private String tkNr;
    @Size(max = 40)
    private String dnrHjemlandsadresse1;
    @Size(max = 40)
    private String dnrHjemlandsadresse2;
    @Size(max = 40)
    private String dnrHjemlandsadresse3;
    @Size(max = 3)
    private String dnrHjemlandLandkode;
    @Size(max = 8)
    private String dnrHjemlandRegDato;
    @Size(max = 1)
    private String dnrIdKontroll;
    @Size(max = 1)
    private String utvandringstype;
    @Size(max = 4)
    private String grunnkrets;
    @Size(max = 3)
    private String statsborgerskap;
    @Size(max = 8)
    private String regdatoStatsb;
    @Size(max = 3)
    private String statsborgerskap2;
    @Size(max = 8)
    private String regdatoStatsb2;
    @Size(max = 3)
    private String statsborgerskap3;
    @Size(max = 8)
    private String regdatoStatsb3;
    @Size(max = 3)
    private String statsborgerskap4;
    @Size(max = 8)
    private String regdatoStatsb4;
    @Size(max = 3)
    private String statsborgerskap5;
    @Size(max = 8)
    private String regdatoStatsb5;
    @Size(max = 3)
    private String statsborgerskap6;
    @Size(max = 8)
    private String regdatoStatsb6;
    @Size(max = 3)
    private String statsborgerskap7;
    @Size(max = 8)
    private String regdatoStatsb7;
    @Size(max = 3)
    private String statsborgerskap8;
    @Size(max = 8)
    private String regdatoStatsb8;
    @Size(max = 3)
    private String statsborgerskap9;
    @Size(max = 8)
    private String regdatoStatsb9;
    @Size(max = 3)
    private String statsborgerskap10;
    @Size(max = 8)
    private String regdatoStatsb10;
    @Size(max = 1)
    private String bibehold;
    @Size(max = 8)
    private String regdatoBibehold;
    @Size(max = 7)
    private String saksid;
    @Size(max = 4)
    private String embete;
    @Size(max = 3)
    private String sakstype;
    @Size(max = 8)
    private String vedtaksdato;
    @Size(max = 7)
    private String internVergeid;
    @Size(max = 11)
    private String vergeFnrDnr;
    @Size(max = 3)
    private String vergetype;
    @Size(max = 3)
    private String mandattype;
    @Size(max = 100)
    private String mandatTekst;
    @Size(max = 151)
    private String reserverFramtidigBruk;
    
    public String getSlekstnavnUgift() {
        return slektsnavnUgift;
    }
    
    public String getKjonn() {
        return kjoenn;
    }
    
    public String getFarsFarsNavn() {
        return farsNavn;
    }
    
    public void setmorsSivilstand(String morsSivilstand) {
        this.morsSiviltilstand = morsSivilstand;
    }
    
    public String getStatsborgerskapRegdato() {
        return regdatoStatsb;
    }
    
    @Override
    public String getMeldingstype() {
        return "t1";
    }
    
}