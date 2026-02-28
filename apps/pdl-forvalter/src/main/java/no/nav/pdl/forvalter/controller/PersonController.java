package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.service.ArtifactDeleteService;
import no.nav.pdl.forvalter.service.ArtifactUpdateService;
import no.nav.pdl.forvalter.service.MetadataTidspunkterService;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/personer", produces = "application/json; charset=utf-8")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;
    private final ArtifactDeleteService artifactDeleteService;
    private final ArtifactUpdateService artifactUpdateService;
    private final MetadataTidspunkterService metadataTidspunkterService;

    @GetMapping
    @Operation(description = "Hent person(er) med angitt(e) ident(er) eller alle")
    public Flux<FullPersonDTO> getPerson(@Parameter(description = "Ident(er) på personer som skal hentes")
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

    @PostMapping
    @Operation(description = "Opprett person basert på angitte informasjonselementer, minimum er {}")
    public Mono<String> createPerson(@RequestBody BestillingRequestDTO request) {

        return personService.createPerson(request);
    }

    @PutMapping(value = "/{ident}")
    @Operation(description = "Oppdater testperson basert på angitte informasjonselementer")
    public Mono<String> updatePerson(@Parameter(description = "Ident på testperson som skal oppdateres")
                               @PathVariable String ident,
                               @RequestBody PersonUpdateRequestDTO request,
                               @Parameter(description = "Angir om tidligere historie skal skrives over")
                               @RequestHeader(required = false) Boolean overwrite,
                               @Parameter(description = "Angir om validering skal sløyfes for navn, adresse m.fl.")
                               @RequestHeader(required = false) Boolean relaxed) {

        return personService.updatePerson(ident, request, overwrite, relaxed);
    }

    @Transactional
    @PostMapping(value = "/{ident}/ordre")
    @Operation(description = "Send angitte testperson(er) med relasjoner til PDL")
    public OrdreResponseDTO sendPersonTilPdl(@Parameter(description = "Ident på hovedperson som skal sendes")
                                             @PathVariable String ident,
                                             @Parameter(description = "Angir om 3. personer (egne hovedpersoner i Dolly) skal ekskluderes")
                                             @RequestParam(required = false) Boolean ekskluderEksternePersoner) {

        metadataTidspunkterService.updateMetadata(ident);

        return pdlOrdreService.send(ident, ekskluderEksternePersoner);
    }

    @DeleteMapping(value = "/{ident}")
    @Operation(description = "Slett person")
    public Mono<Void> deletePerson(@Parameter(description = "Slett angitt testperson med relasjoner")
                             @PathVariable String ident) {

        if (!isTestnorgeIdent(ident)) {
            return personService.deletePerson(ident);

        } else {
            return personService.deleteMasterPdlArtifacter(ident);
        }
    }

    @DeleteMapping(value = "/{ident}/foedsel/{id}")
    @Operation(description = "Slett angitt foedsel for person")
    public Mono<Void> deleteFoedsel(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer foedsel")
                              @PathVariable Integer id) {

        return artifactDeleteService.deleteFoedsel(ident, id);
    }

    @DeleteMapping(value = "/{ident}/foedested/{id}")
    @Operation(description = "Slett angitt foedested for person")
    public Mono<Void> deleteFoedested(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer foedested")
                                @PathVariable Integer id) {

        return artifactDeleteService.deleteFoedested(ident, id);
    }

    @DeleteMapping(value = "/{ident}/foedselsdato/{id}")
    @Operation(description = "Slett angitt foedselsdato for person")
    public Mono<Void> deleteFoedselsdato(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer foedselsdato")
                                   @PathVariable Integer id) {

        return artifactDeleteService.deleteFoedselsdato(ident, id);
    }

    @PutMapping(value = "/{ident}/foedsel/{id}")
    @Operation(description = "Oppdatere angitt foedsel for person")
    public Mono<Void> updateFoedsel(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer foedsel")
                              @PathVariable Integer id,
                              @RequestBody FoedselDTO foedsel) {

        return artifactUpdateService.updateFoedsel(ident, id, foedsel);
    }

    @PutMapping(value = "/{ident}/foedested/{id}")
    @Operation(description = "Oppdatere angitt foedested for person")
    public Mono<Void> updateFoedested(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer foedested")
                                @PathVariable Integer id,
                                @RequestBody FoedestedDTO foedested) {

        return artifactUpdateService.updateFoedested(ident, id, foedested);
    }

    @PutMapping(value = "/{ident}/foedselsdato/{id}")
    @Operation(description = "Oppdatere angitt foedseldato for person")
    public Mono<Void> updateFoedselsdato(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer foedselsdato")
                                   @PathVariable Integer id,
                                   @RequestBody FoedselsdatoDTO foedselsdato) {

        return artifactUpdateService.updateFoedselsdato(ident, id, foedselsdato);
    }

    @DeleteMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Slett angitt navn for person")
    public Mono<Void> deletePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id) {

        return artifactDeleteService.deleteNavn(ident, id);
    }

    @PutMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Oppdatere angitt navn for person")
    public Mono<Void> updatePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id,
                                 @RequestBody NavnDTO navn) {

        return artifactUpdateService.updateNavn(ident, id, navn);
    }

    @DeleteMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "Slett angitt kjoenn for person")
    public Mono<Void> deletePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id) {

        return artifactDeleteService.deleteKjoenn(ident, id);
    }

    @PutMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "oppdatere angitt kjoenn for person")
    public Mono<Void> updatePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id,
                                   @RequestBody KjoennDTO kjoenn) {

        return artifactUpdateService.updateKjoenn(ident, id, kjoenn);
    }

    @DeleteMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Slett angitt bostedsadresse for person")
    public Mono<Void> deleteBostedsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer bostedsadresse")
                                     @PathVariable Integer id) {

        return artifactDeleteService.deleteBostedsadresse(ident, id);
    }

    @PutMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Oppdater angitt bostedsadresse for person")
    public Mono<Void> updateBostedsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer bostedsadresse")
                                     @PathVariable Integer id,
                                     @RequestBody BostedadresseDTO bostedadresse) {

        return artifactUpdateService.updateBostedsadresse(ident, id, bostedadresse);
    }

    @DeleteMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Slett angitt kontaktadresse for person")
    public Mono<Void> deleteKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id) {

        return artifactDeleteService.deleteKontaktadresse(ident, id);
    }

    @PutMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Oppdater angitt kontaktadresse for person")
    public Mono<Void> updateKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id,
                                     @RequestBody KontaktadresseDTO kontaktadresse) {

        return artifactUpdateService.updateKontaktadresse(ident, id, kontaktadresse);
    }

    @DeleteMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Slett angitt oppholdsadresse for person")
    public Mono<Void> deleteOppholdsadresse(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer oppholdsadresse")
                                      @PathVariable Integer id) {

        return artifactDeleteService.deleteOppholdsadresse(ident, id);
    }

    @PutMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Oppdater angitt oppholdsadresse for person")
    public Mono<Void> updateOppholdsadresse(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer oppholdsadresse")
                                      @PathVariable Integer id,
                                      @RequestBody OppholdsadresseDTO oppholdsadresse) {

        return artifactUpdateService.updateOppholdsadresse(ident, id, oppholdsadresse);
    }

    @DeleteMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Slett angitt innflytting for person")
    public Mono<Void> deleteInnflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer innflytting")
                                  @PathVariable Integer id) {

        return artifactDeleteService.deleteInnflytting(ident, id);
    }

    @PutMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Oppdater angitt innflytting for person")
    public Mono<Void> updateInnflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer innflytting")
                                  @PathVariable Integer id,
                                  @RequestBody InnflyttingDTO innflytting) {

        return artifactUpdateService.updateInnflytting(ident, id, innflytting);
    }

    @DeleteMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Slett angitt utflytting for person")
    public Mono<Void> deleteUtflytting(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer utflytting")
                                 @PathVariable Integer id) {

        return artifactDeleteService.deleteUtflytting(ident, id);
    }

    @PutMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Oppdater angitt utflytting for person")
    public Mono<Void> updateUtflytting(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer utflytting")
                                 @PathVariable Integer id,
                                 @RequestBody UtflyttingDTO utflytting) {

        return artifactUpdateService.updateUtflytting(ident, id, utflytting);
    }

    @DeleteMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Slett angitt deltbosted for person")
    public Mono<Void> deleteDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id) {

        return artifactDeleteService.deleteDeltBosted(ident, id);
    }

    @PutMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Oppdater angitt deltbosted for person")
    public Mono<Void> updateDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id,
                                 @RequestBody DeltBostedDTO deltBosted) {

        return artifactUpdateService.updateDeltBosted(ident, id, deltBosted);
    }

    @DeleteMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Slett angitt forelderbarnrelasjon for person")
    public Mono<Void> deleteForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                           @PathVariable Integer id) {

        return artifactDeleteService.deleteForelderBarnRelasjon(ident, id);
    }

    @PutMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Oppdater angitt forelderbarnrelasjon for person")
    public Mono<Void> updateForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                           @PathVariable Integer id,
                                           @RequestBody ForelderBarnRelasjonDTO forelderBarnRelasjon) {

        return artifactUpdateService.updateForelderBarnRelasjon(ident, id, forelderBarnRelasjon);
    }

    @DeleteMapping(value = "/{ident}/foreldreansvar/{id}")
    @Operation(description = "Slett angitt foreldreansvar for person (barn)")
    public Mono<Void> deleteForeldreansvar(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer foreldreansvar")
                                     @PathVariable Integer id) {

        return artifactDeleteService.deleteForeldreansvar(ident, id);
    }

    @PutMapping(value = "/{ident}/foreldreansvar/{id}")
    @Operation(description = "Oppdater angitt foreldreansvar for person (barn)")
    public Mono<Void> updateForeldreansvar(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer foreldreansvar")
                                     @PathVariable Integer id,
                                     @RequestBody ForeldreansvarDTO foreldreansvar) {

        return artifactUpdateService.updateForeldreansvar(ident, id, foreldreansvar);
    }

    @DeleteMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Slett angitt kontaktinformasjonfordoedsbo på person")
    public Mono<Void> deleteKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                                   @PathVariable Integer id) {

        return artifactDeleteService.deleteKontaktinformasjonForDoedsbo(ident, id);
    }

    @PutMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Oppdater angitt kontaktinformasjonfordoedsbo på person")
    public Mono<Void> updateKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                                   @PathVariable Integer id,
                                                   @RequestBody KontaktinformasjonForDoedsboDTO kontaktinformasjonForDoedsbo) {

        return artifactUpdateService.updateKontaktinformasjonForDoedsbo(ident, id, kontaktinformasjonForDoedsbo);
    }

    @DeleteMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Slett angitt utenlandskidentifikasjonsnummer for person")
    public Mono<Void> deleteUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                      @PathVariable Integer id) {

        return artifactDeleteService.deleteUtenlandskIdentifikasjonsnummer(ident, id);
    }

    @PutMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Oppdater angitt utenlandskidentifikasjonsnummer for person")
    public Mono<Void> updateUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                      @PathVariable Integer id,
                                                      @RequestBody UtenlandskIdentifikasjonsnummerDTO utenlandskIdentifikasjonsnummer) {

        return artifactUpdateService.updateUtenlandskIdentifikasjonsnummer(ident, id, utenlandskIdentifikasjonsnummer);
    }

    @DeleteMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Slett angitt falskidentitet for person")
    public Mono<Void> deleteFalskIdentitet(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer falskidentitet")
                                     @PathVariable Integer id) {

        return artifactDeleteService.deleteFalskIdentitet(ident, id);
    }

    @PutMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Oppdater angitt falskidentitet for person")
    public Mono<Void> updateFalskIdentitet(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer falskidentitet")
                                     @PathVariable Integer id,
                                     @RequestBody FalskIdentitetDTO falskIdentitet) {

        return artifactUpdateService.updateFalskIdentitet(ident, id, falskIdentitet);
    }

    @DeleteMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Slett angitt adressebeskyttelse for person")
    public Mono<Void> deleteAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer adressebeskyttelse")
                                         @PathVariable Integer id) {

        return artifactDeleteService.deleteAdressebeskyttelse(ident, id);
    }

    @PutMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Oppdater angitt adressebeskyttelse for person")
    public Mono<Void> updateAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer adressebeskyttelse")
                                         @PathVariable Integer id,
                                         @RequestBody AdressebeskyttelseDTO adressebeskyttelse) {

        return artifactUpdateService.updateAdressebeskyttelse(ident, id, adressebeskyttelse);
    }

    @DeleteMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Slett angitt doedsfall for person")
    public Mono<Void> deleteDoedsfall(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedsfall")
                                @PathVariable Integer id) {

        return artifactDeleteService.deleteDoedsfall(ident, id);
    }

    @PutMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Oppdater angitt doedsfall for person")
    public Mono<Void> updateDoedsfall(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedsfall")
                                @PathVariable Integer id,
                                @RequestBody DoedsfallDTO doedsfall) {

        return artifactUpdateService.updateDoedsfall(ident, id, doedsfall);
    }

    @DeleteMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Slett angitt folkeregisterpersonstatus for person")
    public Mono<Void> deleteFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                                @PathVariable Integer id) {

        return artifactDeleteService.deleteFolkeregisterPersonstatus(ident, id);
    }

    @PutMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Oppdater angitt folkeregisterpersonstatus for person")
    public Mono<Void> updateFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                                @PathVariable Integer id,
                                                @RequestBody FolkeregisterPersonstatusDTO folkeregisterPersonstatus) {

        return artifactUpdateService.updateFolkeregisterPersonstatus(ident, id, folkeregisterPersonstatus);
    }

    @DeleteMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Slett angitt tilrettelagtkommunikasjon for person")
    public Mono<Void> deleteTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id) {

        return artifactDeleteService.deleteTilrettelagtKommunikasjon(ident, id);
    }

    @PutMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Oppdater angitt tilrettelagtkommunikasjon for person")
    public Mono<Void> updateTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id,
                                                @RequestBody TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

        return artifactUpdateService.updateTilrettelagtKommunikasjon(ident, id, tilrettelagtKommunikasjon);
    }

    @DeleteMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Slett angitt statsborgerskap for person")
    public Mono<Void> deleteStatsborgerskap(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer statsborgerskap")
                                      @PathVariable Integer id) {

        return artifactDeleteService.deleteStatsborgerskap(ident, id);
    }

    @PutMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Oppdater angitt statsborgerskap for person")
    public Mono<Void> updateStatsborgerskap(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer statsborgerskap")
                                      @PathVariable Integer id,
                                      @RequestBody StatsborgerskapDTO statsborgerskap) {

        return artifactUpdateService.updateStatsborgerskap(ident, id, statsborgerskap);
    }

    @DeleteMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Slett angitt opphold for person")
    public Mono<Void> deleteOpphold(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer opphold")
                              @PathVariable Integer id) {

        return artifactDeleteService.deleteOpphold(ident, id);
    }

    @PutMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Oppdater angitt opphold for person")
    public Mono<Void> deleteOpphold(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer opphold")
                              @PathVariable Integer id,
                              @RequestBody OppholdDTO opphold) {

        return artifactUpdateService.updateOpphold(ident, id, opphold);
    }

    @DeleteMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Slett angitt sivilstand for person")
    public Mono<Void> deleteSivilstand(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer sivilstand")
                                 @PathVariable Integer id) {

        return artifactDeleteService.deleteSivilstand(ident, id);
    }

    @PutMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Oppdater angitt sivilstand for person")
    public Mono<Void> updateSivilstand(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer sivilstand")
                                 @PathVariable Integer id,
                                 @RequestBody SivilstandDTO sivilstand) {

        return artifactUpdateService.updateSivilstand(ident, id, sivilstand);
    }

    @DeleteMapping(value = "/{ident}/telefonnummer/{id}")
    @Operation(description = "Slett angitt telefonnummer for person")
    public Mono<Void> deleteTelefonnummer(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer telefonnummer")
                                    @PathVariable Integer id) {

        return artifactDeleteService.deleteTelefonnummer(ident, id);
    }

    @PutMapping(value = "/{ident}/telefonnummer")
    @Operation(description = "Oppdater telefonnumre for person")
    public Mono<Void> updateTelefonnumre(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer telefonnummer")
                                   @RequestBody List<TelefonnummerDTO> telefonnumre) {

        return artifactUpdateService.updateTelefonnummer(ident, telefonnumre);
    }

    @DeleteMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Slett angitt vergemaal for person")
    public Mono<Void> deleteVergemaal(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer vergemaal")
                                @PathVariable Integer id) {

        return artifactDeleteService.deleteVergemaal(ident, id);
    }

    @PutMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Oppdater angitt vergemaal for person")
    public Mono<Void> updateVergemaal(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer vergemaal")
                                @PathVariable Integer id,
                                @RequestBody VergemaalDTO vergemaal) {

        return artifactUpdateService.updateVergemaal(ident, id, vergemaal);
    }

    @DeleteMapping(value = "/{ident}/sikkerhetstiltak/{id}")
    @Operation(description = "Slett angitt sikkerhetstiltak for person")
    public Mono<Void> deleteSikkerhetstiltak(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @Parameter(description = "id som identifiserer sikkerhetstiltak")
                                       @PathVariable Integer id) {

        return artifactDeleteService.deleteSikkerhetstiltak(ident, id);
    }

    @PutMapping(value = "/{ident}/sikkerhetstiltak/{id}")
    @Operation(description = "Oppdater angitt sikkerhetstiltak for person")
    public Mono<Void> updateSikkerhetstiltak(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @Parameter(description = "id som identifiserer sikkerhetstiltak")
                                       @PathVariable Integer id,
                                       @RequestBody SikkerhetstiltakDTO sikkerhetstiltak) {

        return artifactUpdateService.updateSikkerhetstiltak(ident, id, sikkerhetstiltak);
    }

    @DeleteMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Slett angitt doedfoedtbarn for person")
    public Mono<Void> deleteDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer doedfoedtbarn")
                                    @PathVariable Integer id) {

        return artifactDeleteService.deleteDoedfoedtBarn(ident, id);
    }

    @PutMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Oppdater angitt doedfoedtbarn for person")
    public Mono<Void> updateDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer doedfoedtbarn")
                                    @PathVariable Integer id,
                                    @RequestBody DoedfoedtBarnDTO doedfoedtBarn) {

        return artifactUpdateService.updateDoedfoedtBarn(ident, id, doedfoedtBarn);
    }
}