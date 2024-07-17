package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.SokPersonVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {
    private final PdlConsumer pdlConsumer;

    //@EventListener(ApplicationReadyEvent.class)
    public List<Ident> getPerson(){
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

        List<Ident> identer = new ArrayList<>();

        node.get("data").get("sokPerson").findValues("identer").forEach(
                hit -> {
                    hit.forEach(
                            ident -> {
                                if(ident.get("gruppe").asText().equals("FOLKEREGISTERIDENT")) {
                                    identer.add(new Ident(ident.get("ident").asText(), ident.get("gruppe").asText()));
                                }
                            }
                    );
                }
        );

        return identer;

    }
}
