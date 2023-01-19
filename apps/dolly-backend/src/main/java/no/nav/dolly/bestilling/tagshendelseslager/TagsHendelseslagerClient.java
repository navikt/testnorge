package no.nav.dolly.bestilling.tagshendelseslager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@Order(4)
@RequiredArgsConstructor
public class TagsHendelseslagerClient implements ClientRegister {

    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!dollyPerson.getTags().isEmpty()) {

            getPdlIdenter(List.of(dollyPerson.getHovedperson()))
                    .flatMap(identer -> sendTags(identer, dollyPerson.getTags()))
                    .subscribe(log::info);
        }

        if (dollyPerson.getMaster() == Testident.Master.PDL) {
            getPdlIdenter(List.of(dollyPerson.getHovedperson()))
                    .map(tagsHendelseslagerConsumer::publish)
                    .subscribe(response -> log.info("Publish sendt til hendelselager for ident: {} med status: {}",
                            dollyPerson.getHovedperson(), response));
        }

        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        getPdlIdenter(identer)
                .flatMapMany(idents -> tagsHendelseslagerConsumer.deleteTags(idents, Arrays.asList(Tags.values())))
                .subscribe(response -> log.info("Slettet fra TagsHendelselager"));
    }

    private Mono<String> sendTags(List<String> identer, List<Tags> tags) {

        return tagsHendelseslagerConsumer.createTags(identer, tags)
                .map(resultat -> getTagStatus(identer, tags, resultat));
    }

    private String getTagStatus(List<String> identer, List<Tags> tags, TagsOpprettingResponse resultat) {

        return resultat.getStatus().is2xxSuccessful() ?
                String.format("Lagt til tag(s) %s for ident(er) %s",
                        tags.stream().map(Tags::getBeskrivelse).collect(Collectors.joining(",")),
                        String.join(",", identer)) :
                resultat.getMessage();
    }

    private Mono<List<String>> getPdlIdenter(List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .map(person -> Stream.of(List.of(person.getIdent()),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList())
                .flatMap(Flux::fromIterable)
                .collectList();
    }
}
