package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonDTO {

    private String ident;

    private String identtype;

    private String kjonn;

    private String fornavn;

    private String mellomnavn;

    private String etternavn;

    private String forkortetNavn;

    private List<StatsborgerskapDTO> statsborgerskap;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private List<SivilstandDTO> sivilstander;

    private LocalDateTime sivilstandRegdato;

    private List<InnvandretUtvandretDTO> innvandretUtvandret;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetTiltak;

    private LocalDateTime sikkerhetTiltakDatoFom;

    private LocalDateTime sikkerhetTiltakDatoTom;

    private String beskrSikkerhetTiltak;

    private List<AdresseDTO> boadresse;

    private List<PostadresseDTO> postadresse;

    private LocalDateTime regdato;

    private List<RelasjonDTO> relasjoner;

    private List<MidlertidigAdresseDTO> midlertidigAdresse;

    private LocalDateTime opprettetDato;

    private String opprettetAv;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private String tknr;

    private String tknavn;

    private String gtType;

    private String gtVerdi;

    private String gtRegel;

    private Boolean utenFastBopel;

    private String personStatus;

    private LocalDateTime forsvunnetDato;

    private String bankkontonr;

    private LocalDateTime bankkontonrRegdato;

    private String telefonLandskode_1;

    private String telefonnummer_1;

    private String telefonLandskode_2;

    private String telefonnummer_2;

    private String importFra;

    public List<PostadresseDTO> getPostadresse() {
        if (isNull(postadresse)) {
            postadresse = new ArrayList<>();
        }
        return postadresse;
    }

    public List<RelasjonDTO> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    public List<SivilstandDTO> getSivilstander() {
        if (isNull(sivilstander)) {
            sivilstander = new ArrayList<>();
        }
        return sivilstander;
    }

    public List<AdresseDTO> getBoadresse() {
        if (isNull(boadresse)) {
            boadresse = new ArrayList<>();
        }
        return boadresse;
    }

    public List<StatsborgerskapDTO> getStatsborgerskap() {
        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList<>();
        }
        return statsborgerskap;
    }

    public List<InnvandretUtvandretDTO> getInnvandretUtvandret() {
        if (isNull(innvandretUtvandret)) {
            innvandretUtvandret = new ArrayList<>();
        }
        return innvandretUtvandret;
    }

    public List<MidlertidigAdresseDTO> getMidlertidigAdresse() {
        if (isNull(midlertidigAdresse)) {
            midlertidigAdresse = new ArrayList<>();
        }
        return midlertidigAdresse;
    }
}