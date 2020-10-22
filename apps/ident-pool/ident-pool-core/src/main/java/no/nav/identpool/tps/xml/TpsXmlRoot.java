package no.nav.identpool.tps.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JacksonXmlRootElement(localName = "tpsPersonData", namespace = "http://www.rtv.no/NamespaceTPS")
class TpsXmlRoot {

    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    @JacksonXmlProperty(localName = "tpsServiceRutine")
    private final TpsServiceRutine tpsServiceRutine;

    String toXml() throws JsonProcessingException {
        return xmlMapper.writeValueAsString(this);
    }
}
