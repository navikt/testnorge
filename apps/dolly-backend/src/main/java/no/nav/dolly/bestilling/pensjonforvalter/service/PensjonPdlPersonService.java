package no.nav.dolly.bestilling.pensjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonPdlPersonService {

    private final Norg2Consumer norg2Consumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final PersonServiceConsumer personServiceConsumer;

    public Mono<Tuple2<List<PdlPersonBolk.PersonBolk>, String>> getUtvidetPersondata(String ident) {

        return getIdenterRelasjoner(ident)
                .collectList()
                .map(this::getPersonData)
                .flatMap(persondata -> Mono.zip(
                        getPdlPerson(persondata),
                        getNavEnhetNr(persondata, ident)))
                .doOnNext(utvidetPersondata -> {
                    if (utvidetPersondata.getT1().isEmpty()) {
                        log.warn("Persondata for {} gir tom response fra PDL", ident);
                    }
                });
    }

    private Flux<String> getIdenterRelasjoner(String ident) {

        return Flux.concat(Flux.just(ident),
                        getPersonData(List.of(ident))
                                .map(PdlPersonBolk.Data::getHentPersonBolk)
                                .flatMap(Flux::fromIterable)
                                .filter(personBolk -> nonNull(personBolk.getPerson()))
                                .flatMap(person -> Flux.fromStream(Stream.of(
                                                person.getPerson().getSivilstand().stream()
                                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getForelderBarnRelasjon().stream()
                                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getFullmakt().stream()
                                                        .map(FullmaktDTO::getMotpartsPersonident))
                                        .flatMap(Function.identity()))),
                        pdlDataConsumer.getPersoner(List.of(ident))
                                .flatMap(person -> Flux.fromIterable(person.getRelasjoner())
                                        .filter(relasjon -> relasjon.getRelasjonType() != RelasjonType.GAMMEL_IDENTITET)
                                        .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                        .map(PersonDTO::getIdent)))
                .distinct();
    }

    private Flux<PdlPersonBolk.Data> getPersonData(List<String> identer) {

        return personServiceConsumer.getPdlPersoner(identer)
                .doOnNext(bolk -> {
                    if (isNull(bolk.getData()) || bolk.getData().getHentPersonBolk().stream()
                            .anyMatch(personBolk -> isNull(personBolk.getPerson()))) {
                        log.warn("PDL-data mangler for {}, bolkPersoner: {}, ", String.join(", ", identer), bolk);
                    }
                })
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData);
    }

    private static Mono<List<PdlPersonBolk.PersonBolk>> getPdlPerson(Flux<PdlPersonBolk.Data> persondata) {

        return persondata
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .collectList();
    }

    private Mono<String> getNavEnhetNr(Flux<PdlPersonBolk.Data> persondata, String ident) {

        return persondata
                .doOnNext(data -> {
                    if (isNull(data.getHentGeografiskTilknytningBolk()) ||
                            data.getHentGeografiskTilknytningBolk().stream()
                                    .anyMatch(bolk -> isNull(bolk.getGeografiskTilknytning()))) {

                        log.warn("GT for {} gir tom response fra PDL", ident);
                    }
                })
                .filter(data -> nonNull(data.getHentGeografiskTilknytningBolk()))
                .map(PdlPersonBolk.Data::getHentGeografiskTilknytningBolk)
                .flatMap(Flux::fromIterable)
                .filter(data -> nonNull(data.getGeografiskTilknytning()))
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(PensjonforvalterUtils::getGeografiskTilknytning)
                .flatMap(norg2Consumer::getNorgEnhet)
                .filter(norgenhet -> nonNull(norgenhet.getEnhetNr()))
                .map(Norg2EnhetResponse::getEnhetNr)
                .collectList()
                .doOnNext(norgdata -> log.info("Mottatt norgdata: {}", norgdata))
                .map(norgdata -> !norgdata.isEmpty() ? norgdata.getFirst() : "0315");
    }
}
