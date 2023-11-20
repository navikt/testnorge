package no.nav.dolly.elastic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonRequest {

    private Identtype identtype;
    private KjoennDTO.Kjoenn kjoenn;
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
    private Boolean harKontaktinformasjonForDoedsbo;
    private Boolean harUtenlandskIdentifikasjonsnummer;
    private Boolean harFalskIdentitet;
    private Boolean harTilrettelagtKommunikasjon;
    private Boolean harSikkerhetstiltak;
    private Boolean harOpphold;
    @Schema($comment = "landkode")
    private String statsborgerskap;
    private Boolean nyIdentitet;

    private BostedAdresseRequest bostedsadresse;
    private Boolean harDeltBosted;

    private Boolean harKontaktadresse;
    private Boolean harOppholdsadresse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class BostedAdresseRequest {

        private String kommunenummer;
        private String postnummer;
        private String bydelsnummer;
        private Boolean harUtenlandsadresse;
        private Boolean harMatrikkelAdresse;
        private Boolean harUkjentAdresse;
    }
}