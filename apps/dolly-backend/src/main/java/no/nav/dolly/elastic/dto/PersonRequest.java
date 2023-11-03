package no.nav.dolly.elastic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonRequest {

    @Schema($comment = "Status: " +
            "UOPPGITT,  UGIFT," +
            "GIFT,   ENKE_ELLER_ENKEMANN," +
            "SKILT, SEPARERT," +
            "REGISTRERT_PARTNER, SEPARERT_PARTNER," +
            "SKILT_PARTNER, GJENLEVENDE_PARTNER" +
            "SAMBOER")
    private SivilstandDTO.Sivilstand sivilstand;
    private AdressebeskyttelseDTO.AdresseBeskyttelse addressebeskyttelse;
    private Boolean harBarn;
    private Boolean harForeldre;
    private Boolean harDoedfoedtBarn;
    private Boolean harForeldreAnsvar;
    private Boolean harVerge;
    private Boolean harFullmakt;
    private Boolean harDoedsfall;
    private Boolean harInnflytting;
    private Boolean harUtflytting;
    private Boolean harDeltBosted;
    private Boolean harKontaktadresse;
    private Boolean harOppholdsadresse;
    @Schema($comment = "Kommunenummer")
    private String bostedKommune;
    private String bostedPostnummer;
    private String bostedBydelsnummer;
    private Boolean harKontaktinformasjonForDoedsbo;
    private Boolean harUtenlandskIdentifikasjonsnummer;
    private Boolean harFalskIdentitet;
    private Boolean harTilrettelagtKommunikasjon;
    private Boolean harSikkerhetstiltak;
    private Boolean harOpphold;
    @Schema($comment = "landkode")
    private String statsborgerskap;
}