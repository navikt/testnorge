package no.nav.registre.testnorge.elsam.utils;

import static java.lang.Boolean.TRUE;
import static javax.xml.bind.JAXBContext.newInstance;
import static javax.xml.bind.Marshaller.JAXB_ENCODING;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.helse.eiFellesformat.XMLEIFellesformat;
import no.nav.helse.msgHead.XMLMsgHead;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public final class JAXB {

    private JAXB() {
        throw new IllegalStateException("Utility klasse");
    }

    private static final JAXBContext SYKMELDING;

    static {
        try {
            SYKMELDING = newInstance(XMLEIFellesformat.class, XMLMsgHead.class, XMLHelseOpplysningerArbeidsuforhet.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T unmarshalFellesformat(String melding) {
        try {
            return (T) SYKMELDING.createUnmarshaller()
                    .unmarshal(new StringReader(melding));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static String marshallFellesformat(Object element) {
        try {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = SYKMELDING.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
            marshaller.setProperty(JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(JAXB_FRAGMENT, true);
            marshaller.marshal(element, new StreamResult(writer));
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
