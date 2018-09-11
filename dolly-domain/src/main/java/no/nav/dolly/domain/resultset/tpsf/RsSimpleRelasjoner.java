package no.nav.dolly.domain.resultset.tpsf;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RsSimpleRelasjoner {

    private RsSimpleDollyRequest partner;

    private List<RsSimpleDollyRequest> barn;
}
