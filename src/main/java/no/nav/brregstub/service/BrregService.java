package no.nav.brregstub.service;

import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;
import java.io.IOException;

@Service
public class BrregService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrregService.class);

    public Grunndata hentRoller(String orgnr) {
        ClassPathResource resource = new ClassPathResource("response/HentRollerRespnse.xml");
        try {
            return JAXB.unmarshal(resource.getFile(), Grunndata.class);
        } catch (IOException e) {
            LOGGER.error("Feil ved hentRoller", e);
            throw new RuntimeException("Feil ved hentRoller", e);
        }
    }

    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String password) {
        ClassPathResource resource = new ClassPathResource("response/HentRolleutskrift.xml");
        try {
            return JAXB.unmarshal(resource.getFile(), no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.class);
        } catch (IOException e) {
            LOGGER.error("Feil ved hentRolleutskrift", e);
            throw new RuntimeException("Feil ved hentRolleutskrift", e);
        }
    }
}
