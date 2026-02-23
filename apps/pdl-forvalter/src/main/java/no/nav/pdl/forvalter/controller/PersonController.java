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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<List<FullPersonDTO>> getPerson(@Parameter(description = "Ident(er) på personer som skal hentes")
                                         @RequestParam(required = false) List<String> identer,
                                         @Parameter(description = "Sidenummer ved sortering på 'sistOppdatert' og nyeste først")
                                         @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                         @Parameter(description = "Sidestørrelse ved sortering på 'sistOppdatert' og nyeste først, default 10")
                                         @RequestParam(required = false) Integer sidestorrelse) {

        return Mono.fromCallable(() -> {
            var effectiveSidestorrelse = sidestorrelse;
            if (isNull(effectiveSidestorrelse)) {
                if (nonNull(identer)) {
                    effectiveSidestorrelse = 200;
                } else {
                    effectiveSidestorrelse = 10;
                }
            }

            return personService.getPerson(identer, Paginering.builder()
                    .sidenummer(sidenummer)
                    .sidestoerrelse(effectiveSidestorrelse)
                    .build());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    @Operation(description = "Opprett person basert på angitte informasjonselementer, minimum er {}")
    public Mono<String> createPerson(@RequestBody BestillingRequestDTO request) {

        return Mono.fromCallable(() -> personService.createPerson(request))
                .subscribeOn(Schedulers.boundedElastic());
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

        return Mono.fromCallable(() -> personService.updatePerson(ident, request, overwrite, relaxed))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(value = "/{ident}/ordre")
    @Operation(description = "Send angitte testperson(er) med relasjoner til PDL")
    public Mono<OrdreResponseDTO> sendPersonTilPdl(@Parameter(description = "Ident på hovedperson som skal sendes")
                                             @PathVariable String ident,
                                             @Parameter(description = "Angir om 3. personer (egne hovedpersoner i Dolly) skal ekskluderes")
                                             @RequestParam(required = false) Boolean ekskluderEksternePersoner) {

        return Mono.fromCallable(() -> {
            metadataTidspunkterService.updateMetadata(ident);
            return pdlOrdreService.send(ident, ekskluderEksternePersoner);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}")
    @Operation(description = "Slett person")
    public Mono<Void> deletePerson(@Parameter(description = "Slett angitt testperson med relasjoner")
                             @PathVariable String ident) {

        return Mono.<Void>fromRunnable(() -> {
            if (!isTestnorgeIdent(ident)) {
                personService.deletePerson(ident);

            } else {
                personService.deleteMasterPdlArtifacter(ident);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/foedsel/{id}")
    @Operation(description = "Slett angitt foedsel for person")
    public Mono<Void> deleteFoedsel(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer foedsel")
                              @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteFoedsel(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/foedested/{id}")
    @Operation(description = "Slett angitt foedested for person")
    public Mono<Void> deleteFoedested(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer foedested")
                                @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteFoedested(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/foedselsdato/{id}")
    @Operation(description = "Slett angitt foedselsdato for person")
    public Mono<Void> deleteFoedselsdato(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer foedselsdato")
                                   @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteFoedselsdato(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/foedsel/{id}")
    @Operation(description = "Oppdatere angitt foedsel for person")
    public Mono<Void> updateFoedsel(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer foedsel")
                              @PathVariable Integer id,
                              @RequestBody FoedselDTO foedsel) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateFoedsel(ident, id, foedsel))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/foedested/{id}")
    @Operation(description = "Oppdatere angitt foedested for person")
    public Mono<Void> updateFoedested(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer foedested")
                                @PathVariable Integer id,
                                @RequestBody FoedestedDTO foedested) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateFoedested(ident, id, foedested))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/foedselsdato/{id}")
    @Operation(description = "Oppdatere angitt foedseldato for person")
    public Mono<Void> updateFoedselsdato(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer foedselsdato")
                                   @PathVariable Integer id,
                                   @RequestBody FoedselsdatoDTO foedselsdato) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateFoedselsdato(ident, id, foedselsdato))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Slett angitt navn for person")
    public Mono<Void> deletePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteNavn(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/navn/{id}")
    @Operation(description = "Oppdatere angitt navn for person")
    public Mono<Void> updatePersonNavn(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer navn")
                                 @PathVariable Integer id,
                                 @RequestBody NavnDTO navn) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateNavn(ident, id, navn))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "Slett angitt kjoenn for person")
    public Mono<Void> deletePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteKjoenn(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/kjoenn/{id}")
    @Operation(description = "oppdatere angitt kjoenn for person")
    public Mono<Void> updatePersonKjoenn(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer kjoenn")
                                   @PathVariable Integer id,
                                   @RequestBody KjoennDTO kjoenn) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateKjoenn(ident, id, kjoenn))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Slett angitt bostedsadresse for person")
    public Mono<Void> deleteBostedsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer bostedsadresse")
                                     @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteBostedsadresse(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/bostedsadresse/{id}")
    @Operation(description = "Oppdater angitt bostedsadresse for person")
    public Mono<Void> updateBostedsadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer bostedsadresse")
                                     @PathVariable Integer id,
                                     @RequestBody BostedadresseDTO bostedadresse) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateBostedsadresse(ident, id, bostedadresse))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Slett angitt kontaktadresse for person")
    public Mono<Void> deleteKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteKontaktadresse(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/kontaktadresse/{id}")
    @Operation(description = "Oppdater angitt kontaktadresse for person")
    public Mono<Void> updateKontaktadresse(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer kontaktadresse")
                                     @PathVariable Integer id,
                                     @RequestBody KontaktadresseDTO kontaktadresse) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateKontaktadresse(ident, id, kontaktadresse))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Slett angitt oppholdsadresse for person")
    public Mono<Void> deleteOppholdsadresse(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer oppholdsadresse")
                                      @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteOppholdsadresse(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/oppholdsadresse/{id}")
    @Operation(description = "Oppdater angitt oppholdsadresse for person")
    public Mono<Void> updateOppholdsadresse(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer oppholdsadresse")
                                      @PathVariable Integer id,
                                      @RequestBody OppholdsadresseDTO oppholdsadresse) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateOppholdsadresse(ident, id, oppholdsadresse))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Slett angitt innflytting for person")
    public Mono<Void> deleteInnflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer innflytting")
                                  @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteInnflytting(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/innflytting/{id}")
    @Operation(description = "Oppdater angitt innflytting for person")
    public Mono<Void> updateInnflytting(@Parameter(description = "Ident for testperson")
                                  @PathVariable String ident,
                                  @Parameter(description = "id som identifiserer innflytting")
                                  @PathVariable Integer id,
                                  @RequestBody InnflyttingDTO innflytting) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateInnflytting(ident, id, innflytting))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Slett angitt utflytting for person")
    public Mono<Void> deleteUtflytting(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer utflytting")
                                 @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteUtflytting(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/utflytting/{id}")
    @Operation(description = "Oppdater angitt utflytting for person")
    public Mono<Void> updateUtflytting(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer utflytting")
                                 @PathVariable Integer id,
                                 @RequestBody UtflyttingDTO utflytting) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateUtflytting(ident, id, utflytting))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Slett angitt deltbosted for person")
    public Mono<Void> deleteDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteDeltBosted(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/deltbosted/{id}")
    @Operation(description = "Oppdater angitt deltbosted for person")
    public Mono<Void> updateDeltBosted(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer deltbosted")
                                 @PathVariable Integer id,
                                 @RequestBody DeltBostedDTO deltBosted) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateDeltBosted(ident, id, deltBosted))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Slett angitt forelderbarnrelasjon for person")
    public Mono<Void> deleteForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                           @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteForelderBarnRelasjon(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/forelderbarnrelasjon/{id}")
    @Operation(description = "Oppdater angitt forelderbarnrelasjon for person")
    public Mono<Void> updateForelderBarnRelasjon(@Parameter(description = "Ident for testperson")
                                           @PathVariable String ident,
                                           @Parameter(description = "id som identifiserer forelderbarnrelasjon")
                                           @PathVariable Integer id,
                                           @RequestBody ForelderBarnRelasjonDTO forelderBarnRelasjon) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateForelderBarnRelasjon(ident, id, forelderBarnRelasjon))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/foreldreansvar/{id}")
    @Operation(description = "Slett angitt foreldreansvar for person (barn)")
    public Mono<Void> deleteForeldreansvar(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer foreldreansvar")
                                     @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteForeldreansvar(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/foreldreansvar/{id}")
    @Operation(description = "Oppdater angitt foreldreansvar for person (barn)")
    public Mono<Void> updateForeldreansvar(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer foreldreansvar")
                                     @PathVariable Integer id,
                                     @RequestBody ForeldreansvarDTO foreldreansvar) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateForeldreansvar(ident, id, foreldreansvar))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Slett angitt kontaktinformasjonfordoedsbo på person")
    public Mono<Void> deleteKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                                   @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteKontaktinformasjonForDoedsbo(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/kontaktinformasjonfordoedsbo/{id}")
    @Operation(description = "Oppdater angitt kontaktinformasjonfordoedsbo på person")
    public Mono<Void> updateKontaktinformasjonForDoedsbo(@Parameter(description = "Ident for testperson")
                                                   @PathVariable String ident,
                                                   @Parameter(description = "id som identifiserer kontaktinformasjonfordoedsbo")
                                                   @PathVariable Integer id,
                                                   @RequestBody KontaktinformasjonForDoedsboDTO kontaktinformasjonForDoedsbo) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateKontaktinformasjonForDoedsbo(ident, id, kontaktinformasjonForDoedsbo))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Slett angitt utenlandskidentifikasjonsnummer for person")
    public Mono<Void> deleteUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                      @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteUtenlandskIdentifikasjonsnummer(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/utenlandskidentifikasjonsnummer/{id}")
    @Operation(description = "Oppdater angitt utenlandskidentifikasjonsnummer for person")
    public Mono<Void> updateUtenlandskIdentifikasjonsnummer(@Parameter(description = "Ident for testperson")
                                                      @PathVariable String ident,
                                                      @Parameter(description = "id som identifiserer utenlandskidentifikasjonsnummer")
                                                      @PathVariable Integer id,
                                                      @RequestBody UtenlandskIdentifikasjonsnummerDTO utenlandskIdentifikasjonsnummer) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateUtenlandskIdentifikasjonsnummer(ident, id, utenlandskIdentifikasjonsnummer))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Slett angitt falskidentitet for person")
    public Mono<Void> deleteFalskIdentitet(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer falskidentitet")
                                     @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteFalskIdentitet(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/falskidentitet/{id}")
    @Operation(description = "Oppdater angitt falskidentitet for person")
    public Mono<Void> updateFalskIdentitet(@Parameter(description = "Ident for testperson")
                                     @PathVariable String ident,
                                     @Parameter(description = "id som identifiserer falskidentitet")
                                     @PathVariable Integer id,
                                     @RequestBody FalskIdentitetDTO falskIdentitet) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateFalskIdentitet(ident, id, falskIdentitet))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Slett angitt adressebeskyttelse for person")
    public Mono<Void> deleteAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer adressebeskyttelse")
                                         @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteAdressebeskyttelse(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/adressebeskyttelse/{id}")
    @Operation(description = "Oppdater angitt adressebeskyttelse for person")
    public Mono<Void> updateAdressebeskyttelse(@Parameter(description = "Ident for testperson")
                                         @PathVariable String ident,
                                         @Parameter(description = "id som identifiserer adressebeskyttelse")
                                         @PathVariable Integer id,
                                         @RequestBody AdressebeskyttelseDTO adressebeskyttelse) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateAdressebeskyttelse(ident, id, adressebeskyttelse))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Slett angitt doedsfall for person")
    public Mono<Void> deleteDoedsfall(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedsfall")
                                @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteDoedsfall(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/doedsfall/{id}")
    @Operation(description = "Oppdater angitt doedsfall for person")
    public Mono<Void> updateDoedsfall(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer doedsfall")
                                @PathVariable Integer id,
                                @RequestBody DoedsfallDTO doedsfall) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateDoedsfall(ident, id, doedsfall))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Slett angitt folkeregisterpersonstatus for person")
    public Mono<Void> deleteFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                                @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteFolkeregisterPersonstatus(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/folkeregisterpersonstatus/{id}")
    @Operation(description = "Oppdater angitt folkeregisterpersonstatus for person")
    public Mono<Void> updateFolkeregisterPersonstatus(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer folkeregisterpersonstatus")
                                                @PathVariable Integer id,
                                                @RequestBody FolkeregisterPersonstatusDTO folkeregisterPersonstatus) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateFolkeregisterPersonstatus(ident, id, folkeregisterPersonstatus))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Slett angitt tilrettelagtkommunikasjon for person")
    public Mono<Void> deleteTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteTilrettelagtKommunikasjon(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/tilrettelagtkommunikasjon/{id}")
    @Operation(description = "Oppdater angitt tilrettelagtkommunikasjon for person")
    public Mono<Void> updateTilrettelagtKommunikasjon(@Parameter(description = "Ident for testperson")
                                                @PathVariable String ident,
                                                @Parameter(description = "id som identifiserer tilrettelagtkommunikasjon")
                                                @PathVariable Integer id,
                                                @RequestBody TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateTilrettelagtKommunikasjon(ident, id, tilrettelagtKommunikasjon))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Slett angitt statsborgerskap for person")
    public Mono<Void> deleteStatsborgerskap(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer statsborgerskap")
                                      @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteStatsborgerskap(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/statsborgerskap/{id}")
    @Operation(description = "Oppdater angitt statsborgerskap for person")
    public Mono<Void> updateStatsborgerskap(@Parameter(description = "Ident for testperson")
                                      @PathVariable String ident,
                                      @Parameter(description = "id som identifiserer statsborgerskap")
                                      @PathVariable Integer id,
                                      @RequestBody StatsborgerskapDTO statsborgerskap) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateStatsborgerskap(ident, id, statsborgerskap))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Slett angitt opphold for person")
    public Mono<Void> deleteOpphold(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer opphold")
                              @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteOpphold(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/opphold/{id}")
    @Operation(description = "Oppdater angitt opphold for person")
    public Mono<Void> deleteOpphold(@Parameter(description = "Ident for testperson")
                              @PathVariable String ident,
                              @Parameter(description = "id som identifiserer opphold")
                              @PathVariable Integer id,
                              @RequestBody OppholdDTO opphold) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateOpphold(ident, id, opphold))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Slett angitt sivilstand for person")
    public Mono<Void> deleteSivilstand(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer sivilstand")
                                 @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteSivilstand(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/sivilstand/{id}")
    @Operation(description = "Oppdater angitt sivilstand for person")
    public Mono<Void> updateSivilstand(@Parameter(description = "Ident for testperson")
                                 @PathVariable String ident,
                                 @Parameter(description = "id som identifiserer sivilstand")
                                 @PathVariable Integer id,
                                 @RequestBody SivilstandDTO sivilstand) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateSivilstand(ident, id, sivilstand))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/telefonnummer/{id}")
    @Operation(description = "Slett angitt telefonnummer for person")
    public Mono<Void> deleteTelefonnummer(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer telefonnummer")
                                    @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteTelefonnummer(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/telefonnummer")
    @Operation(description = "Oppdater telefonnumre for person")
    public Mono<Void> updateTelefonnumre(@Parameter(description = "Ident for testperson")
                                   @PathVariable String ident,
                                   @Parameter(description = "id som identifiserer telefonnummer")
                                   @RequestBody List<TelefonnummerDTO> telefonnumre) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateTelefonnummer(ident, telefonnumre))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Slett angitt vergemaal for person")
    public Mono<Void> deleteVergemaal(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer vergemaal")
                                @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteVergemaal(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/vergemaal/{id}")
    @Operation(description = "Oppdater angitt vergemaal for person")
    public Mono<Void> updateVergemaal(@Parameter(description = "Ident for testperson")
                                @PathVariable String ident,
                                @Parameter(description = "id som identifiserer vergemaal")
                                @PathVariable Integer id,
                                @RequestBody VergemaalDTO vergemaal) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateVergemaal(ident, id, vergemaal))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/sikkerhetstiltak/{id}")
    @Operation(description = "Slett angitt sikkerhetstiltak for person")
    public Mono<Void> deleteSikkerhetstiltak(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @Parameter(description = "id som identifiserer sikkerhetstiltak")
                                       @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteSikkerhetstiltak(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/sikkerhetstiltak/{id}")
    @Operation(description = "Oppdater angitt sikkerhetstiltak for person")
    public Mono<Void> updateSikkerhetstiltak(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @Parameter(description = "id som identifiserer sikkerhetstiltak")
                                       @PathVariable Integer id,
                                       @RequestBody SikkerhetstiltakDTO sikkerhetstiltak) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateSikkerhetstiltak(ident, id, sikkerhetstiltak))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Slett angitt doedfoedtbarn for person")
    public Mono<Void> deleteDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer doedfoedtbarn")
                                    @PathVariable Integer id) {

        return Mono.<Void>fromRunnable(() -> artifactDeleteService.deleteDoedfoedtBarn(ident, id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/doedfoedtbarn/{id}")
    @Operation(description = "Oppdater angitt doedfoedtbarn for person")
    public Mono<Void> updateDoedfoedtBarn(@Parameter(description = "Ident for testperson")
                                    @PathVariable String ident,
                                    @Parameter(description = "id som identifiserer doedfoedtbarn")
                                    @PathVariable Integer id,
                                    @RequestBody DoedfoedtBarnDTO doedfoedtBarn) {

        return Mono.<Void>fromRunnable(() -> artifactUpdateService.updateDoedfoedtBarn(ident, id, doedfoedtBarn))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
