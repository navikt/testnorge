package no.nav.dolly.testdata.builder;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsAdresse;
import no.nav.dolly.domain.resultset.RsPostadresse;
import no.nav.dolly.domain.resultset.tpsf.RsSimpleRelasjoner;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;

@Builder
@Getter
@Setter
public class RsTpsfBestillingBuilder {

    private List<String> environments;
    private int antall;
    private RsSimpleRelasjoner relasjoner;
    private String identtype;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private boolean withAdresse;
    private RsAdresse boadresse;
    private List<RsPostadresse> postadresse;
    private String kjonn;
    private String statsborgerskap;
    private String spesreg;
    private LocalDateTime spesregDato;
    private LocalDateTime doedsdato;
    private LocalDateTime regdato;
    private LocalDateTime egenAnsattDatoFom;
    private LocalDateTime egenAnsattDatoTom;
    private String typeSikkerhetsTiltak;
    private LocalDateTime sikkerhetsTiltakDatoFom;
    private LocalDateTime sikkerhetsTiltakDatoTom;
    private String beskrSikkerhetsTiltak;

    public RsTpsfBestilling convertToRealRsTpsfBestilling(){
        RsTpsfBestilling bestilling = new RsTpsfBestilling();
        bestilling.setEnvironments(this.environments);
        bestilling.setAntall(this.antall);
        bestilling.setRelasjoner(this.relasjoner);
        bestilling.setIdenttype(this.identtype);
        bestilling.setFoedtEtter(this.foedtEtter);
        bestilling.setFoedtFoer(this.foedtFoer);
        bestilling.setWithAdresse(this.withAdresse);
        bestilling.setBoadresse(this.boadresse);
        bestilling.setPostadresse(this.postadresse);
        bestilling.setKjonn(this.kjonn);
        bestilling.setStatsborgerskap(this.statsborgerskap);
        bestilling.setSpesreg(this.spesreg);
        bestilling.setSpesregDato(this.spesregDato);
        bestilling.setDoedsdato(this.doedsdato);
        bestilling.setRegdato(this.regdato);
        bestilling.setEgenAnsattDatoFom(this.egenAnsattDatoFom);
        bestilling.setEgenAnsattDatoTom(this.egenAnsattDatoTom);
        bestilling.setTypeSikkerhetsTiltak(this.typeSikkerhetsTiltak);
        bestilling.setSikkerhetsTiltakDatoFom(this.sikkerhetsTiltakDatoFom);
        bestilling.setSikkerhetsTiltakDatoTom(this.sikkerhetsTiltakDatoTom);
        bestilling.setBeskrSikkerhetsTiltak(this.beskrSikkerhetsTiltak);

        return bestilling;
    }
}
