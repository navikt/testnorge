package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsAdresse;
import no.nav.dolly.domain.resultset.RsPostadresse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfBasisBestilling {

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private List<Sivilstatus> sivilstatus;

    private LocalDateTime regdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetsTiltak;

    private LocalDateTime sikkerhetsTiltakDatoFom;

    private LocalDateTime sikkerhetsTiltakDatoTom;

    private String beskrSikkerhetsTiltak;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private Boolean utenFastBopel;

    private String utvandretTilLand;

    private LocalDateTime utvandretTilLandFlyttedato;

    private Boolean harMellomnavn;
}
