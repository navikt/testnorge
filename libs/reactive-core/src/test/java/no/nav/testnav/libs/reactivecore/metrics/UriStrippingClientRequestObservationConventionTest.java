package no.nav.testnav.libs.reactivecore.metrics;

import io.micrometer.common.KeyValue;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientRequestObservationContext;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.HttpMethod.GET;

class UriStrippingClientRequestObservationConventionTest {

    private static final String HOST = "nowhere.net";
    private static final String BASE_URI = "https://" + HOST + ":8080";
    private static final String PATH_ONLY = "/some/path";
    private static final String PATH_AND_PARAMETERS = PATH_ONLY + "?some=param";
    private static final String PATH_AND_PARAMETERS_AND_TEMPLATE = PATH_AND_PARAMETERS + "&{?someTemplateValue}";

    @Test
    void testUriStripping()
            throws URISyntaxException {

        var context = new ClientRequestObservationContext();
        context.setUriTemplate(BASE_URI + PATH_AND_PARAMETERS_AND_TEMPLATE);
        context.setRequest(
                ClientRequest
                        .create(GET, new URI(BASE_URI + PATH_AND_PARAMETERS))
                        .build()
        );
        context.setResponse(
                ClientResponse
                        .create(HttpStatusCode.valueOf(200))
                        .build()
        );

        var keyValuesFromDefault = new DefaultClientRequestObservationConvention().getLowCardinalityKeyValues(context);
        var keyValuesFromCustom = new UriStrippingClientRequestObservationConvention().getLowCardinalityKeyValues(context);

        assertThat(keyValuesFromDefault)
                .contains(KeyValue.of("client.name", HOST))
                .contains(KeyValue.of("exception", "none"))
                .contains(KeyValue.of("method", "GET"))
                .contains(KeyValue.of("outcome", "SUCCESS"))
                .contains(KeyValue.of("status", "200"))
                .contains(KeyValue.of("uri", PATH_AND_PARAMETERS_AND_TEMPLATE));

        assertThat(keyValuesFromCustom)
                .contains(KeyValue.of("client.name", HOST))
                .contains(KeyValue.of("exception", "none"))
                .contains(KeyValue.of("method", "GET"))
                .contains(KeyValue.of("outcome", "SUCCESS"))
                .contains(KeyValue.of("status", "200"))
                .contains(KeyValue.of("uri", PATH_ONLY));

    }

}
