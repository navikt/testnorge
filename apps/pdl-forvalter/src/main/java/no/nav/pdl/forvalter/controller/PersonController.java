package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.service.ArtifactDeleteService;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;
    private final ArtifactDeleteService artifactDeleteService;

    @ResponseBody
    @GetMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Hent person(er) med angitt(e) ident(er) eller alle")
    public List<FullPersonDTO> getPerson(@Parameter(description = "Ident(er) på personer som skal hentes")
                                         @RequestParam(required = false) List<String> identer,
                                         @Parameter(description = "Sidenummer ved sortering på 'sistOppdatert' og nyeste først")
                                         @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                         @Parameter(description = "Sidestørrelse ved sortering på 'sistOppdatert' og nyeste først")
                                         @RequestParam(required = false, defaultValue = "10") Integer sidestorrelse) {

        return personService.getPerson(identer, Paginering.builder()
                .sidenummer(sidenummer)
                .sidestoerrelse(sidestorrelse)
                .build());
    }

    @ResponseBody
    @PostMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Opprett person basert på angitte informasjonselementer, minimum er {}")
    public String createPerson(@RequestBody BestillingRequestDTO request) {

        return personService.createPerson(request);
    }

    @ResponseBody
    @PutMapping(value = "/{ident}", produces = "application/json; charset=utf-8")
    @Operation(description = "Oppdater testperson basert på angitte informasjonselementer")
    public String updatePerson(@Parameter(description = "Ident på testperson som skal oppdateres")
                               @PathVariable String ident,
                               @RequestBody PersonUpdateRequestDTO request) {

        return personService.updatePerson(ident, request);
    }

    @ResponseBody
    @PostMapping(value = "/{ident}/ordre", produces = "application/json; charset=utf-8")
    @Operation(description = "Send angitte testperson(er) med relasjoner til PDL")
    public OrdreResponseDTO sendPersonTilPdl(@Parameter(description = "Ident på hovedperson som skal sendes")
                                             @PathVariable String ident,
                                             @Parameter(description = "Angir om TPS er master, true == hovedperson skal ikke slettes i PDL")
                                             @RequestParam(required = false) Boolean isTpsMaster) {

        return pdlOrdreService.send(ident, isTpsMaster);
    }

    @ResponseBody
    @DeleteMapping(value = "/{ident}")
    @Operation(description = "Slett person")
    public void deletePerson(@Parameter(description = "Slett angitt testperson med relasjoner")
                             @PathVariable String ident) {

        personService.deletePerson(ident);
    }

    @DeleteMapping(value = "/{ident}/foedsel/{id}")
    @Operation(description = "Slett indikert foedsel for person")
    public void deleteFoedsel(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer foedsel")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteFoedsel(ident, id);
    }

    @DeleteMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Slett indikert navn for person")
    public void deletePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteNavn(ident, id);
    }

    @DeleteMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "Slett indikert kjoenn for person")
    public void deletePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id) {

        artifactDeleteService.deleteKjoenn(ident, id);
    }

    @DeleteMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Slett indikert bostedsadresse for person")
    public void deleteBostedsadresse(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer bostedsadresse")
                                   @PathVariable Integer id) {

        artifactDeleteService.deleteBostedsadresse(ident, id);
    }

    @DeleteMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Slett indikert kontaktadresse for person")
    public void deleteKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id) {

        artifactDeleteService.deleteKontaktadresse(ident, id);
    }

    @DeleteMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Slett indikert oppholdsadresse for person")
    public void deleteOppholdsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer oppholdsadresse")
                                     @PathVariable Integer id) {

        artifactDeleteService.deleteOppholdsadresse(ident, id);
    }

    @DeleteMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Slett indikert innflytting for person")
    public void deleteInnflytting(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer innflytting")
                                      @PathVariable Integer id) {

        artifactDeleteService.deleteInnflytting(ident, id);
    }

    @DeleteMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Slett indikert utflytting for person")
    public void deleteUtflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer utflytting")
                                  @PathVariable Integer id) {

        artifactDeleteService.deleteUtflytting(ident, id);
    }

    @DeleteMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Slett indikert deltbosted for person")
    public void deleteDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteDeltBosted(ident, id);
    }

    @DeleteMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Slett indikert forelderbarnrelasjon for person")
    public void deleteForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteForelderBarnRelasjon(ident, id);
    }

    @DeleteMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Slett indikert kontaktinformasjonfordoedsbo på person")
    public void deleteKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                           @PathVariable Integer id) {

        artifactDeleteService.deleteKontaktinformasjonForDoedsbo(ident, id);
    }
    
    @DeleteMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Slett indikert utenlandskidentifikasjonsnummer for person")
    public void deleteUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                   @PathVariable Integer id) {

        artifactDeleteService.deleteUtenlandskIdentifikasjonsnummer(ident, id);
    }

    @DeleteMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Slett indikert falskidentitet for person")
    public void deleteFalskIdentitet(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer falskidentitet")
                                                      @PathVariable Integer id) {

        artifactDeleteService.deleteFalskIdentitet(ident, id);
    }

    @DeleteMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Slett indikert adressebeskyttelse for person")
    public void deleteAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer adressebeskyttelse")
                                     @PathVariable Integer id) {

        artifactDeleteService.deleteAdressebeskyttelse(ident, id);
    }

    @DeleteMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Slett indikert doedsfall for person")
    public void deleteDoedsfall(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer doedsfall")
                                         @PathVariable Integer id) {

        artifactDeleteService.deleteDoedsfall(ident, id);
    }

    @DeleteMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Slett indikert folkeregisterpersonstatus for person")
    public void deleteFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                @PathVariable Integer id) {

        artifactDeleteService.deleteFolkeregisterPersonstatus(ident, id);
    }

    @DeleteMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Slett indikert tilrettelagtkommunikasjon for person")
    public void deleteTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id) {

        artifactDeleteService.deleteTilrettelagtKommunikasjon(ident, id);
    }
    
    @DeleteMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Slett indikert statsborgerskap for person")
    public void deleteStatsborgerskap(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer statsborgerskap")
                                                @PathVariable Integer id) {

        artifactDeleteService.deleteStatsborgerskap(ident, id);
    }

    @DeleteMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Slett indikert opphold for person")
    public void deleteOpphold(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer opphold")
                                      @PathVariable Integer id) {

        artifactDeleteService.deleteOpphold(ident, id);
    }

    @DeleteMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Slett indikert sivilstand for person")
    public void deleteSivilstand(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer sivilstand")
                              @PathVariable Integer id) {

        artifactDeleteService.deleteSivilstand(ident, id);
    }

    @DeleteMapping(value = "/{ident}/telefonnummer/{id}")
    @Operation(description = "Slett indikert telefonnummer for person")
    public void deleteTelefonnummer(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer telefonnummer")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteTelefonnummer(ident, id);
    }

    @DeleteMapping(value = "/{ident}/fullmakt/{id}")
    @Operation(description = "Slett indikert fullmakt for person")
    public void deleteFullmakt(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer fullmakt")
                                   @PathVariable Integer id) {

        artifactDeleteService.deleteFullmakt(ident, id);
    }

    @DeleteMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Slett indikert vergemaal for person")
    public void deleteVergemaal(@Parameter(description = "Ident for testperson")
                               @PathVariable String ident,
                               @Parameter(description = "id som identifiserer vergemaal")
                               @PathVariable Integer id) {

        artifactDeleteService.deleteVergemaal(ident, id);
    }

    @DeleteMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Slett indikert doedfoedtbarn for person")
    public void deleteDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedfoedtbarn")
                                @PathVariable Integer id) {

        artifactDeleteService.deleteDoedfoedtBarn(ident, id);
    }
}