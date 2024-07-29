package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class Test {

    private final PdlService pdlService;

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws Exception {

        pdlService.setFrom("1988");
        pdlService.setTo("2011");
        pdlService.setPostnr("2100");
        List<Ident> personer =  pdlService.getPersoner();
        log.info("Personer {}", personer.toString());
        List<String> identer = new ArrayList<>();
        personer.forEach(pers -> identer.add(pers.getIdent()));
        String[] ident = identer.stream().map(i -> i).toArray(String[]::new);
        log.info("ident: {}", Arrays.toString(ident));
        personer.forEach(pers -> identer.add(pers.getIdent()));
        pdlService.HentTags(ident);
    }
}
