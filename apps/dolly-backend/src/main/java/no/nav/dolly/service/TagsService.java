package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.Tags.DOLLY;

@Service
@RequiredArgsConstructor
public class TagsService {

    private final TestgruppeRepository testgruppeRepository;
    private final PersonServiceConsumer personServiceConsumer;
    private final IdentRepository identRepository;
    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;

    public Mono<TagsOpprettingResponse> sendTags(Long gruppeId, List<String> tags) {

        val oppdatertGruppe = new AtomicReference<Testgruppe>();

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Fant ikke gruppe på id: %s", gruppeId))))
                .flatMapMany(gruppe -> Flux.fromArray(Tags.values())
                        .filter(tag -> !Objects.equals(DOLLY, tag))
                        .map(Tags::name)
                        .filter(tags::contains)
                        .collect(Collectors.joining(","))
                        .doOnNext(gruppe::setTags)
                        .thenReturn(gruppe))
                .flatMap(testgruppeRepository::save)
                .doOnNext(oppdatertGruppe::set)
                .flatMap(gruppe -> identRepository.findByGruppeId(gruppeId, Pageable.unpaged())
                        .map(Testident::getIdent)
                        .collectList()
                        .flatMapMany(identer -> {

                            if (!identer.isEmpty()) {
                                return personServiceConsumer.getPdlPersoner(identer)
                                        .filter(pdlBolk -> nonNull(pdlBolk) && nonNull(pdlBolk.getData()))
                                        .map(PdlPersonBolk::getData)
                                        .map(PdlPersonBolk.Data::getHentPersonBolk)
                                        .flatMap(Flux::fromIterable)
                                        .filter(personBolk -> nonNull(personBolk.getPerson()))
                                        .flatMap(person -> Flux.merge(Mono.just(person.getIdent()),
                                                        Flux.fromIterable(person.getPerson().getSivilstand())
                                                                .filter(sivilstand -> nonNull(sivilstand.getRelatertVedSivilstand()))
                                                                .map(PdlPerson.Sivilstand::getRelatertVedSivilstand),
                                                        Flux.fromIterable(person.getPerson().getForelderBarnRelasjon())
                                                                .filter(relasjon -> nonNull(relasjon.getRelatertPersonsIdent()))
                                                                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent),
                                                        Flux.fromIterable(person.getPerson().getForeldreansvar())
                                                                .filter(foreldreansvar -> nonNull(foreldreansvar.getAnsvarlig()))
                                                                .map(ForeldreansvarDTO::getAnsvarlig),
                                                        Flux.fromIterable(person.getPerson().getFullmakt())
                                                                .filter(fullmakt -> nonNull(fullmakt.getMotpartsPersonident()))
                                                                .map(FullmaktDTO::getMotpartsPersonident),
                                                        Flux.fromIterable(person.getPerson().getVergemaalEllerFremtidsfullmakt())
                                                                .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                                                .filter(verge -> nonNull(verge.getMotpartsPersonident()))
                                                                .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident),
                                                        Flux.fromIterable(person.getPerson().getKontaktinformasjonForDoedsbo())
                                                                .filter(kontakt -> nonNull(kontakt.getPersonSomKontakt()))
                                                                .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                                                .filter(kontakt -> nonNull(kontakt.getIdentifikasjonsnummer()))
                                                                .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer))
                                                .distinct())
                                        .collectList()
                                        .flatMap(bolkPersoner -> updateTags(bolkPersoner, oppdatertGruppe.get().getTags()));

                            } else {
                                return Mono.just(TagsOpprettingResponse.builder()
                                        .status(HttpStatus.OK)
                                        .build());
                            }
                        }))
                .next();
    }

    private Mono<TagsOpprettingResponse> updateTags(List<String> identer, List<Tags> tags) {

        return tagsHendelseslagerConsumer.getTagsBolk(identer)
                .flatMapMany(tagsBolk -> Flux.fromIterable(identer)
                        .flatMap(ident -> {

                            val tagsForDelete = tagsForDelete(tags, tagsBolk.get(ident));
                            if (!tagsForDelete.isEmpty()) {
                                return tagsHendelseslagerConsumer.deleteTags(List.of(ident), tagsForDelete)
                                        .then()
                                        .thenReturn(ident);
                            } else {
                                return Mono.just(ident);
                            }
                        })
                        .flatMap(ident -> {

                            val tagsForCreate = tagsForCreate(tags, tagsBolk.get(ident));
                            if (!tagsForCreate.isEmpty()) {
                                return tagsHendelseslagerConsumer.createTags(List.of(ident), tagsForCreate)
                                        .thenReturn(ident);
                            } else {
                                return Mono.just(ident);
                            }
                        }))
                .then(Mono.just(TagsOpprettingResponse.builder()
                        .status(HttpStatus.OK)
                        .build()));
    }

    private List<Tags> tagsForDelete(List<Tags> request, List<String> existing) {

        return Stream.of(Tags.values())
                .filter(tag -> !tag.equals(DOLLY))
                .filter(tag -> existing.contains(tag.name()) && !request.contains(tag))
                .toList();
    }

    private List<Tags> tagsForCreate(List<Tags> request, List<String> existing) {

        return Stream.of(Tags.values())
                .filter(tag -> !tag.equals(DOLLY))
                .filter(tag -> !existing.contains(tag.name()) && request.contains(tag))
                .toList();
    }
}
