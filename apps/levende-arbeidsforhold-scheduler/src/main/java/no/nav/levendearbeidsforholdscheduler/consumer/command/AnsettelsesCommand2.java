package no.nav.levendearbeidsforholdscheduler.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;


import java.util.concurrent.Callable;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;

@RequiredArgsConstructor
public class AnsettelsesCommand2 implements Callable<String> {
    private final WebClient webClient;
    private final String token;
    @Override
    public String call()  {
        return webClient.get().uri(
                builder -> builder.path("/api/ansettelse-jobb")
                        .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
