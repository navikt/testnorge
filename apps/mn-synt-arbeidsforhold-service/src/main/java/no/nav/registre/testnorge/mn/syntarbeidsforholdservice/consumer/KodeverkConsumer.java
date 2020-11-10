package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GetKodeverkCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.dto.KodeverkDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Kodeverk;

@Component
public class KodeverkConsumer {
    private final WebClient webClient;
    private final String applicationName;

    public KodeverkConsumer(
            @Value("${consumers.kodeverk.url}") String url,
            @Value("${spring.application.name}") String applicationName
    ) {
        this.applicationName = applicationName;
        this.webClient = WebClient
                .builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)
                )
                .baseUrl(url)
                .build();
    }

    public Kodeverk getYrkeKodeverk(){
        KodeverkDTO dto = new GetKodeverkCommand(webClient, "Yrker", LocalDate.now(), applicationName).call();
        return new Kodeverk(dto);
    }

}
