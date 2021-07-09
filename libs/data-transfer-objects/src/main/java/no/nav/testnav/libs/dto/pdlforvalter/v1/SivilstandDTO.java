package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SivilstandDTO extends DbVersjonDTO {

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