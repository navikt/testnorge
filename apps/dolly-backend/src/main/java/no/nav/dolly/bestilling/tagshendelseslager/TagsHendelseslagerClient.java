package no.nav.dolly.bestilling.tagshendelseslager;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.consumer.pdlperson.dto.PdlBolkResponse;
import no.nav.dolly.consumer.pdlperson.dto.PdlPersonDTO;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
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

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!dollyPerson.getTags().isEmpty()) {


            var pdlpersonBolk = pdlPersonConsumer.getPdlPersoner(List.of(dollyPerson.getHovedperson())).getData().getHentPersonBolk().stream()
                    .map(PdlBolkResponse.BolkPerson::getPerson)
                    .toList();

            log.info("KAPPLAH - Bestilling: {}", Json.pretty(bestilling));
            log.info("KAPPLAH - personBolk: {}", Json.pretty(pdlpersonBolk));
            log.info("KAPPLAH - dollyperson: {}", Json.pretty(dollyPerson));

            tagsHendelseslagerConsumer.createTags(Stream.of(
                                    List.of(dollyPerson.getHovedperson()),
                                    pdlpersonBolk.stream()
                                            .flatMap(personDTO -> personDTO.getSivilstand().stream()
                                                    .map(SivilstandDTO::getRelatertVedSivilstand))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(personDTO -> personDTO.getForelderBarnRelasjon().stream()
                                                    .map(PdlPersonDTO.ForelderBarnRelasjon::getRelatertPersonsIdent))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(personDTO -> personDTO.getSikkerhetstiltak().stream()
                                                    .map(SikkerhetstiltakDTO::getKontaktperson)
                                                    .map(SikkerhetstiltakDTO.Kontaktperson::getPersonident))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(personDTO -> personDTO.getFullmakt().stream()
                                                    .map(FullmaktDTO::getMotpartsPersonident))
                                            .toList(),
                                    pdlpersonBolk.stream()
                                            .flatMap(personDTO -> personDTO.getVergemaal().stream()
                                                    .map(VergemaalDTO::getVergeIdent))
                                            .toList())
                            .flatMap(Collection::stream)
                            .filter(StringUtils::isNotBlank)
                            .distinct()
                            .toList(),
                    dollyPerson.getTags());
        }

        if (progress.isPdl()) {
            tagsHendelseslagerConsumer.createTags(List.of(dollyPerson.getHovedperson()), List.of(Tags.DOLLY));
            var status = tagsHendelseslagerConsumer.publish(List.of(dollyPerson.getHovedperson()));
            log.info("Person med ident {} sendt fra hendelselager med status {}", dollyPerson.getHovedperson(), status);
        }
    }

    @Override
    public void release(List<String> identer) {

        tagsHendelseslagerConsumer.deleteTags(identer, Arrays.stream(Tags.values()).toList());
    }
}
