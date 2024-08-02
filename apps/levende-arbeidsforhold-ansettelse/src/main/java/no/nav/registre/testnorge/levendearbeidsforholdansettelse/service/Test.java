package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.TagsDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.Arbeidsforhold;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_ORGANISASJONER;

@Service
@Slf4j
@RequiredArgsConstructor
public class Test {

    private final PdlService pdlService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final AnsettelseService ansettelseService;
    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        ansettelseService.runAnsettelseService();
        /*
        pdlService.setFrom("1960");
        pdlService.setTo("2011");
        pdlService.setPostnr("2100");
        List<Ident> personer = pdlService.getPersoner();
        List<String> ident =new ArrayList<>();
        personer.forEach(pers -> ident.add(pers.getIdent()));
        TagsDTO dto = pdlService.HentTags(ident);
        log.info("TagsDTO: {}", dto.toString());

         */
        /*
        for (Ident ident: idents){
            List<Arbeidsforhold> arbeidsforhold = arbeidsforholdService.hentArbeidsforhold(ident.getIdent());
            log.info("arbeidsforhold: {}", arbeidsforhold.toString());
            for(Arbeidsforhold arbeidsforhold1: arbeidsforhold){
                log.info("Arbeidsavtale: {}",arbeidsforhold1.getArbeidsavtaler().toString());

            }
        }

         */

    }
}
