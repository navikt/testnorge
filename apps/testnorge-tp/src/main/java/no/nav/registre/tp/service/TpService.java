package no.nav.registre.tp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.consumer.HodejegerenConsumer;
import no.nav.registre.tp.consumer.TpConsumer;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpService {

    private static final String EIER = "synt";

    private final HodejegerenConsumer hodejegerenConsumer;

    private final TpConsumer tpConsumer;

    public int initializeTpDbForEnvironment(
            Long id
    ) {
        var allIdentities = getLivingIdentities(id);
        var created = tpConsumer.createMissingPersons(allIdentities);

        log.info("Opprettet {} personer i tp", created.size());
        return created.size();
    }

    public List<String> createPeople(List<String> fnrs) {
        return tpConsumer.createMissingPersons(fnrs);
    }

    public List<String> filterTpOnFnrs(List<String> fnrs) {
        return tpConsumer.findExistingPersons(fnrs);
    }

    public List<String> removeFnrsFromTp(List<String> fnrs) {
        return tpConsumer.removePersons(fnrs);
    }

    private List<String> getLivingIdentities(
            Long id
    ) {
        return hodejegerenConsumer.getLevende(id);
    }
}
