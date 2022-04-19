package no.nav.dolly.bestilling.tagshendelseslager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
    private final DollyPersonCache dollyPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!dollyPerson.getTags().isEmpty()) {

            dollyPersonCache.fetchIfEmpty(dollyPerson);
            tagsHendelseslagerConsumer.createTags(Stream.of(List.of(dollyPerson.getHovedperson()),
                                    dollyPerson.getPartnere(), dollyPerson.getForeldre(), dollyPerson.getBarn(),
                                    dollyPerson.getFullmektige(), dollyPerson.getVerger())
                            .flatMap(Collection::stream)
                            .toList(), dollyPerson.getTags())
                    .collectList()
                    .subscribe(response -> log.info("Lagt til tag(s) {} for ident {}",
                            dollyPerson.getTags().stream().map(Enum::name).collect(Collectors.joining(", ")),
                            dollyPerson.getHovedperson()));
        }
    }

    @Override
    public void release(List<String> identer) {

        try {
            pdlPersonConsumer.getPdlPersoner(identer)
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
                    .flatMap(idents -> tagsHendelseslagerConsumer.deleteTags(idents, Arrays.asList(Tags.values())))
                    .subscribe(response -> log.info("Slettet fra TagsHendelselager"));

        } catch (RuntimeException e) {
            log.error("Feilet å slette tags for identer: {}", String.join(", ", identer));
        }
    }
}
