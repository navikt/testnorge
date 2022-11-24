package no.nav.registre.tp.consumer.command;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class RemovePersonsCommand extends TpPostStringListCommand {

    public RemovePersonsCommand(List<String> fnrs, WebClient webClient) {
        super("/api/tp/remove-persons", fnrs, webClient);
    }
}
