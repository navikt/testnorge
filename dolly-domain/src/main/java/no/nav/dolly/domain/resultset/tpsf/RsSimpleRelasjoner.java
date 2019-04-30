package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsSimpleRelasjoner {

    private RsRelasjon partner;

    private List<RsRelasjon> barn;
}
