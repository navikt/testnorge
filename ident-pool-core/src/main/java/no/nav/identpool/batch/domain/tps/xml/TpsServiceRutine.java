package no.nav.identpool.batch.domain.tps.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
@JacksonXmlRootElement(localName = "tpsServiceRutine")
public abstract class TpsServiceRutine {

    @JsonProperty("serviceRutinenavn")
    private final ServiceRutinenavn serviceRutinenavn;

    @JsonProperty("aksjonsKode")
    private final String aksjonsKode;

    @JsonProperty("aksjonsKode2")
    private final String aksjonsKode2;

    public String toXml() {
        try {
            return new TpsXmlRoot(this).toXml();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Could not convert object to xml");
        }
    }
}
