package no.nav.registre.sam.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

import no.nav.registre.sam.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sam.consumer.rs.SamSyntetisererenConsumer;
import no.nav.registre.sam.database.TPersonRepository;
import no.nav.registre.sam.database.TSamHendelseRepository;
import no.nav.registre.sam.database.TSamMeldingRepository;
import no.nav.registre.sam.database.TSamVedtakRepository;
import no.nav.registre.sam.domain.SyntetisertSamObject;
import no.nav.registre.sam.domain.database.TPerson;
import no.nav.registre.sam.domain.database.TSamHendelse;
import no.nav.registre.sam.domain.database.TSamMelding;
import no.nav.registre.sam.domain.database.TSamVedtak;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;

@Service
@Slf4j
public class SyntetiseringService {

    public static final String ENDRET_OPPRETTET_AV = "Orkestratoren";

    @Autowired
    private SamSyntetisererenConsumer samSyntRestConsumer;

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
        List<SyntetisertSamObject> syntetiserteMeldinger = samSyntRestConsumer.hentSammeldingerFromSyntRest(identer.size());
        lagreSyntetiserteMeldinger(syntetiserteMeldinger, identer);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public List<String> finnLevendeIdenter(SyntetiserSamRequest request) {
        return hodejegerenConsumer.finnLevendeIdenter(request);
    }

    public void lagreSyntetiserteMeldinger(List<SyntetisertSamObject> syntetiserteMeldinger, List<String> identer) {
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
            } catch (ParseException e) {
                log.error("Parse-error ved opprettelse av samordning for ident {}", person.getFnrFK(), e);
            }
        }
    }
}