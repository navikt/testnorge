package no.nav.identpool.batch.tps.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import no.nav.identpool.batch.tps.xml.service.ServiceRutinenavn;

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

    public String getServiceRutinenavn() {
        return serviceRutinenavn.getValue();
    }
}
