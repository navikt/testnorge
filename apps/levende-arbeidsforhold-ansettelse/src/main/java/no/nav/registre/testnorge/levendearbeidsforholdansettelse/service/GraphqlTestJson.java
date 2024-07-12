package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.SokPersonVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphqlTestJson {

    private final PdlConsumer pdlConsumer;

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws JsonProcessingException {
        log.info("Er i test");

        SokPersonVariables sokPerson = SokPersonVariables
                .builder()
                .pageNumber(1)
                .resultsPerPage(5)
                .from("1955")
                .to("2006")
                .postNr("1???")
                .build();

        JsonNode node = pdlConsumer.getSokPerson(sokPerson.lagSokPersonPaging(),
                sokPerson.lagSokPersonCriteria(),
                PdlMiljoer.Q2)
                .block();

        log.info("{}", node);
    }

}
