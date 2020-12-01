package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private String xml;
    private TpsServiceRoutineRequest routineRequest;
    private TpsRequestContext context;

}
