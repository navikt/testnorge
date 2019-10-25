package no.nav.registre.aareg.consumer.ws;

import static java.util.Arrays.asList;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.fasit.FasitResourceWithUnmappedProperties;

@Component
@RequiredArgsConstructor
public class AaregBehandleArbeidsforholdFasitConsumer {

    private static final String BASE_URL = "BaseUrl";
    private static final String BEHANDLE_ARBEIDFORHOLD_ALIAS = "virksomhet:BehandleArbeidsforhold_v1";

    private static final String BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL = "/aareg-core/BehandleArbeidsforholdService/v1";
    private static final String FAGSYSTEM = "fss";

    private final FasitApiConsumer fasitApiConsumer;

    public Map<String, String> fetchWsUrlsAllEnvironments() {

        FasitResourceWithUnmappedProperties[] fasitResources = fasitApiConsumer.fetchResources(BEHANDLE_ARBEIDFORHOLD_ALIAS, BASE_URL);

        return Arrays.stream(fasitResources)
                .filter(resource -> FAGSYSTEM.equals(resource.getScope().getZone()))
                .collect(Collectors.toMap(
                        resource -> resource.getScope().getEnvironment(),
                        resource -> ((String) ((Map) resource.getProperties()).get("url"))
                                .contains("/aareg-services/BehandleArbeidsforholdService/v1") ?
                                ((String) ((Map) resource.getProperties()).get("url")) :
                                ((Map) resource.getProperties()).get("url") + BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL));
    }
}
