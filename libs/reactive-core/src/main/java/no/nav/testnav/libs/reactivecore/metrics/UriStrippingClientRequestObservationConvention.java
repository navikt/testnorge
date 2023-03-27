package no.nav.testnav.libs.reactivecore.metrics;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.observation.GlobalObservationConvention;
import org.springframework.web.reactive.function.client.ClientRequestObservationContext;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;

/**
 * Custom {@code ClientRequestObservationConvention} that strips the query parameters from the URI.
 */
@NonNullApi
public class UriStrippingClientRequestObservationConvention
        extends DefaultClientRequestObservationConvention
        implements GlobalObservationConvention<ClientRequestObservationContext> {

    @Override
    protected KeyValue uri(ClientRequestObservationContext context) {
        return KeyValue.of(
                "uri",
                super
                        .uri(context)
                        .getValue()
                        .replaceFirst("\\?.*", "")
        );
    }

}
