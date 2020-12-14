package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.dto.ItemDTO;
import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;

@RequiredArgsConstructor
public class GetQueueItemCommand implements Callable<ItemDTO> {
    private final WebClient webClient;
    private final JenkinsCrumb crumb;
    private final Long itemId;

    @Override
    public ItemDTO call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/queue/item/{itemId}/api/json").build(itemId))
                .header(crumb.getCrumbRequestField(), crumb.getCrumb())
                .retrieve()
                .bodyToMono(ItemDTO.class)
                .block();
    }
}
