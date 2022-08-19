package no.nav.testnav.apps.tpservice.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpservice.consumer.HodejegerenConsumer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.testnav.apps.tpservice.database.models.TPerson;
import no.nav.testnav.apps.tpservice.database.repository.TForholdRepository;
import no.nav.testnav.apps.tpservice.database.repository.TPersonRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpService {

    public static final String EIER = "synt";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final TForholdRepository tForholdRepository;

    private final TPersonRepository tPersonRepository;

    private static final int PARTITION_SIZE = 500;

    public int initializeTpDbForEnvironment(
            Long id
    ) {
        var allIdentities = getLivingIdentities(id);
        var allInDb = (List<TPerson>) tPersonRepository.findAll();
        var fnrsInDb = allInDb.parallelStream().map(TPerson::getFnrFk).collect(Collectors.toSet());
        var missing = allIdentities.parallelStream().filter(fnr -> !fnrsInDb.contains(fnr)).collect(Collectors.toSet());
        var created = createPeopleFromStream(missing.parallelStream());

        log.info("Opprettet {} personer i tp", created.size());
        return created.size();
    }

    public List<String> createPeople(
            List<String> fnrs
    ) {
        var notFound = fnrs.parallelStream().filter(fnr -> tPersonRepository.findByFnrFk(fnr) == null).toList();
        var saved = createPeopleFromStream(notFound.parallelStream());
        return saved.parallelStream().map(TPerson::getFnrFk).toList();
    }

    public List<String> filterTpOnFnrs(List<String> fnrs) {
        var partitions = partition(fnrs);
        List<String> personsInTp = new ArrayList<>();
        for (var subList : partitions) {
            var allPersons = tPersonRepository.findAllByFnrFkIn(subList);
            personsInTp.addAll(allPersons.stream().map(TPerson::getFnrFk).toList());
        }
        return personsInTp;
    }

    @Transactional
    public List<String> removeFnrsFromTp(List<String> fnrs) {
        List<String> removedFnrs = new ArrayList<>();
        for (String fnr : fnrs) {
            var tPerson = tPersonRepository.findByFnrFk(fnr);
            if (tPerson != null) {
                var tForhold = tForholdRepository.findByPersonId(tPerson.getPersonId());

                if (tForhold == null) {
                    tPersonRepository.delete(tPerson);
                    removedFnrs.add(fnr);
                }
            }
        }
        return removedFnrs;
    }

    private List<List<String>> partition(List<String> list) {
        int size = list.size();

        int numberOfPartitions = size / PARTITION_SIZE;
        if (size % PARTITION_SIZE != 0) {
            numberOfPartitions++;
        }

        List<List<String>> partitions = new ArrayList<>(numberOfPartitions);
        for (int i = 0; i < numberOfPartitions; i++) {
            int fromIndex = i * PARTITION_SIZE;
            int toIndex = (i * PARTITION_SIZE + PARTITION_SIZE < size) ? (i * PARTITION_SIZE + PARTITION_SIZE) : size;

            partitions.add(new ArrayList<>(list.subList(fromIndex, toIndex)));
        }

        return partitions;
    }

    private List<TPerson> createPeopleFromStream(
            Stream<String> stringStream
    ) {
        var timestamp = Timestamp.from(Instant.now());

        var toCreate = stringStream.map(fnr -> new TPerson(fnr, timestamp, EIER, timestamp, EIER, "1"))
                .collect(Collectors.toSet());

        return (List<TPerson>) tPersonRepository.saveAll(toCreate);
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> getLivingIdentities(
            Long id
    ) {
        return hodejegerenConsumer.getLevende(id);
    }

}
