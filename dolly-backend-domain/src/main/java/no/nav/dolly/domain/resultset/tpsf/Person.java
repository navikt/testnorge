package no.nav.dolly.domain.resultset.tpsf;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private Long personId;
    private String ident;
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
    private List<RsPostadresse> postadresse;
    private List<RsAdresse> boadresse;
    private LocalDateTime utvandretTilLandFlyttedato;

    public List<Relasjon> getRelasjoner() {

        if (isNull(relasjoner)) {
            relasjoner = new ArrayList();
        }
        return relasjoner;
    }

    public List<Statsborgerskap> getStatsborgerskap() {

        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList();
        }
        return statsborgerskap;
    }

    public List<RsPostadresse> getPostadresse() {
        if (isNull(postadresse)) {
            postadresse = new ArrayList();
        }
        return postadresse;
    }

    public List<RsAdresse> getBoadresse() {
        if (isNull(boadresse)) {
            boadresse = new ArrayList();
        }
        return boadresse;
    }
}