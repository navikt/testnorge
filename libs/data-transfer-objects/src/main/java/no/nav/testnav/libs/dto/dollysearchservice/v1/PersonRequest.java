package no.nav.testnav.libs.dto.dollysearchservice.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonRequest {

    private String ident;

    private Identtype identtype;
    private KjoennDTO.Kjoenn kjoenn;
    private Integer alderFom;
    private Integer alderTom;
    private LocalDate foedselsdatoFom;
    private LocalDate foedselsdatoTom;
    private SivilstandDTO.Sivilstand sivilstand;

    @Schema(description = "erLevende eksluderer erDoed, begge kan ikke være satt")
    private Boolean erLevende;
    @Schema(description = "erDoed eksluderer erLevende, begge kan ikke være satt")
    private Boolean erDoed;
    private Boolean harBarn;
    private Boolean harForeldre;
    private Boolean harDoedfoedtBarn;
    private Boolean harForeldreAnsvar;
    private Boolean harVerge;
    private Boolean harInnflytting;
    private Boolean harUtflytting;
    private Boolean harKontaktinformasjonForDoedsbo;
    private Boolean harUtenlandskIdentifikasjonsnummer;
    private Boolean harFalskIdentitet;
    private Boolean harTilrettelagtKommunikasjon;
    private Boolean harSikkerhetstiltak;
    private Boolean harOpphold;
    @Schema(description = "landkode")
    private String statsborgerskap;
    private FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus personStatus;
    private Boolean harNyIdentitet;

    private AdresseRequest adresse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class AdresseRequest {

        private AdressebeskyttelseDTO.AdresseBeskyttelse addressebeskyttelse;

        private String kommunenummer;
        private String postnummer;
        private String bydelsnummer;

        private Boolean harBydelsnummer;
        private Boolean harUtenlandsadresse;
        private Boolean harMatrikkeladresse;
        private Boolean harUkjentAdresse;
        private Boolean harDeltBosted;

        private Boolean harBostedsadresse;
        private Boolean harKontaktadresse;
        private Boolean harOppholdsadresse;
    }
}