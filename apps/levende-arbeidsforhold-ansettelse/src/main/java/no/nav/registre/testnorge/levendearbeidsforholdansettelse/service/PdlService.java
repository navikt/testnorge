package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.SokPersonVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {
    private final PdlConsumer pdlConsumer;

    public void getPerson(){
        PdlMiljoer miljoer = PdlMiljoer.Q2;
        SokPersonVariables sokPerson = SokPersonVariables
                .builder()
                .pageNumber(1)
                .resultsPerPage(5)
                .from("1955")
                .to("2006")
                .postnr("1???")
                .build();

        JsonNode node = pdlConsumer.getSokPerson(sokPerson.lagSokPersonPaging(),
                        sokPerson.lagSokPersonCriteria(),
                        PdlMiljoer.Q2)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});

        log.info("{}", result);
    }
}
