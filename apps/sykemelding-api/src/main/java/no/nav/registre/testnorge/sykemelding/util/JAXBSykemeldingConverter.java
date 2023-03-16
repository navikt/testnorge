package no.nav.registre.testnorge.sykemelding.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.helse.eiFellesformat.XMLEIFellesformat;
import no.nav.helse.msgHead.XMLMsgHead;
import no.nav.registre.testnorge.sykemelding.exception.JAXBSykemeldingConverterException;

import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

import static jakarta.xml.bind.JAXBContext.newInstance;
import static jakarta.xml.bind.Marshaller.JAXB_ENCODING;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static jakarta.xml.bind.Marshaller.JAXB_FRAGMENT;
import static java.lang.Boolean.TRUE;

public class JAXBSykemeldingConverter {

    private static JAXBSykemeldingConverter instance;

    private final JAXBContext context;

    private JAXBSykemeldingConverter() throws JAXBException {
        context = newInstance(XMLEIFellesformat.class, XMLMsgHead.class, XMLHelseOpplysningerArbeidsuforhet.class);
    }

    public XMLEIFellesformat convertToXMLEIFellesformat(String xml) {
        try {
            return (XMLEIFellesformat) context.createUnmarshaller().unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new JAXBSykemeldingConverterException("Klarer ikke å opprette XMLEIFellesformat fra xml", e);
        }
    }

    public String convertToXml(XMLEIFellesformat element) {
        try {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
            marshaller.setProperty(JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(JAXB_FRAGMENT, true);
            marshaller.marshal(element, new StreamResult(writer));
            return writer.toString();
        } catch (JAXBException e) {
            throw new JAXBSykemeldingConverterException("Klarer ikke å konvertere sykemelding til xml", e);
        }
    }

    public static JAXBSykemeldingConverter getInstance() {
        if (instance == null) {
            try {
                instance = new JAXBSykemeldingConverter();
            } catch (JAXBException e) {
                throw new JAXBSykemeldingConverterException("Klarer ikke å opprette sykemelding converter", e);
            }
        }
        return instance;
    }
}
