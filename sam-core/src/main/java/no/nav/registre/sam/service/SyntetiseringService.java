package no.nav.registre.sam.service;

import no.nav.registre.sam.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sam.consumer.rs.SamSyntetisererenConsumer;
import no.nav.registre.sam.database.TPersonRepository;
import no.nav.registre.sam.domain.SyntetisertSamObject;
import no.nav.registre.sam.domain.database.DBObject;
import no.nav.registre.sam.domain.database.TPerson;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.sam.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyntetiseringService {

    @Autowired
    SamSyntetisererenConsumer samSyntRestConsumer;

    @Autowired
    HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    TPersonRepository tPersonRepository;

    public ResponseEntity finnSyntetiserteMeldinger(List<String> identer) {
        try{
            List<SyntetisertSamObject> syntetiserteMeldinger = samSyntRestConsumer.hentSammeldingerFromSyntRest(identer.size());
            return ResponseEntity.ok().body(syntetiserteMeldinger);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public List<String> finnLevendeIdenter(SyntetiserSamRequest request) {
        return hodejegerenConsumer.finnLevendeIdenter(request);
    }

    public List<DBObject> createDatabaseEntities(List<SyntetisertSamObject> syntetiserteMeldinger, List<String> identer){
        ArrayList<DBObject> dbObjects = new ArrayList<>();
        if (syntetiserteMeldinger.size() != identer.size()){
            throw new IndexOutOfBoundsException("Feil! Antall identer og antall syntetiske meldinger stemmer ikke overens");
        }
        for (int i = 0; i < identer.size(); i++){
            TPerson tPerson = new TPerson(identer.get(i));
        }
        return null;
    }
}