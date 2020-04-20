package no.nav.brregstub.service;

import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.InputStream;

@Service
public class BrregService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrregService.class);

    public Grunndata hentRoller(String orgnr) {
        InputStream in = this.getClass().getResourceAsStream("/response/HentRollerResponse.xml");
        return JAXB.unmarshal(in, Grunndata.class);
    }

    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String requestId) {
        InputStream in = this.getClass().getResourceAsStream("/response/HentRolleutskriftResponse.xml");
        return JAXB.unmarshal(in, no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.class);
    }
}
