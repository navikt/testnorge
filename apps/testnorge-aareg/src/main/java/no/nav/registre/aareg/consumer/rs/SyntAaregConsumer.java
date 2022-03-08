package no.nav.registre.aareg.consumer.rs;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.aareg.consumer.rs.command.PostSyntAaregCommand;
import no.nav.registre.aareg.syntetisering.RsAaregSyntetiseringsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;


@Component
public class SyntAaregConsumer {

    private final int pageSize;
    private final WebClient webClient;

    public SyntAaregConsumer(
            @Value("${aareg.pageSize}") int pageSize,
            @Value("${synt-aareg.rest.api.url}") String aaregUrl
    ) {
        this.pageSize = pageSize;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(aaregUrl)
                .build();
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aareg-syntetisereren" })
    public List<RsAaregSyntetiseringsRequest> getSyntetiserteArbeidsforholdsmeldinger(List<String> identer) {
        List<RsAaregSyntetiseringsRequest> syntetiserteMeldinger = new ArrayList<>();

        if (identer.size() > pageSize) {
            for (var i = 0; i * pageSize < identer.size(); i++) {
                var endIndex = pageSize * (i + 1);
                if (endIndex > identer.size()) {
                    endIndex = identer.size();
                }

                insertSyntetiskeArbeidsforhold(syntetiserteMeldinger, identer.subList(i * pageSize, endIndex));
            }
        } else {
            insertSyntetiskeArbeidsforhold(syntetiserteMeldinger, identer);
        }

        return syntetiserteMeldinger;
    }

    private void insertSyntetiskeArbeidsforhold(
            List<RsAaregSyntetiseringsRequest> syntetiserteMeldinger,
            List<String> fnrs
    ) {
        List<RsAaregSyntetiseringsRequest> response = new PostSyntAaregCommand(fnrs, webClient).call();
        if (!response.isEmpty()) {
            syntetiserteMeldinger.addAll(response);
        }
    }
}

