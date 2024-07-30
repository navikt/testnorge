package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    //@EventListener(ApplicationReadyEvent.class)
    public void test() throws Exception {


    }
}
