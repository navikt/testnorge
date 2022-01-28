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
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagsHendelseslagerClient implements ClientRegister {

    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final DollyPersonCache dollyPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!dollyPerson.getTags().isEmpty()) {

            var pdlpersonBolk = pdlPersonConsumer.getPdlPersoner(List.of(dollyPerson.getHovedperson())).getData().getHentPersonBolk().stream()
                    .map(PdlPersonBolk.PersonBolk::getPerson)
                    .toList();

            tagsHendelseslagerConsumer.createTags(Stream.of(
                                    List.of(dollyPerson.getHovedperson()),
                                    pdlpersonBolk.stream()
                                            .flatMap(person -> person.getSivilstand().stream()
                                                    .map(PdlPerson.Sivilstand::getRelatertVedSivilstand))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(personDTO -> personDTO.getForelderBarnRelasjon().stream()
                                                    .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(person -> person.getFullmakt().stream()
                                                    .map(FullmaktDTO::getMotpartsPersonident))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(person -> person.getVergemaalEllerFremtidsfullmakt().stream()
                                                    .map(PdlPerson.Vergemaal::getVergeEllerFullmektig))
                                            .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                            .toList())
                            .flatMap(Collection::stream)
                            .filter(StringUtils::isNotBlank)
                            .distinct()
                            .toList(),
                    dollyPerson.getTags());
        }

        if (progress.isPdl()) {
            dollyPersonCache.fetchIfEmpty(dollyPerson);
            tagsHendelseslagerConsumer.createTags(Stream.of(List.of(dollyPerson.getHovedperson()),
                            dollyPerson.getPartnere(), dollyPerson.getForeldre(), dollyPerson.getBarn(),
                            dollyPerson.getFullmektige(), dollyPerson.getVerger())
                    .flatMap(Collection::stream)
                    .toList(), List.of(Tags.DOLLY));
            var status = tagsHendelseslagerConsumer.publish(List.of(dollyPerson.getHovedperson()));
            log.info("Person med ident {} sendt fra hendelselager med status {}", dollyPerson.getHovedperson(), status);
        }
    }

    @Override
    public void release(List<String> identer) {

        tagsHendelseslagerConsumer.deleteTags(identer, Arrays.stream(Tags.values()).toList());
    }
}
