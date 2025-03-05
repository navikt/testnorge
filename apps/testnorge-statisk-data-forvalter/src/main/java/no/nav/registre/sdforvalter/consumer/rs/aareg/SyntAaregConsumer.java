package no.nav.registre.sdforvalter.consumer.rs.aareg;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.sdforvalter.consumer.rs.aareg.command.PostSyntAaregCommand;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsAaregSyntetiseringsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;


@Component
public class SyntAaregConsumer {

    private final int pageSize;
    private final WebClient webClient;

    public SyntAaregConsumer(
            WebClient webClient,
            @Value("${aareg.pageSize}") int pageSize,
            @Value("${consumers.synthdata-aareg.url}") String syntUrl
    ) {
        this.pageSize = pageSize;
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntUrl)
                .build();
    }

    @Timed(value = "aareg.resource.latency", extraTags = {"operation", "aareg-syntetisereren"})
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
        if (nonNull(response) && !response.isEmpty()) {
            syntetiserteMeldinger.addAll(response);
        }
    }
}

