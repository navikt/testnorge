package no.nav.dolly.domain.resultset.tpsf;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.INNVANDRET;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.UTVANDRET;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private static final Integer MYNDIGHET_ALDER = 18;

    private Long personId;
    private String ident;
    private List<IdentHistorikk> identHistorikk;
    private Integer alder;
    private String identtype;
    private String kjonn;
    private String fornavn;
    private String etternavn;
    private String mellomnavn;
    private String forkortetNavn;
    private LocalDateTime regdato;
    private LocalDateTime foedselsdato;
    private List<Relasjon> relasjoner;
    private String spesreg;
    private LocalDateTime spesregDato;
    private LocalDateTime doedsdato;
    private List<Statsborgerskap> statsborgerskap;
    private Sivilstand.Sivilstatus sivilstand;
    private LocalDateTime sivilstandRegdato;
    private List<Sivilstand> sivilstander;
    private List<RsPostadresse> postadresse;
    private List<BoAdresse> boadresse;
    private LocalDateTime utvandretTilLandFlyttedato;
    private String telefonLandskode_1;
    private String telefonnummer_1;
    private String telefonLandskode_2;
    private String telefonnummer_2;
    private String tknavn;
    private String tknr;
    private Boolean utenFastBopel;
    private String personStatus;
    private List<InnvandretUtvandret> innvandretUtvandret;
    private List<MidlertidigAdresse> midlertidigAdresse;
    private LocalDateTime egenAnsattDatoFom;
    private LocalDateTime egenAnsattDatoTom;

    public List<MidlertidigAdresse> getMidlertidigAdresse() {
        if (isNull(midlertidigAdresse)) {
            midlertidigAdresse = new ArrayList<>();
        }
        return midlertidigAdresse;
    }

    public List<IdentHistorikk> getIdentHistorikk() {
        if (isNull(identHistorikk)) {
            identHistorikk = new ArrayList<>();
        }
        return identHistorikk;
    }

    public List<InnvandretUtvandret> getInnvandretUtvandret() {
        if (isNull(innvandretUtvandret)) {
            innvandretUtvandret = new ArrayList<>();
        }
        return innvandretUtvandret;
    }

    public List<Relasjon> getRelasjoner() {

        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    public List<Sivilstand> getSivilstander() {

        if (isNull(sivilstander)) {
            sivilstander = new ArrayList<>();
        }
        return sivilstander;
    }

    public List<Statsborgerskap> getStatsborgerskap() {

        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList<>();
        }
        return statsborgerskap;
    }

    public List<RsPostadresse> getPostadresse() {
        if (isNull(postadresse)) {
            postadresse = new ArrayList<>();
        }
        return postadresse;
    }

    public List<BoAdresse> getBoadresse() {
        if (isNull(boadresse)) {
            boadresse = new ArrayList<>();
        }
        return boadresse;
    }

    public boolean isSivilstandGift() {

        if (isNull(getSivilstand())) {
            return false;

        } else {
            switch (getSivilstand()) {
            case GIFT:
            case REPA:
            case SEPR:
            case SEPA:
                return true;
            default:
                return false;
            }
        }
    }

    public boolean isMyndig() {
        return getAlder() >= MYNDIGHET_ALDER;
    }

    public boolean hasOppholdsadresse() {
        return !getBoadresse().isEmpty() && !isUtenFastBopel() || hasUtenlandskAdresse();
    }

    public boolean hasUtenlandskAdresse() {
        return !getPostadresse().isEmpty() && getPostadresse().get(0).isUtenlandsk() ||
                (!getMidlertidigAdresse().isEmpty() && midlertidigAdresse.get(0).isUtenlandsk());
    }

    public boolean hasNorskKontaktadresse() {
        return !getBoadresse().isEmpty() && !isUtenFastBopel() && getBoadresse().get(0).isGateadresse() ||
                (!getPostadresse().isEmpty() && getPostadresse().get(0).isNorsk()) ||
                (!getMidlertidigAdresse().isEmpty() && midlertidigAdresse.get(0).isNorsk() &&
                        !midlertidigAdresse.get(0).isStedadresse());
    }

    public boolean isUtenFastBopel() {
        return isTrue(utenFastBopel) || "UFB".equals(getSpesreg());
    }

    public boolean isDoedFoedt() {
        return "FDAT".equals(getIdenttype());
    }

    public boolean isInnvandret() {
        return !getInnvandretUtvandret().isEmpty() && INNVANDRET == getInnvandretUtvandret().get(0).getInnutvandret();
    }

    public boolean isUtvandret() {
        return !getInnvandretUtvandret().isEmpty() && UTVANDRET == getInnvandretUtvandret().get(0).getInnutvandret();
    }
}