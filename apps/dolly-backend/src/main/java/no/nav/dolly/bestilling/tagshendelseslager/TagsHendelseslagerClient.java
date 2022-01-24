package no.nav.dolly.bestilling.tagshendelseslager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagsHendelseslagerClient implements ClientRegister {

    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getTags().isEmpty()) {
            tagsHendelseslagerConsumer.createTags(List.of(dollyPerson.getHovedperson()), bestilling.getTags());
        }

        if (progress.isPdl()) {
            tagsHendelseslagerConsumer.createTags(List.of(dollyPerson.getHovedperson()), List.of("Dolly"));
            var status = tagsHendelseslagerConsumer.publish(List.of(dollyPerson.getHovedperson()));
            log.info("Person med ident {} sendt fra hendelselager med status {}", dollyPerson.getHovedperson(), status);
        }
    }

    @Override
    public void release(List<String> identer) {

        tagsHendelseslagerConsumer.deleteTags(identer);
    }
}
