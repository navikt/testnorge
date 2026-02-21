package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.dto.ItemDTO;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetQueueItemCommand implements Callable<ItemDTO> {
    private final WebClient webClient;
    private final String token;
    private final JenkinsCrumb crumb;
    private final Long itemId;

    @Override
    public ItemDTO call() {
        log.info("Henter jobb fra Jenkins med id: {}.", itemId);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/queue/item/{itemId}/api/json").build(itemId))
                .header(crumb.getCrumbRequestField(), crumb.getCrumb())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(ItemDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
