package no.nav.registre.sam.service;

import no.nav.registre.sam.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sam.consumer.rs.SamSyntetisererenConsumer;
import no.nav.registre.sam.database.TPersonRepository;
import no.nav.registre.sam.database.TSamHendelseRepository;
import no.nav.registre.sam.database.TSamMeldingRepository;
import no.nav.registre.sam.database.TSamVedtakRepository;
import no.nav.registre.sam.domain.SyntetisertSamObject;
import no.nav.registre.sam.domain.database.*;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyntetiseringService {

    @Autowired
    SamSyntetisererenConsumer samSyntRestConsumer;

    @Autowired
    HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    TPersonRepository tPersonRepository;

    @Autowired
    TSamHendelseRepository tSamHendelseRepository;

    @Autowired
    TSamMeldingRepository tSamMeldingRepository;

    @Autowired
    TSamVedtakRepository tSamVedtakRepository;

    public ResponseEntity finnSyntetiserteMeldinger(List<String> identer) {
        try{
            List<SyntetisertSamObject> syntetiserteMeldinger = samSyntRestConsumer.hentSammeldingerFromSyntRest(identer.size());
            try {
                lagreSyntetiserteMeldinger(syntetiserteMeldinger, identer);
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (Exception e) {
                System.out.println(e.toString());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<String> finnLevendeIdenter(SyntetiserSamRequest request) {
        return hodejegerenConsumer.finnLevendeIdenter(request);
    }

    public void lagreSyntetiserteMeldinger(List<SyntetisertSamObject> syntetiserteMeldinger, List<String> identer){
        if (syntetiserteMeldinger.size() != identer.size()){
            throw new IndexOutOfBoundsException("Feil! Antall identer og antall syntetiske meldinger stemmer ikke overens");
        }
        for (int i = 0; i < identer.size(); i++){
            TPerson person = tPersonRepository.findByFnrFK(identer.get(i));
            if (person == null){
                person = tPersonRepository.save(new TPerson(identer.get(i)));
            }
            tSamHendelseRepository.save(new TSamHendelse(syntetiserteMeldinger.get(i), person));
            TSamVedtak tSamVedtak = tSamVedtakRepository.save(new TSamVedtak(syntetiserteMeldinger.get(i), person));
            tSamMeldingRepository.save(new TSamMelding(syntetiserteMeldinger.get(i), tSamVedtak));
        }
    }
}