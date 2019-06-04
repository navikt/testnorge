package no.nav.registre.sam.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.sam.IdentMedData;
import no.nav.registre.sam.SamSaveInHodejegerenRequest;
import no.nav.registre.sam.SyntetisertSamordningsmelding;
import no.nav.registre.sam.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sam.consumer.rs.SamSyntetisererenConsumer;
import no.nav.registre.sam.database.TPersonRepository;
import no.nav.registre.sam.database.TSamHendelseRepository;
import no.nav.registre.sam.database.TSamMeldingRepository;
import no.nav.registre.sam.database.TSamVedtakRepository;
import no.nav.registre.sam.domain.database.TPerson;
import no.nav.registre.sam.domain.database.TSamHendelse;
import no.nav.registre.sam.domain.database.TSamMelding;
import no.nav.registre.sam.domain.database.TSamVedtak;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;

@Service
@Slf4j
public class SyntetiseringService {

    private static final String SAM_NAME = "sam";

    public static final String ENDRET_OPPRETTET_AV = "Orkestratoren";

    @Autowired
    private SamSyntetisererenConsumer samSyntetisererenConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TPersonRepository tPersonRepository;

    @Autowired
    private TSamHendelseRepository tSamHendelseRepository;

    @Autowired
    private TSamMeldingRepository tSamMeldingRepository;

    @Autowired
    private TSamVedtakRepository tSamVedtakRepository;

    public ResponseEntity opprettOgLagreSyntetiserteSamordningsmeldinger(List<String> identer) {
        List<SyntetisertSamordningsmelding> syntetiserteMeldinger = samSyntetisererenConsumer.hentSammeldingerFromSyntRest(identer.size());
        lagreSyntetiserteMeldinger(syntetiserteMeldinger, identer);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public List<String> finnLevendeIdenter(SyntetiserSamRequest request) {
        return hodejegerenConsumer.finnLevendeIdenter(request);
    }

    public void lagreSyntetiserteMeldinger(List<SyntetisertSamordningsmelding> syntetiserteMeldinger, List<String> identer) {
        HashMap<String, SyntetisertSamordningsmelding> historikkSomSkalLagres = new HashMap<>();
        if (syntetiserteMeldinger.size() != identer.size()) {
            log.warn("Mottok ikke riktig antall syntetiske samordningsmeldinger fra sam-syntetisereren. Antall forespurt: {}, antall mottatt: {}", identer.size(), syntetiserteMeldinger.size());
        }

        for (int i = 0; i < identer.size() && i < syntetiserteMeldinger.size(); i++) {
            TPerson person = tPersonRepository.findByFnrFK(identer.get(i));
            if (person == null) {
                person = tPersonRepository.save(new TPerson(identer.get(i)));
            }
            try {
                tSamHendelseRepository.save(new TSamHendelse(syntetiserteMeldinger.get(i), person));
                TSamVedtak tSamVedtak = tSamVedtakRepository.save(new TSamVedtak(syntetiserteMeldinger.get(i), person));
                tSamMeldingRepository.save(new TSamMelding(syntetiserteMeldinger.get(i), tSamVedtak));
                historikkSomSkalLagres.put(identer.get(i), syntetiserteMeldinger.get(i));
            } catch (ParseException e) {
                log.error("Parse-error ved opprettelse av samordning for ident {}", person.getFnrFK(), e);
            }
        }

        List<IdentMedData> identerMedData = new ArrayList<>(historikkSomSkalLagres.size());
        for (Map.Entry<String, SyntetisertSamordningsmelding> personInfo : historikkSomSkalLagres.entrySet()) {
            identerMedData.add(new IdentMedData(personInfo.getKey(), new ArrayList<>(Arrays.asList(personInfo.getValue()))));
        }
        SamSaveInHodejegerenRequest hodejegerenRequests = new SamSaveInHodejegerenRequest(SAM_NAME, identerMedData);

        List<String> lagredeIdenter = hodejegerenConsumer.saveHistory(hodejegerenRequests);

        if (lagredeIdenter.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (IdentMedData ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(lagredeIdenter);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }
    }
}