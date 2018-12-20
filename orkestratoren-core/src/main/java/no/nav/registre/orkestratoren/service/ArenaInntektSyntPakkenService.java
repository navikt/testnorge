package no.nav.registre.orkestratoren.service;

import static no.nav.registre.orkestratoren.service.FnrUtility.getFoedselsdatoFraFnr;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.ArenaInntektSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Service
@Slf4j
public class ArenaInntektSyntPakkenService {

    @Autowired
    private ArenaInntektSyntConsumer arenaInntektSyntConsumer;
    @Autowired
    private SyntDataInfoService syntDataInfoService;

    public List<String> genererEnInntektsmeldingPerFnrIInntektstub(SyntetiserInntektsmeldingRequest request) {
        List<String> levendeNordmennFnr = syntDataInfoService.opprettListenLevendeNordmenn(request.getSkdMeldingGruppeId());
        List<String> inntektsmldMottakere = new ArrayList<>();
        inntektsmldMottakere.addAll(levendeNordmennFnr.subList(0, levendeNordmennFnr.size() / 2));
        inntektsmldMottakere.removeIf(fnr -> getFoedselsdatoFraFnr(fnr).isAfter(LocalDate.now().minusYears(13)));//Mottakere av inntektsmeldinger må være minst 13 år
        if (!inntektsmldMottakere.isEmpty()) {
            asyncBestillEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);
        }
        return inntektsmldMottakere;
    }

    @Async
    public void asyncBestillEnInntektsmeldingPerFnrIInntektstub(List<String> inntektsmldMottakere) {
        LocalDateTime bestillingstidspunktet = LocalDateTime.now();
        try {
            arenaInntektSyntConsumer.genererEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);
        } catch (HttpStatusCodeException e) {
            StringBuilder feilmelding = new StringBuilder();
            feilmelding.append("synth-arena-inntekt returnerte feilmeldingen ");
            feilmelding.append(getMessageFromJson(e.getResponseBodyAsString()));
            feilmelding.append(". Bestillingen ble sendt ");
            feilmelding.append(bestillingstidspunktet);
            log.error(feilmelding.toString(), e);
        }
        log.info("synth-arena-inntekt har fullført bestillingen som ble sendt {}. "
                + "Antall inntektsmeldinger opprettet i inntekts-stub: {} ", bestillingstidspunktet, inntektsmldMottakere.size());
    }

    private String getMessageFromJson(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(responseBody).findValue("message").asText();
        } catch (IOException e) {
            return responseBody;
        }
    }
}
