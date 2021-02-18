package no.nav.identpool.tps.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
@JacksonXmlRootElement(localName = "tpsServiceRutine")
public abstract class TpsServiceRutine {

    @JacksonXmlProperty(localName = "serviceRutinenavn")
    private final ServiceRutinenavn serviceRutinenavn;

    @JacksonXmlProperty(localName = "aksjonsKode")
    private final String aksjonsKode;

    @JacksonXmlProperty(localName = "aksjonsKode2")
    private final String aksjonsKode2;

    public String toXml() {
        try {
            return new TpsXmlRoot(this).toXml();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
}
