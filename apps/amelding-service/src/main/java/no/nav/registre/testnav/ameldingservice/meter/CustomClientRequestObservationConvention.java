package no.nav.registre.testnav.ameldingservice.meter;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.observation.GlobalObservationConvention;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequestObservationContext;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;

@Component
@NonNullApi
public class CustomClientRequestObservationConvention
        extends DefaultClientRequestObservationConvention
        implements GlobalObservationConvention<ClientRequestObservationContext> {

    @Override
    public KeyValues getLowCardinalityKeyValues(ClientRequestObservationContext context) {
        return super
                .getLowCardinalityKeyValues(context)
                .and(method(context))
                .and(uri(context))
                .and(status(context))
                .and(outcome(context));
    }

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
