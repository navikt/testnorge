package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.HentTagsConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.SokPersonVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class PdlService {
    private final PdlConsumer pdlConsumer;
    private int resultsPerPage = 100;
    private String from;
    private String to;
    private String postnr;
    private final HentTagsConsumer hentTagsConsumer;


    public List<Ident> getPersoner(){

        SokPersonVariables sokPersonVariables = lagSokPersonVariables(
                //tilfeldigPageNumber(getSokPersonPages()),
                resultsPerPage,
                from,
                to,
                postnr);

        JsonNode node = pdlConsumer.getSokPerson(sokPersonVariables.lagSokPersonPaging(),
                        sokPersonVariables.lagSokPersonCriteria(),
                        PdlMiljoer.Q2)
                .block();

        List<Ident> identer = new ArrayList<>();

        assert node != null;
        node.get("data").get("sokPerson").findValues("identer").forEach(
                hit -> hit.forEach(
                        ident -> {
                            if(ident.get("gruppe").asText().equals("FOLKEREGISTERIDENT")) {
                                identer.add(new Ident(ident.get("ident").asText(), ident.get("gruppe").asText()));
                            }
                        }
                )
        );
        return identer;
    }

    private int getSokPersonPages() {
        SokPersonVariables sokPersonVariablesEnPage = SokPersonVariables
                .builder()
                .pageNumber(1)
                .resultsPerPage(1)
                .from(from)
                .to(to)
                .postnr(postnr)
                .build();

        JsonNode node = pdlConsumer.getSokPersonPages(sokPersonVariablesEnPage.lagSokPersonPaging(),
                        sokPersonVariablesEnPage.lagSokPersonCriteria(),
                        PdlMiljoer.Q2)
                .block();

        assert node != null;
        return node.get("data").get("sokPerson").findValues("totalPages").getFirst().asInt()/resultsPerPage;
    }

    private int tilfeldigPageNumber(int totalPages) {
        Random random = new Random();
        return random.nextInt(totalPages);
    }

    private SokPersonVariables lagSokPersonVariables( int resultsPerPage, String from, String to, String postnr) {//int pageNumber,
         return SokPersonVariables
                 .builder()
                 //.pageNumber(pageNumber)
                 .resultsPerPage(resultsPerPage)
                 .from(from)
                 .to(to)
                 .postnr(postnr)
                 .build();
    }

    public void HentTags(String[] identer) throws Exception {
        hentTagsConsumer.hentTags(identer);
    }
}
