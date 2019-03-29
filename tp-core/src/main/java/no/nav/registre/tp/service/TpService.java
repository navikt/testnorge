package no.nav.registre.tp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.tp.consumer.rs.HodejegerenConsumer;
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
@RequiredArgsConstructor
public class TpService {

    private final TForholdYtelseHistorikkRepository tForholdYtelseHistorikkRepository;
    private final TForholdRepository tForholdRepository;
    private final TPersonRepository tPersonRepository;
    private final TYtelseRepository tYtelseRepository;

    private final TpSyntConsumer tpSyntConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;

    public int initializeTpDbForEnvironment(Long id, String env) {
        Set<String> allIdentities = hodejegerenConsumer.getAllIdentities(new SyntetiseringsRequest(id, env, 0));

        List<TPerson> allInDb = (List<TPerson>) tPersonRepository.findAll();

        Set<String> fnrsInDb = allInDb.parallelStream().map(TPerson::getFnrFk).collect(Collectors.toSet());

        Set<String> missing = allIdentities.parallelStream().filter(fnr -> !fnrsInDb.contains(fnr)).collect(Collectors.toSet());

        List<TPerson> created = createPeopleFromStream(missing.parallelStream());

        log.info("Opprettet {} personer i tp", created.size());
        return created.size();
    }

    public void syntetiser(@Valid SyntetiseringsRequest request) {

        List<String> ids = hodejegerenConsumer.getLivingIdentities(request);

        List<TYtelse> ytelser = tpSyntConsumer.getSyntYtelser(ids.size());
        if (ytelser.size() != ids.size()) {
            log.warn("Fikk ikke riktig antall ytelser i forhold til forventet antall. Ytelser: {} Fnrs: {}", ytelser.size(), ids.size());
            log.warn("Fortsetter execution men sløyfer de siste ytelsene");
        }
        if (ids.size() > ytelser.size()) {
            String formated = String.format("Invalid amount of ytelser: %d, fnrs: %d", ytelser.size(), ids.size());
            log.warn(formated);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, formated);
        }
        for (int i = 0; i < ids.size(); i++) {
            createFullRelation(ids.get(i), ytelser.get(i));
        }
    }

    public List<TForhold> getForhold() {
        return (List<TForhold>) tForholdRepository.findAll();
    }

    public List<String> createPeople(List<String> fnrs) {
        List<String> notFound = fnrs.parallelStream().filter(fnr -> tPersonRepository.findByFnrFk(fnr) == null).collect(Collectors.toList());
        List<TPerson> saved = createPeopleFromStream(notFound.parallelStream());
        return saved.parallelStream().map(TPerson::getFnrFk).collect(Collectors.toList());
    }

    private List<TPerson> createPeopleFromStream(Stream<String> stringStream) {
        Timestamp timestamp = Timestamp.from(Instant.now());

        Set<TPerson> toCreate = stringStream.map(fnr -> TPerson.builder()
                .fnrFk(fnr)
                .datoOpprettet(timestamp)
                .datoEndret(timestamp)
                .endretAv("synt")
                .opprettetAv("synt")
                .versjon("1")
                .build())
                .collect(Collectors.toSet());

        return (List<TPerson>) tPersonRepository.saveAll(toCreate);
    }

    private TYtelse saveYtelse(TYtelse ytelse) {
        ytelse.setOpprettetAv("synt");
        ytelse.setEndretAv("synt");
        return tYtelseRepository.save(ytelse);
    }

    private TPerson savePerson(TPerson person) {
        TPerson funnet = tPersonRepository.findByFnrFk(person.getFnrFk());
        if (funnet != null) {
            return funnet;
        }
        return tPersonRepository.save(person);
    }

    private void saveForhold(TPerson person, TYtelse ytelse) {

        String endretAv = "INFOTRYGD";

        if (ytelse.getEndretAv().contains("srvElsam")) {
            endretAv = "TPLEV";
        }

        Timestamp now = Timestamp.from(Instant.now());

        TForhold savedForhold = tForholdRepository.save(TForhold.builder()
                .personId(person.getPersonId())
                .datoBrukFom(ytelse.getDatoBrukFom())
                .datoBrukTom(ytelse.getDatoBrukTom())
                .erGyldig(ytelse.getErGyldig())
                .kSamtykkeSimT("N")
                .kKildeTpT(endretAv)
                .harSimulering("0")
                .tssEksternIdFk("80000470761")
                .versjon(ytelse.getVersjon())
                .datoOpprettet(now)
                .datoEndret(now)
                .endretAv("synt")
                .opprettetAv("synt")
                .datoSamtykkeGitt(new java.sql.Date(Instant.now().toEpochMilli()))
                .harUtlandPensj("N")
                .funkForholdId(ytelse.getFunkYtelseId())
                .build());

        tForholdYtelseHistorikkRepository.save(new TForholdYtelseHistorikk(new HistorikkComposityKey(savedForhold.getForholdId(), ytelse.getYtelseId())));

    }

    private void createFullRelation(String fnr, TYtelse ytelse) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        TPerson tPerson = savePerson(TPerson.builder()
                .fnrFk(fnr)
                .datoOpprettet(timestamp)
                .datoEndret(timestamp)
                .endretAv("synt")
                .versjon(ytelse.getVersjon())
                .opprettetAv("synt")
                .build());
        TYtelse tYtelse = saveYtelse(ytelse);
        saveForhold(tPerson, tYtelse);
    }

}
