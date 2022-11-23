package no.nav.registre.tp.consumer.command;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class TpPostStringListCommand extends TpPostListCommand<String[]> {

    public TpPostStringListCommand(String url, List<String> fnrs, WebClient webClient) {
        super(url, String[].class, new String[0], fnrs, webClient);
    }

}
