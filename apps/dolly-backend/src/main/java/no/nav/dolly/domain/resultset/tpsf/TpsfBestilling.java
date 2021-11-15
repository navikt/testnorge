package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.tpsf.adresse.AdresseNrInfo;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMidlertidigAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsfBestilling {

    private List<String> opprettFraIdenter;

    private List<String> environments;

    private int antall;

    private RsSimpleRelasjoner relasjoner;

    private IdentType identtype;

    private Boolean navSyntetiskIdent;

    private Integer alder;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private RsMidlertidigAdresse midlertidigAdresse;

    private String kjonn;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private LocalDateTime statsborgerskapTildato;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private LocalDateTime sivilstandRegdato;

    private LocalDateTime regdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetTiltak;

    private LocalDateTime sikkerhetTiltakDatoFom;

    private LocalDateTime sikkerhetTiltakDatoTom;

    private String beskrSikkerhetTiltak;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private Boolean utenFastBopel;

    private String utvandretTilLand;

    private LocalDateTime utvandretTilLandFlyttedato;

    private Boolean harMellomnavn;

    private Boolean harNyttNavn;

    private String innvandretFraLand;

    private LocalDateTime innvandretFraLandFlyttedato;

    private AdresseNrInfo adresseNrInfo;

    private Boolean erForsvunnet;

    private LocalDateTime forsvunnetDato;

    private List<RsIdenthistorikk> identHistorikk;

    private Boolean harBankkontonr;

    private LocalDateTime bankkontonrRegdato;

    private String telefonLandskode_1;

    private String telefonnummer_1;

    private String telefonLandskode_2;

    private String telefonnummer_2;

    private RsVergemaalRequest vergemaal;

    private RsFullmaktRequest fullmakt;
}
