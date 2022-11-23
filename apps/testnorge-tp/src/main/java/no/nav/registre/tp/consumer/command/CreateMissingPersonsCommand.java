package no.nav.registre.tp.consumer.command;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class CreateMissingPersonsCommand extends TpPostStringListCommand {

    public CreateMissingPersonsCommand(List<String> fnrs, WebClient webClient) {
        super("/api/tp/missing-persons", fnrs, webClient);
    }

}
