package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class TpsParameter {

    private String name;
    private TpsParameterType type;
    private String use;
    private List<?> values;

}
