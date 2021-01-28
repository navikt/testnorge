package no.nav.registre.testnorge.endringsmeldingservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.command.SendDoedsmeldingCommand;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.command.SendFoedselsmeldingCommand;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsDoedsmeldingDTO;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsFoedselsmeldingDTO;
import no.nav.registre.testnorge.libs.analysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.DoedsmeldingDTO;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;

@Component
@DependencyOn(
        name = "tps-forvalteren",
        namespace = "default",
        cluster = "dev-fss"
)
public class TpsForvalterConsumer {
    private final WebClient webClient;

    public TpsForvalterConsumer(@Value("consumers.tpsforvalter.url") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public void sendFoedselsmelding(FoedselsmeldingDTO dto, Set<String> miljoer) {
        new SendFoedselsmeldingCommand(webClient, new TpsFoedselsmeldingDTO(dto, miljoer)).run();
    }

    public void sendDoedsmelding(DoedsmeldingDTO dto, Set<String> miljoer) {
        new SendDoedsmeldingCommand(webClient, new TpsDoedsmeldingDTO(dto, miljoer)).run();
    }
}
