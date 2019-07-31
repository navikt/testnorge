package no.nav.dolly.bestilling.instdata;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.properties.ProvidersProps;

@Getter
@Setter
public class InstdataResponse {

    private Map<String, List<ProvidersProps.Instdata>> identliste;
}
