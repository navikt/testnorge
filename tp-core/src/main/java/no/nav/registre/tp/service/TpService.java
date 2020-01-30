package no.nav.registre.tp.service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tp.Forhold;
import no.nav.registre.tp.FulltForhold;
import no.nav.registre.tp.IdentMedData;
import no.nav.registre.tp.Person;
import no.nav.registre.tp.TpSaveInHodejegerenRequest;
import no.nav.registre.tp.Ytelse;
import no.nav.registre.tp.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.tp.consumer.rs.TpSyntConsumer;
import no.nav.registre.tp.database.models.HistorikkComposityKey;
import no.nav.registre.tp.database.models.TForhold;
import no.nav.registre.tp.database.models.TForholdYtelseHistorikk;
import no.nav.registre.tp.database.models.TPerson;
import no.nav.registre.tp.database.models.TYtelse;
import no.nav.registre.tp.database.repository.TForholdRepository;
import no.nav.registre.tp.database.repository.TForholdYtelseHistorikkRepository;
import no.nav.registre.tp.database.repository.TPersonRepository;
import no.nav.registre.tp.database.repository.TYtelseRepository;
import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;

@Slf4j
@Service
public class TpService {

    private static final Integer MIN_AGE = 13;
    private static final String EIER = "synt";

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TForholdYtelseHistorikkRepository tForholdYtelseHistorikkRepository;

    @Autowired
    private TForholdRepository tForholdRepository;

    @Autowired
    private TPersonRepository tPersonRepository;

    @Autowired
    private TYtelseRepository tYtelseRepository;

    @Autowired
    private TpSyntConsumer tpSyntConsumer;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    private static final String TP_NAME = "tp";
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

    public void syntetiser(
            @Valid SyntetiseringsRequest request
    ) {
        var ids = getLivingIdentities(request.getAvspillergruppeId(), request.getMiljoe(), request.getAntallPersoner(), MIN_AGE);

        var ytelser = tpSyntConsumer.getSyntYtelser(ids.size());
        if (ytelser.size() != ids.size()) {
            log.warn("Fikk ikke riktig antall ytelser i forhold til forventet antall. Ytelser: {} Fnrs: {}", ytelser.size(), ids.size());
            log.warn("Fortsetter execution men sløyfer de siste ytelsene");
        }
        if (ids.size() > ytelser.size()) {
            String formated = String.format("Invalid amount of ytelser: %d, fnrs: %d", ytelser.size(), ids.size());
            log.warn(formated);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, formated);
        }

        List<FullSavedForhold> savedForhold = new ArrayList<>(ids.size());

        for (int i = 0; i < ids.size(); i++) {
            savedForhold.add(createFullRelation(ids.get(i), ytelser.get(i)));
        }

        var identerMedData = savedForhold.parallelStream().map(f -> IdentMedData.builder()
                .id(f.tPerson.getFnrFk())
                .data(
                        Collections.singletonList(
                                FulltForhold.builder()
                                        .forhold(
                                                new Forhold.Builder()
                                                        .setDatoBrukFom(f.tForhold.getDatoBrukFom())
                                                        .setDatoBrukTom(f.tForhold.getDatoBrukTom())
                                                        .setDatoEndret(f.tForhold.getDatoEndret())
                                                        .setDatoOpprettet(f.tForhold.getDatoOpprettet())
                                                        .setDatoSamtykkeGitt(f.tForhold.getDatoSamtykkeGitt())
                                                        .setEndretAv(f.tForhold.getEndretAv())
                                                        .setErGyldig(f.tForhold.getErGyldig())
                                                        .setHarUtlandPensj(f.tForhold.getHarUtlandPensj())
                                                        .setKilde(f.tForhold.getKKildeTpT())
                                                        .setTssEksternIdFk(f.tForhold.getTssEksternIdFk())
                                                        .setOpprettetAv(f.tForhold.getOpprettetAv())
                                                        .setVersjon(f.tForhold.getVersjon())
                                                        .build()
                                        )
                                        .person(
                                                new Person.Builder()
                                                        .setDatoEndret(f.tPerson.getDatoEndret())
                                                        .setDatoOpprettet(f.tPerson.getDatoOpprettet())
                                                        .setEndretAv(f.tPerson.getEndretAv())
                                                        .setFnrFk(f.tPerson.getFnrFk())
                                                        .setVersjon(f.tPerson.getVersjon())
                                                        .build()
                                        )
                                        .ytelse(
                                                new Ytelse.Builder()
                                                        .setDatoBrukFom(f.tYtelse.getDatoBrukFom())
                                                        .setDatoBrukTom(f.tYtelse.getDatoBrukTom())
                                                        .setDatoEndret(f.tYtelse.getDatoEndret())
                                                        .setDatoInnmYtelFom(f.tYtelse.getDatoInnmYtelFom())
                                                        .setDatoYtelIverFom(f.tYtelse.getDatoYtelIverFom())
                                                        .setDatoYtelIverTom(f.tYtelse.getDatoYtelIverTom())
                                                        .setDatoOpprettet(f.tYtelse.getDatoOpprettet())
                                                        .setEndretAv(f.tYtelse.getEndretAv())
                                                        .setErGyldig(f.tYtelse.getErGyldig())
                                                        .setKYtelseT(f.tYtelse.getKYtelseT())
                                                        .setMeldingsType(f.tYtelse.getKMeldingT())
                                                        .setOpprettetAv(f.tYtelse.getOpprettetAv())
                                                        .setVersjon(f.tYtelse.getVersjon())
                                                        .build()
                                        )
                                        .build()
                        )
                ).build()
        ).collect(Collectors.toList());

        var tpSaveInHodejegerenRequest = TpSaveInHodejegerenRequest.builder()
                .kilde(TP_NAME)
                .identMedData(identerMedData)
                .build();

        var savedIds = hodejegerenHistorikkConsumer.saveHistory(tpSaveInHodejegerenRequest);

        if (savedIds.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (var ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(savedIds);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }
    }

    public List<TForhold> getForhold() {
        return (List<TForhold>) tForholdRepository.findAll();
    }

    public List<String> createPeople(
            List<String> fnrs
    ) {
        var notFound = fnrs.parallelStream().filter(fnr -> tPersonRepository.findByFnrFk(fnr) == null).collect(Collectors.toList());
        var saved = createPeopleFromStream(notFound.parallelStream());
        return saved.parallelStream().map(TPerson::getFnrFk).collect(Collectors.toList());
    }

    public List<String> filterTpOnFnrs(List<String> fnrs) {
        var partitions = partition(fnrs);
        List<String> personsInTp = new ArrayList<>();
        for (var subList : partitions) {
            var allPersons = tPersonRepository.findAllByFnrFkIn(subList);
            personsInTp.addAll(allPersons.stream().map(TPerson::getFnrFk).collect(Collectors.toList()));
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

    private TYtelse saveYtelse(
            TYtelse ytelse
    ) {
        ytelse.setOpprettetAv(EIER);
        ytelse.setEndretAv(EIER);
        return tYtelseRepository.save(ytelse);
    }

    private TPerson savePerson(
            TPerson person
    ) {
        var funnet = tPersonRepository.findByFnrFk(person.getFnrFk());
        if (funnet != null) {
            return funnet;
        }
        return tPersonRepository.save(person);
    }

    private TForhold saveForhold(
            TPerson person,
            TYtelse ytelse
    ) {
        var endretAv = "INFOTRYGD";

        if (ytelse.getEndretAv().contains("srvElsam")) {
            endretAv = "TPLEV";
        }

        var now = Timestamp.from(Instant.now());

        var forhold = new TForhold();
        forhold.setPersonId(person.getPersonId());
        forhold.setDatoBrukFom(ytelse.getDatoBrukFom());
        forhold.setDatoBrukTom(ytelse.getDatoBrukTom());
        forhold.setErGyldig(ytelse.getErGyldig());
        forhold.setKSamtykkeSimT("N");
        forhold.setKKildeTpT(endretAv);
        forhold.setHarSimulering("0");
        forhold.setTssEksternIdFk("80000470761");
        forhold.setVersjon(ytelse.getVersjon());
        forhold.setDatoOpprettet(now);
        forhold.setDatoEndret(now);
        forhold.setEndretAv(EIER);
        forhold.setOpprettetAv(EIER);
        forhold.setDatoSamtykkeGitt(new Date(Instant.now().toEpochMilli()));
        forhold.setHarUtlandPensj("N");
        forhold.setFunkForholdId(ytelse.getFunkYtelseId());
        var savedForhold = tForholdRepository.save(forhold);

        tForholdYtelseHistorikkRepository.save(new TForholdYtelseHistorikk(new HistorikkComposityKey(savedForhold.getForholdId(), ytelse.getYtelseId())));
        return savedForhold;
    }

    private FullSavedForhold createFullRelation(
            String fnr,
            TYtelse ytelse
    ) {
        var fullSavedForhold = new FullSavedForhold();
        var timestamp = Timestamp.from(Instant.now());

        var tPerson = savePerson(new TPerson(fnr, timestamp, EIER, timestamp, EIER, ytelse.getVersjon()));
        var tYtelse = saveYtelse(ytelse);
        var tForhold = saveForhold(tPerson, tYtelse);

        fullSavedForhold.tPerson = tPerson;
        fullSavedForhold.tForhold = tForhold;
        fullSavedForhold.tYtelse = tYtelse;

        return fullSavedForhold;
    }

    @Timed(value = "tp.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> getLivingIdentities(
            Long id,
            String env,
            int numberOfIdents,
            int minAge
    ) {
        return hodejegerenConsumer.getLevende(id, env, numberOfIdents, minAge);
    }

    @Timed(value = "tp.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> getLivingIdentities(
            Long id
    ) {
        return hodejegerenConsumer.getLevende(id);
    }

    private static class FullSavedForhold {

        TForhold tForhold;
        TPerson tPerson;
        TYtelse tYtelse;
    }
}
