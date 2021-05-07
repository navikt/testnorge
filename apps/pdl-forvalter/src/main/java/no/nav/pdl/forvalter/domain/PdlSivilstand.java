package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlSivilstand extends PdlDbVersjon {

    public enum Sivilstand {
        UOPPGITT,
        UGIFT,
        GIFT,
        ENKE_ELLER_ENKEMANN,
        SKILT,
        SEPARERT,
        REGISTRERT_PARTNER,
        SEPARERT_PARTNER,
        SKILT_PARTNER,
        GJENLEVENDE_PARTNER
    }

    private LocalDateTime bekreftelsesdato;
    private String kommune;
    private String master;
    private String myndighet;
    private String relatertVedSivilstand;
    private LocalDateTime sivilstandsdato;
    private String sted;
    private Sivilstand type;
    private String utland;
}