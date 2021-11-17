package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.persistence.CascadeType.ALL;
import static no.nav.tps.forvalteren.domain.jpa.InnvandretUtvandret.InnUtvandret.INNVANDRET;
import static no.nav.tps.forvalteren.domain.jpa.InnvandretUtvandret.InnUtvandret.UTVANDRET;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.tps.forvalteren.domain.jpa.embedded.ChangeStamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    private String ident;

    private String identtype;

    private String kjonn;

    private String fornavn;

    private String mellomnavn;

    private String etternavn;

    private String forkortetNavn;

    private List<Statsborgerskap> statsborgerskap;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private List<Sivilstand> sivilstander;

    private LocalDateTime sivilstandRegdato;

    private List<InnvandretUtvandret> innvandretUtvandret;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetTiltak;

    private LocalDateTime sikkerhetTiltakDatoFom;

    private LocalDateTime sikkerhetTiltakDatoTom;

    private String beskrSikkerhetTiltak;

    private List<Adresse> boadresse;

    private List<Postadresse> postadresse;

    private LocalDateTime regdato;

    private List<Relasjon> relasjoner;

    private List<IdentHistorikk> identHistorikk;

    private List<MidlertidigAdresse> midlertidigAdresse;

    private List<Vergemaal> vergemaal;

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

    @Transient
    private String replacedByIdent;

    @Transient
    private LocalDateTime aliasRegdato;

    @Transient
    private boolean isNyPerson;

    public List<Postadresse> getPostadresse() {
        if (isNull(postadresse)) {
            postadresse = new ArrayList<>();
        }
        return postadresse;
    }

    public List<Relasjon> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    public List<IdentHistorikk> getIdentHistorikk() {
        if (isNull(identHistorikk)) {
            identHistorikk = new ArrayList<>();
        }
        return identHistorikk;
    }

    public List<Sivilstand> getSivilstander() {
        if (isNull(sivilstander)) {
            sivilstander = new ArrayList<>();
        }
        return sivilstander;
    }

    public List<Adresse> getBoadresse() {
        if (isNull(boadresse)) {
            boadresse = new ArrayList<>();
        }
        return boadresse;
    }

    public List<Statsborgerskap> getStatsborgerskap() {
        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList<>();
        }
        return statsborgerskap;
    }

    public List<InnvandretUtvandret> getInnvandretUtvandret() {
        if (isNull(innvandretUtvandret)) {
            innvandretUtvandret = new ArrayList<>();
        }
        return innvandretUtvandret;
    }

    public List<MidlertidigAdresse> getMidlertidigAdresse() {
        if (isNull(midlertidigAdresse)) {
            midlertidigAdresse = new ArrayList<>();
        }
        return midlertidigAdresse;
    }

    public List<Vergemaal> getVergemaal() {
        if (isNull(vergemaal)) {
            vergemaal = new ArrayList<>();
        }
        return vergemaal;
    }

    public List<Fullmakt> getFullmakt() {
        if (isNull(fullmakt)) {
            fullmakt = new ArrayList<>();
        }
        return fullmakt;
    }

    public String getLandkodeOfFirstInnvandret() {

        return getInnvandretUtvandret().stream()
                .filter(innutvandret -> INNVANDRET == innutvandret.getInnutvandret())
                .map(InnvandretUtvandret::getLandkode)
                .reduce((a, b) -> b).orElse(null);
    }

    public LocalDateTime getFlyttedatoOfFirstInnvandret() {

        return getInnvandretUtvandret().stream()
                .filter(innutvandret -> INNVANDRET == innutvandret.getInnutvandret())
                .map(InnvandretUtvandret::getFlyttedato)
                .reduce((a, b) -> b).orElse(null);
    }

    public PersonDTO toUppercase() {
        if (isNotBlank(getFornavn())) {
            setFornavn(getFornavn().toUpperCase());
        }
        if (isNotBlank(getMellomnavn())) {
            setMellomnavn(getMellomnavn().toUpperCase());
        }
        if (isNotBlank(getEtternavn())) {
            setEtternavn(getEtternavn().toUpperCase());
        }
        if (isNotBlank(getForkortetNavn())) {
            setForkortetNavn(getForkortetNavn().toUpperCase());
        }
        if (isNotBlank(getIdenttype())) {
            setIdenttype(getIdenttype().toUpperCase());
        }

        getBoadresse().forEach(Adresse::toUppercase);
        getPostadresse().forEach(Postadresse::toUppercase);

        return this;
    }

    @JsonIgnore
    public boolean isUtenFastBopel() {
        return isTrue(utenFastBopel) || "UFB".equals(getSpesreg());
    }

    @JsonIgnore
    public boolean isKode6() {
        return "SPSF".equals(getSpesreg());
    }

    @JsonIgnore
    public boolean isForsvunnet() {
        return nonNull(getForsvunnetDato());
    }

    @JsonIgnore
    public boolean isEgenansatt() {
        return nonNull(getEgenAnsattDatoFom()) && isNull(getEgenAnsattDatoTom());
    }

    @JsonIgnore
    public boolean isUtvandret() {
        return !getInnvandretUtvandret().isEmpty() &&
                UTVANDRET == getInnvandretUtvandret().get(0).getInnutvandret();
    }

    @JsonIgnore
    public boolean isDoedFoedt() {
        return "FDAT".equals(getIdenttype());
    }

    @JsonIgnore
    public boolean isKvinne() {
        return "K".equals(getKjonn());
    }
}