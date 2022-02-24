package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.service.ArtifactDeleteService;
import no.nav.pdl.forvalter.service.ArtifactUpdateService;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/personer", produces = "application/json; charset=utf-8")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;
    private final ArtifactDeleteService artifactDeleteService;
    private final ArtifactUpdateService artifactUpdateService;

    @ResponseBody
    @GetMapping
    @Operation(description = "Hent person(er) med angitt(e) ident(er) eller alle")
    public List<FullPersonDTO> getPerson(@Parameter(description = "Ident(er) på personer som skal hentes")
                                         @RequestParam(required = false) List<String> identer,
                                         @Parameter(description = "Sidenummer ved sortering på 'sistOppdatert' og nyeste først")
                                         @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                         @Parameter(description = "Sidestørrelse ved sortering på 'sistOppdatert' og nyeste først, default 10")
                                         @RequestParam(required = false) Integer sidestorrelse) {

        if (isNull(sidestorrelse)) {
            if (nonNull(identer)) {
                sidestorrelse = 200;
            } else {
                sidestorrelse = 10;
            }
        }

        return personService.getPerson(identer, Paginering.builder()
                .sidenummer(sidenummer)
                .sidestoerrelse(sidestorrelse)
                .build());
    }

    @ResponseBody
    @PostMapping
    @Operation(description = "Opprett person basert på angitte informasjonselementer, minimum er {}")
    public String createPerson(@RequestBody BestillingRequestDTO request) {

        return personService.createPerson(request);
    }

    @ResponseBody
    @PutMapping(value = "/{ident}")
    @Operation(description = "Oppdater testperson basert på angitte informasjonselementer")
    public String updatePerson(@Parameter(description = "Ident på testperson som skal oppdateres")
                               @PathVariable String ident,
                               @RequestBody PersonUpdateRequestDTO request,
                               @Parameter(description = "Angir om tidligere historie skal skrives over")
                               @RequestHeader(required = false) Boolean overwrite,
                               @Parameter(description = "Angir om validering skal sløyfes for navn, adresse m.fl.")
                               @RequestHeader(required = false) Boolean relaxed) {

        return personService.updatePerson(ident, request, overwrite, relaxed);
    }

    @ResponseBody
    @PostMapping(value = "/{ident}/ordre")
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
    @Operation(description = "Slett angitt foedsel for person")
    public void deleteFoedsel(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer foedsel")
                              @PathVariable Integer id) {

        artifactDeleteService.deleteFoedsel(ident, id);
    }

    @PutMapping(value = "/{ident}/foedsel/{id}")
    @Operation(description = "Oppdatere angitt foedsel for person")
    public void updateFoedsel(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer foedsel")
                              @PathVariable Integer id,
                              @RequestBody FoedselDTO foedsel) {

        artifactUpdateService.updateFoedsel(ident, id, foedsel);
    }

    @DeleteMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Slett angitt navn for person")
    public void deletePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteNavn(ident, id);
    }

    @PutMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Oppdatere angitt navn for person")
    public void updatePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id,
                                 @RequestBody NavnDTO navn) {

        artifactUpdateService.updateNavn(ident, id, navn);
    }

    @DeleteMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "Slett angitt kjoenn for person")
    public void deletePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id) {

        artifactDeleteService.deleteKjoenn(ident, id);
    }

    @PutMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "oppdatere angitt kjoenn for person")
    public void updatePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id,
                                   @RequestBody KjoennDTO kjoenn) {

        artifactUpdateService.updateKjoenn(ident, id, kjoenn);
    }

    @DeleteMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Slett angitt bostedsadresse for person")
    public void deleteBostedsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer bostedsadresse")
                                     @PathVariable Integer id) {

        artifactDeleteService.deleteBostedsadresse(ident, id);
    }

    @PutMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Oppdater angitt bostedsadresse for person")
    public void updateBostedsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer bostedsadresse")
                                     @PathVariable Integer id,
                                     @RequestBody BostedadresseDTO bostedadresse) {

        artifactUpdateService.updateBostedsadresse(ident, id, bostedadresse);
    }

    @DeleteMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Slett angitt kontaktadresse for person")
    public void deleteKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id) {

        artifactDeleteService.deleteKontaktadresse(ident, id);
    }

    @PutMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Oppdater angitt kontaktadresse for person")
    public void updateKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id,
                                     @RequestBody KontaktadresseDTO kontaktadresse) {

        artifactUpdateService.updateKontaktadresse(ident, id, kontaktadresse);
    }

    @DeleteMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Slett angitt oppholdsadresse for person")
    public void deleteOppholdsadresse(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer oppholdsadresse")
                                      @PathVariable Integer id) {

        artifactDeleteService.deleteOppholdsadresse(ident, id);
    }

    @PutMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Oppdater angitt oppholdsadresse for person")
    public void updateOppholdsadresse(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer oppholdsadresse")
                                      @PathVariable Integer id,
                                      @RequestBody OppholdsadresseDTO oppholdsadresse) {

        artifactUpdateService.updateOppholdsadresse(ident, id, oppholdsadresse);
    }

    @DeleteMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Slett angitt innflytting for person")
    public void deleteInnflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer innflytting")
                                  @PathVariable Integer id) {

        artifactDeleteService.deleteInnflytting(ident, id);
    }

    @PutMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Oppdater angitt innflytting for person")
    public void updateInnflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer innflytting")
                                  @PathVariable Integer id,
                                  @RequestBody InnflyttingDTO innflytting) {

        artifactUpdateService.updateInnflytting(ident, id, innflytting);
    }

    @DeleteMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Slett angitt utflytting for person")
    public void deleteUtflytting(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer utflytting")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteUtflytting(ident, id);
    }

    @PutMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Oppdater angitt utflytting for person")
    public void updateUtflytting(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer utflytting")
                                 @PathVariable Integer id,
                                 @RequestBody UtflyttingDTO utflytting) {

        artifactUpdateService.updateUtflytting(ident, id, utflytting);
    }

    @DeleteMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Slett angitt deltbosted for person")
    public void deleteDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteDeltBosted(ident, id);
    }

    @PutMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Oppdater angitt deltbosted for person")
    public void updateDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id,
                                 @RequestBody DeltBostedDTO deltBosted) {

        artifactUpdateService.updateDeltBosted(ident, id, deltBosted);
    }

    @DeleteMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Slett angitt forelderbarnrelasjon for person")
    public void deleteForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                           @PathVariable Integer id) {

        artifactDeleteService.deleteForelderBarnRelasjon(ident, id);
    }

    @PutMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Oppdater angitt forelderbarnrelasjon for person")
    public void updateForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                           @PathVariable Integer id,
                                           @RequestBody ForelderBarnRelasjonDTO forelderBarnRelasjon) {

        artifactUpdateService.updateForelderBarnRelasjon(ident, id, forelderBarnRelasjon);
    }

    @DeleteMapping(value = "/{ident}/foreldreansvar/{id}")
    @Operation(description = "Slett angitt foreldreansvar for person (barn)")
    public void deleteForeldreansvar(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer foreldreansvar")
                                           @PathVariable Integer id) {

        artifactDeleteService.deleteForeldreansvar(ident, id);
    }

    @PutMapping(value = "/{ident}/foreldreansvar/{id}")
    @Operation(description = "Oppdater angitt foreldreansvar for person (barn)")
    public void updateForeldreansvar(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer foreldreansvar")
                                           @PathVariable Integer id,
                                           @RequestBody ForeldreansvarDTO foreldreansvar) {

        artifactUpdateService.updateForeldreansvar(ident, id, foreldreansvar);
    }

    @DeleteMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Slett angitt kontaktinformasjonfordoedsbo på person")
    public void deleteKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                                   @PathVariable Integer id) {

        artifactDeleteService.deleteKontaktinformasjonForDoedsbo(ident, id);
    }

    @PutMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Oppdater angitt kontaktinformasjonfordoedsbo på person")
    public void updateKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                                   @PathVariable Integer id,
                                                   @RequestBody KontaktinformasjonForDoedsboDTO kontaktinformasjonForDoedsbo) {

        artifactUpdateService.updateKontaktinformasjonForDoedsbo(ident, id, kontaktinformasjonForDoedsbo);
    }

    @DeleteMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Slett angitt utenlandskidentifikasjonsnummer for person")
    public void deleteUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                      @PathVariable Integer id) {

        artifactDeleteService.deleteUtenlandskIdentifikasjonsnummer(ident, id);
    }

    @PutMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Oppdater angitt utenlandskidentifikasjonsnummer for person")
    public void updateUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                      @PathVariable Integer id,
                                                      @RequestBody UtenlandskIdentifikasjonsnummerDTO utenlandskIdentifikasjonsnummer) {

        artifactUpdateService.updateUtenlandskIdentifikasjonsnummer(ident, id, utenlandskIdentifikasjonsnummer);
    }

    @DeleteMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Slett angitt falskidentitet for person")
    public void deleteFalskIdentitet(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer falskidentitet")
                                     @PathVariable Integer id) {

        artifactDeleteService.deleteFalskIdentitet(ident, id);
    }

    @PutMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Oppdater angitt falskidentitet for person")
    public void updateFalskIdentitet(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer falskidentitet")
                                     @PathVariable Integer id,
                                     @RequestBody FalskIdentitetDTO falskIdentitet) {

        artifactUpdateService.updateFalskIdentitet(ident, id, falskIdentitet);
    }

    @DeleteMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Slett angitt adressebeskyttelse for person")
    public void deleteAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer adressebeskyttelse")
                                         @PathVariable Integer id) {

        artifactDeleteService.deleteAdressebeskyttelse(ident, id);
    }

    @PutMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Oppdater angitt adressebeskyttelse for person")
    public void updateAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer adressebeskyttelse")
                                         @PathVariable Integer id,
                                         @RequestBody AdressebeskyttelseDTO adressebeskyttelse) {

        artifactUpdateService.updateAdressebeskyttelse(ident, id, adressebeskyttelse);
    }

    @DeleteMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Slett angitt doedsfall for person")
    public void deleteDoedsfall(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedsfall")
                                @PathVariable Integer id) {

        artifactDeleteService.deleteDoedsfall(ident, id);
    }

    @PutMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Oppdater angitt doedsfall for person")
    public void updateDoedsfall(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedsfall")
                                @PathVariable Integer id,
                                @RequestBody DoedsfallDTO doedsfall) {

        artifactUpdateService.updateDoedsfall(ident, id, doedsfall);
    }

    @DeleteMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Slett angitt folkeregisterpersonstatus for person")
    public void deleteFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                                @PathVariable Integer id) {

        artifactDeleteService.deleteFolkeregisterPersonstatus(ident, id);
    }

    @PutMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Oppdater angitt folkeregisterpersonstatus for person")
    public void updateFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                                @PathVariable Integer id,
                                                @RequestBody FolkeregisterPersonstatusDTO folkeregisterPersonstatus) {

        artifactUpdateService.updateFolkeregisterPersonstatus(ident, id, folkeregisterPersonstatus);
    }

    @DeleteMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Slett angitt tilrettelagtkommunikasjon for person")
    public void deleteTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id) {

        artifactDeleteService.deleteTilrettelagtKommunikasjon(ident, id);
    }

    @PutMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Oppdater angitt tilrettelagtkommunikasjon for person")
    public void updateTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id,
                                                @RequestBody TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

        artifactUpdateService.updateTilrettelagtKommunikasjon(ident, id, tilrettelagtKommunikasjon);
    }

    @DeleteMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Slett angitt statsborgerskap for person")
    public void deleteStatsborgerskap(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer statsborgerskap")
                                      @PathVariable Integer id) {

        artifactDeleteService.deleteStatsborgerskap(ident, id);
    }

    @PutMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Oppdater angitt statsborgerskap for person")
    public void updateStatsborgerskap(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer statsborgerskap")
                                      @PathVariable Integer id,
                                      @RequestBody StatsborgerskapDTO statsborgerskap) {

        artifactUpdateService.updateStatsborgerskap(ident, id, statsborgerskap);
    }

    @DeleteMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Slett angitt opphold for person")
    public void deleteOpphold(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer opphold")
                              @PathVariable Integer id) {

        artifactDeleteService.deleteOpphold(ident, id);
    }

    @PutMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Oppdater angitt opphold for person")
    public void deleteOpphold(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer opphold")
                              @PathVariable Integer id,
                              @RequestBody OppholdDTO opphold) {

        artifactUpdateService.updateOpphold(ident, id, opphold);
    }

    @DeleteMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Slett angitt sivilstand for person")
    public void deleteSivilstand(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer sivilstand")
                                 @PathVariable Integer id) {

        artifactDeleteService.deleteSivilstand(ident, id);
    }

    @PutMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Oppdater angitt sivilstand for person")
    public void updateSivilstand(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer sivilstand")
                                 @PathVariable Integer id,
                                 @RequestBody SivilstandDTO sivilstand) {

        artifactUpdateService.updateSivilstand(ident, id, sivilstand);
    }

    @DeleteMapping(value = "/{ident}/telefonnummer/{id}")
    @Operation(description = "Slett angitt telefonnummer for person")
    public void deleteTelefonnummer(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer telefonnummer")
                                    @PathVariable Integer id) {

        artifactDeleteService.deleteTelefonnummer(ident, id);
    }

    @PutMapping(value = "/{ident}/telefonnummer/{id}")
    @Operation(description = "Oppdater angitt telefonnummer for person")
    public void updateTelefonnummer(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer telefonnummer")
                                    @PathVariable Integer id,
                                    @RequestBody TelefonnummerDTO telefonnummer) {

        artifactUpdateService.updateTelefonnummer(ident, id, telefonnummer);
    }

    @DeleteMapping(value = "/{ident}/fullmakt/{id}")
    @Operation(description = "Slett angitt fullmakt for person")
    public void deleteFullmakt(@Parameter(description = "Ident for testperson")
                               @PathVariable String ident,
                               @Parameter(description = "id som identifiserer fullmakt")
                               @PathVariable Integer id) {

        artifactDeleteService.deleteFullmakt(ident, id);
    }

    @PutMapping(value = "/{ident}/fullmakt/{id}")
    @Operation(description = "Oppdater angitt fullmakt for person")
    public void updateFullmakt(@Parameter(description = "Ident for testperson")
                               @PathVariable String ident,
                               @Parameter(description = "id som identifiserer fullmakt")
                               @PathVariable Integer id,
                               @RequestBody FullmaktDTO fullmakt) {

        artifactUpdateService.updateFullmakt(ident, id, fullmakt);
    }

    @DeleteMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Slett angitt vergemaal for person")
    public void deleteVergemaal(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer vergemaal")
                                @PathVariable Integer id) {

        artifactDeleteService.deleteVergemaal(ident, id);
    }

    @PutMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Oppdater angitt vergemaal for person")
    public void updateVergemaal(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer vergemaal")
                                @PathVariable Integer id,
                                @RequestBody VergemaalDTO vergemaal) {

        artifactUpdateService.updateVergemaal(ident, id, vergemaal);
    }

    @DeleteMapping(value = "/{ident}/sikkerhetstiltak/{id}")
    @Operation(description = "Slett angitt sikkerhetstiltak for person")
    public void deleteSikkerhetstiltak(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @Parameter(description = "id som identifiserer sikkerhetstiltak")
                                       @PathVariable Integer id) {

        artifactDeleteService.deleteSikkerhetstiltak(ident, id);
    }

    @PutMapping(value = "/{ident}/sikkerhetstiltak/{id}")
    @Operation(description = "Oppdater angitt sikkerhetstiltak for person")
    public void updateSikkerhetstiltak(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @Parameter(description = "id som identifiserer sikkerhetstiltak")
                                       @PathVariable Integer id,
                                       @RequestBody SikkerhetstiltakDTO sikkerhetstiltak) {

        artifactUpdateService.updateSikkerhetstiltak(ident, id, sikkerhetstiltak);
    }

    @DeleteMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Slett angitt doedfoedtbarn for person")
    public void deleteDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer doedfoedtbarn")
                                    @PathVariable Integer id) {

        artifactDeleteService.deleteDoedfoedtBarn(ident, id);
    }

    @PutMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Oppdater angitt doedfoedtbarn for person")
    public void updateDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer doedfoedtbarn")
                                    @PathVariable Integer id,
                                    @RequestBody DoedfoedtBarnDTO doedfoedtBarn) {

        artifactUpdateService.updateDoedfoedtBarn(ident, id, doedfoedtBarn);
    }
}