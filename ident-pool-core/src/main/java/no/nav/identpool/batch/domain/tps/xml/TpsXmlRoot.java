package no.nav.identpool.batch.domain.tps.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JacksonXmlRootElement(localName = "tpsPersonData")
class TpsXmlRoot {

    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    @JsonProperty("tpsServiceRutine")
    private final TpsServiceRutine tpsServiceRutine;

    String toXml() throws JsonProcessingException {
        return xmlMapper.writeValueAsString(this);
    }
}
