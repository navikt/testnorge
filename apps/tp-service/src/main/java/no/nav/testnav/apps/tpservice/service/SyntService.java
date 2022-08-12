package no.nav.testnav.apps.tpservice.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.apps.tpservice.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.testnav.apps.tpservice.consumer.rs.TpSyntConsumer;
import no.nav.testnav.apps.tpservice.database.models.HistorikkComposityKey;
import no.nav.testnav.apps.tpservice.database.models.TForholdYtelseHistorikk;
import no.nav.testnav.apps.tpservice.database.models.TPerson;
import no.nav.testnav.apps.tpservice.database.models.TYtelse;
import no.nav.testnav.apps.tpservice.database.models.TForhold;
import no.nav.testnav.apps.tpservice.database.repository.TForholdRepository;
import no.nav.testnav.apps.tpservice.database.repository.TForholdYtelseHistorikkRepository;
import no.nav.testnav.apps.tpservice.database.repository.TPersonRepository;
import no.nav.testnav.apps.tpservice.database.repository.TYtelseRepository;
import no.nav.testnav.apps.tpservice.domain.Forhold;
import no.nav.testnav.apps.tpservice.domain.IdentMedData;
import no.nav.testnav.apps.tpservice.domain.Person;
import no.nav.testnav.apps.tpservice.domain.TpSaveInHodejegerenRequest;
import no.nav.testnav.apps.tpservice.domain.Ytelse;
import no.nav.testnav.apps.tpservice.domain.FulltForhold;
import no.nav.testnav.apps.tpservice.provider.rs.request.SyntetiseringsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static no.nav.testnav.apps.tpservice.service.TpService.EIER;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntService {

    private final TForholdYtelseHistorikkRepository tForholdYtelseHistorikkRepository;
    private final TYtelseRepository tYtelseRepository;
    private final TForholdRepository tForholdRepository;
    private final TPersonRepository tPersonRepository;
    private final TpSyntConsumer tpSyntConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    private static final Integer MIN_AGE = 13;
    private static final String TP_NAME = "tp";

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

        List<SyntService.FullSavedForhold> savedForhold = new ArrayList<>(ids.size());

        for (int i = 0; i < ids.size(); i++) {
            savedForhold.add(createFullRelation(ids.get(i), ytelser.get(i)));
        }

        var identerMedData = savedForhold.parallelStream()
                .map(f -> IdentMedData.builder()
                        .id(f.tPerson.getFnrFk())
                        .data(Collections.singletonList(convertToFulltForhold(f)))
                        .build()
                ).toList();

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

    private FulltForhold convertToFulltForhold(FullSavedForhold f) {
        return FulltForhold.builder()
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
                .build();
    }


    private SyntService.FullSavedForhold createFullRelation(
            String fnr,
            TYtelse ytelse
    ) {
        var fullSavedForhold = new SyntService.FullSavedForhold();
        var timestamp = Timestamp.from(Instant.now());

        var tPerson = savePerson(new TPerson(fnr, timestamp, EIER, timestamp, EIER, ytelse.getVersjon()));
        var tYtelse = saveYtelse(ytelse);
        var tForhold = saveForhold(tPerson, tYtelse);

        fullSavedForhold.tPerson = tPerson;
        fullSavedForhold.tForhold = tForhold;
        fullSavedForhold.tYtelse = tYtelse;

        return fullSavedForhold;
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> getLivingIdentities(
            Long id,
            String env,
            int numberOfIdents,
            int minAge
    ) {
        return hodejegerenConsumer.getLevende(id, env, numberOfIdents, minAge);
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

    private static class FullSavedForhold {

        TForhold tForhold;
        TPerson tPerson;
        TYtelse tYtelse;
    }

    public List<TForhold> getForhold() {
        return (List<TForhold>) tForholdRepository.findAll();
    }

}
