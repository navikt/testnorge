package no.nav.pdl.forvalter.meter;

import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTags;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.List;

public class PdlWebClientTagsProvider implements WebClientExchangeTagsProvider {

    private static String stripRequestParanms(Tag uriTag) {

        return uriTag.getValue().replaceFirst("\\?.*", "");
    }

    @Override
    public Iterable<Tag> tags(ClientRequest request, ClientResponse response, Throwable throwable) {

        return List.of(
                WebClientExchangeTags.method(request),
                Tag.of("uri", stripRequestParanms(WebClientExchangeTags.uri(request))),
                WebClientExchangeTags.status(response, throwable),
                WebClientExchangeTags.outcome(response));
    }
}
