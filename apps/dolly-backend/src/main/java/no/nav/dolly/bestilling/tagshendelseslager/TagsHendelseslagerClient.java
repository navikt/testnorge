package no.nav.dolly.bestilling.tagshendelseslager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.dto.HendelselagerResponse;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagsHendelseslagerClient implements ClientRegister {

    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;
    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.from(getPdlIdenter(List.of(dollyPerson.getIdent()))
                .collectList()
                .flatMap(identer -> Flux.concat(
                                Flux.just(dollyPerson.getTags())
                                        .filter(tags -> !tags.isEmpty())
                                        .flatMap(tags -> tagsHendelseslagerConsumer.createTags(identer, tags)
                                                .map(resultat -> getTagStatus(identer, tags, resultat))),
                                Flux.just(identer)
                                        .filter(identer1 -> dollyPerson.getMaster() == Testident.Master.PDL)
                                        .flatMap(tagsHendelseslagerConsumer::publish)
                                        .map(status -> getPublishStatus(identer, status)))
                        .collect(Collectors.joining(", og ")))
                .map(status -> futureComplete(progress, status)));
    }

    private ClientFuture futureComplete(BestillingProgress progress, String status) {

        return () -> {
            if (isNotBlank(status)) {
                log.info(status);
            }
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        getPdlIdenter(identer)
                .collectList()
                .flatMapMany(idents -> tagsHendelseslagerConsumer.deleteTags(idents, Arrays.asList(Tags.values())))
                .subscribe(response -> log.info("Slettet fra TagsHendelselager"));
    }

    private String getTagStatus(List<String> identer, List<Tags> tags, TagsOpprettingResponse resultat) {

        return resultat.getStatus().is2xxSuccessful() ?
                format("Lagt til tag(s) %s for ident(er) %s",
                        tags.stream().map(Tags::getBeskrivelse).collect(Collectors.joining(",")),
                        String.join(", ", identer)) :
                resultat.getMessage();
    }

    private String getPublishStatus(List<String> identer, HendelselagerResponse resultat) {

        return resultat.getStatus().is2xxSuccessful() ?
                format("Publish sendt til hendelselager for identer: %s med status: %s %s",
                        String.join(", ", identer), resultat.getStatus(), resultat.getBody()) :
                resultat.getFeilmelding();
    }

    private Flux<String> getPdlIdenter(List<String> identer) {

        return personServiceConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .flatMap(person -> Flux.fromStream(Stream.of(Stream.of(person.getIdent()),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull),
                                person.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull),
                                person.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull),
                                person.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                        .filter(Objects::nonNull),
                                person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                        .filter(Objects::nonNull))
                        .flatMap(Function.identity())));
    }
}
