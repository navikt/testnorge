package no.nav.brregstub.service;

import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;

@Service
public class BrregService {

    public Grunndata hentRoller(String orgnummer) {
        var in = this.getClass().getResourceAsStream("/response/HentRollerResponse.xml");
        var grunndata = JAXB.unmarshal(in, Grunndata.class);
        grunndata.getResponseHeader().setOrgnr(Integer.valueOf(orgnummer));
        return grunndata;
    }

    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String requestId) {
        var in = this.getClass().getResourceAsStream("/response/HentRolleutskriftResponse.xml");

        var grunndata = JAXB.unmarshal(in, no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.class);
        grunndata.getResponseHeader().setFodselsnr(requestId);
        return grunndata;
    }
}
