package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.dto.responses.ItemDto;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OrganisasjonBestillingCommand implements Callable<ItemDto[]> {

    private static final String STATUS_URL = "/api/v1/order/{uuid}/items";

    private final WebClient webClient;
    private final String uuid;
    private final String token;

    @Override
    public ItemDto[] call()  {

        return webClient.get()
                .uri(STATUS_URL.replace("{uuid}", uuid))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ItemDto[].class)
                .block();
    }
}
