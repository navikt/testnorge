package no.nav.identpool.ident.ajourhold.tps.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.identpool.ident.ajourhold.tps.xml.service.Personopplysning;

@RunWith(MockitoJUnitRunner.class)
public class TpsMeldingerTest {

    private final static String personopplysningXml = "<?xml version='1.0' encoding='UTF-8'?><tpsPersonData><tpsServiceRutine><serviceRutinenavn>FS03-FDNUMMER-PERSDATA-O</serviceRutinenavn><aksjonsKode>F</aksjonsKode><aksjonsKode2>0</aksjonsKode2><fnr>%s</fnr></tpsServiceRutine></tpsPersonData>";

    @Test
    public void PersonopplysningTest() {
        String fnr = "12345678910";
        String xml = new Personopplysning(fnr).toXml();
        assertThat(String.format(personopplysningXml, fnr), is(xml));
    }
}
