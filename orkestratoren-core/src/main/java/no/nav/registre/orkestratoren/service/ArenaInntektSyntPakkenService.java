package no.nav.registre.orkestratoren.service;

import static no.nav.registre.orkestratoren.service.FnrUtility.getFoedselsdatoFraFnr;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

import no.nav.registre.orkestratoren.consumer.rs.ArenaInntektSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Service
@Slf4j
public class ArenaInntektSyntPakkenService {

    @Autowired
    private ArenaInntektSyntConsumer arenaInntektSyntConsumer;

    @Autowired
    private SyntDataInfoService syntDataInfoService;

    public List<String> genererEnInntektsmeldingPerFnrIInntektstub(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
//        List<String> levendeNordmennFnr = syntDataInfoService.opprettListenLevendeNordmenn(syntetiserInntektsmeldingRequest.getAvspillergruppeId());
//        List<String> inntektsmldMottakere = new ArrayList<>();
//        inntektsmldMottakere.addAll(levendeNordmennFnr.subList(0, levendeNordmennFnr.size() / 2));
//        inntektsmldMottakere.removeIf(fnr -> getFoedselsdatoFraFnr(fnr).isAfter(LocalDate.now().minusYears(13)));// Mottakere av inntektsmeldinger må være minst 13 år
//        if (!inntektsmldMottakere.isEmpty()) {
//            arenaInntektSyntConsumer.asyncBestillEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);
//        }
//        return inntektsmldMottakere;

        return arenaInntektSyntConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);
    }
}
