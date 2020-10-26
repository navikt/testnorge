package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "tpsServiceRutine")
public class TpsServiceRoutineHentByFnrRequest extends TpsServiceRoutineHentRequest {

    private String fnr;
}
