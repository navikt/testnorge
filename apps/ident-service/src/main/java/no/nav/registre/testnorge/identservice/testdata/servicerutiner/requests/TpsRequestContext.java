package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TpsRequestContext {

    private User user;
    private String environment;
}
