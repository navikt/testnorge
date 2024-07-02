package no.nav.registre.testnorge.levendearbeidsforhold.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
/*
@Slf4j
@RequiredArgsConstructor
public class EndreArbeidsforholdCommand {
    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String miljo = "q2";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String CONSUMER = "Dolly";

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
    /*
    @SneakyThrows
    @Override
    public List<ArbeidsforholdDTO> update(){
        return null;
    }
}

 */
