package no.nav.brregstub.service;

import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;
import java.io.IOException;

@Service
public class BrregService {

    public Grunndata hentRoller(String orgnr) {
        ClassPathResource resource = new ClassPathResource("response/HentRollerRespnse.xml");
        try {
            return JAXB.unmarshal(resource.getFile(), Grunndata.class);
        } catch (IOException e) {
            throw new RuntimeException("Feil ved hentRoller", e);
        }
    }

    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String password) {
        ClassPathResource resource = new ClassPathResource("response/HentRolleutskrift.xml");
        try {
            return JAXB.unmarshal(resource.getFile(), no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.class);
        } catch (IOException e) {
            throw new RuntimeException("Feil ved hentRolleutskrift", e);
        }
    }
}
