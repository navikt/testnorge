package no.nav.registre.testnorge.identservice.testdata.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineHentRequest;

@Getter
@Setter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "tpsServiceRutine")
public class TpsHentFnrHistMultiServiceRoutineRequest extends TpsServiceRoutineHentRequest {
    private String antallFnr;

    @JacksonXmlElementWrapper(localName = "nFnr")
    @JacksonXmlProperty(localName = "fnr")
    private String fnr;

}
