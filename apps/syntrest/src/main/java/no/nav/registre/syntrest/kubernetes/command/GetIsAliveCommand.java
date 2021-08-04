package no.nav.registre.syntrest.kubernetes.command;

import java.util.concurrent.Callable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetIsAliveCommand implements Callable<Boolean> {

    private final WebClient webClient;
    private final String isAliveUrl;
    private final String appName;

    public GetIsAliveCommand(WebClient webClient, String isAliveUrl, String appName){
        this.webClient = webClient;
        this.isAliveUrl = isAliveUrl;
        this.appName = appName;
    }

    @Override
    public Boolean call(){
        try {
            String response = webClient.get()
                    .uri(isAliveUrl.replace("{appName}", appName))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return "1".equals(response);
        } catch (WebClientResponseException.ServiceUnavailable | WebClientResponseException.NotFound e) {
            log.info(e.getMessage());
            return false;
        }
    }
}
