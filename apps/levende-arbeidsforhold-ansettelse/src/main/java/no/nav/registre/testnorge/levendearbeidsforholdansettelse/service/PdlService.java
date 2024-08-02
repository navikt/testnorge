package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.HentTagsConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.TagsDTO;
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

    /**
     * Lager SokPersonVariabler som matcher filterene man vil basere søket på, og henter personer fra PDL som
     * oppfyller kravene. I tillegg filtreres vekk personer som er i bruk andre steder enn Testnorge
     *
     * @return En liste med identer for personene som matcher søk-variablene
     */
    public List<Ident> getPersoner(){

        SokPersonVariables sokPersonVariables = lagSokPersonVariables(
                tilfeldigPageNumber(getSokPersonPages()),
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
        return harBareTestnorgeTags(identer);
    }

    /**
     * Sjekker om personene kun brukes i Testnorge
     *
     * @param personer Ident-liste med personer man vil sjekke
     * @return En liste med Ident-objekter som oppfyller kravet
     */
    private List<Ident> harBareTestnorgeTags(List<Ident> personer){
        List<String> identer = new ArrayList<>();
        personer.forEach(person -> identer.add(person.getIdent()));
        TagsDTO tagsDTO = hentTags(identer);

        for(var id : tagsDTO.getPersonerTags().entrySet()){
            List<String> value = id.getValue();
            if(!(value.size() == 1 && value.getFirst().contains("TESTNORGE"))){
                String iden = id.getKey();
                personer.removeIf(ide -> ide.getIdent().equals(iden));
            }
        }
        return personer;
    }

    /**
     * Henter ut antall sider med ett treff per side fra PDL slik at man
     * kan hente et tilfeldig sidetall å hente personer fra
     *
     * @return Antallet sider med kun ett treff per side fra PDL
     */
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
        int pages = node.get("data").get("sokPerson").findValues("totalPages").getFirst().asInt()/resultsPerPage;
        return (pages == 0) ? 1 : pages;
    }

    /**
     * @param totalPages Maks-antall
     * @return Et tilfeldig tall mellom 1 og opgitt maks-antall
     */
    private int tilfeldigPageNumber(int totalPages) {
        Random random = new Random();
        return random.nextInt(totalPages);
    }

    /**
     * Bygger et SokPersonVariables-objekt med de oppgitte parameterene som brukes til å filtrere spørringen mot PDl
     *
     * @param pageNumber Sidetallet resultatene skal hentes fra
     * @param resultsPerPage Antall treff per side
     * @param from Tidligste dato for alders-intervallet det skal søkes på
     * @param to Seneste dato for alders-intervallet det skal søkes på
     * @param postnr Postnummer det skal søkes på
     * @return SokPersonVariables-objekt basert på parameterene
     */
    private SokPersonVariables lagSokPersonVariables(int pageNumber, int resultsPerPage, String from, String to, String postnr) {
         return SokPersonVariables
                 .builder()
                 .pageNumber(pageNumber)
                 .resultsPerPage(resultsPerPage)
                 .from(from)
                 .to(to)
                 .postnr(postnr)
                 .build();
    }

    /**
     *
     * @param identer Liste med identnummere
     * @return TagsDTO for hver ident i identer-listen
     */
    private TagsDTO hentTags(List<String> identer) {
         return hentTagsConsumer.hentTags(identer);
    }
}
