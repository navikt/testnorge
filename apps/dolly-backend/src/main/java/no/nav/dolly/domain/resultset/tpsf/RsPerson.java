package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPerson {

    private Long personId;

    private String ident;

    private String identtype;

    private String kjonn;

    private String fornavn;

    private String mellomnavn;

    private String etternavn;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private LocalDateTime statsborgerskapTildato;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private String innvandretFraLand;

    private LocalDateTime innvandretFraLandFlyttedato;

    private LocalDateTime innvandretFraLandRegdato;

    private String utvandretTilLand;

    private LocalDateTime utvandretTilLandFlyttedato;

    private LocalDateTime utvandretTilLandRegdato;

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private LocalDateTime regdato;

    private RsSimpleRelasjoner relasjoner;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetsTiltak;

    private LocalDateTime sikkerhetsTiltakDatoFom;

    private LocalDateTime sikkerhetsTiltakDatoTom;

    private String beskrSikkerhetsTiltak;

    private LocalDateTime foedselsdato;

    private Integer alder;

    private LocalDateTime opprettetDato;

    private String opprettetAv;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private String tknr;

    private String gtType;

    private String gtVerdi;

    private String gtRegel;

    private Boolean utenFastBopel;

    private String personStatus;

    private String forsvunnetDato;
}
