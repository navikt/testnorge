package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.PdlConsumer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {
    //private final PdlConsumer pdlConsumer;

    public void getPerson(String ident) {
        /*
        log.info("Getting persons from PDL");
        String query = "query ($paging:Paging, $criteria:[{\"fieldName\"}]) {\n" +
                "    sokPerson (paging: $paging,  criteria: $criteria){\n" +
                "        pageNumber,\n" +
                "        totalHits,\n" +
                "        totalPages,\n" +
                "        hits {\n" +
                "            score,\n" +
                "            highlights {\n" +
                "                opplysning,\n" +
                "                opplysningsId\n" +
                "                historisk\n" +
                "                matches {\n" +
                "                    field\n" +
                "                    fragments\n" +
                "                }\n" +
                "            }\n" +
                "            person {\n" +
                "                navn(historikk: false) {\n" +
                "                    fornavn\n" +
                "                    etternavn\n" +
                "                    mellomnavn\n" +
                "                    metadata{historisk}\n" +
                "                }\n" +
                "            }}}}";
        pdlConsumer.getPdlPerson(ident, null);

         */
    }
}
