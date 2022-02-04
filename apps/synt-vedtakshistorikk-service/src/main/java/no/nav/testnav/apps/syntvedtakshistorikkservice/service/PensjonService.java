package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PensjonTestdataFacadeConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataInntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataPerson;
import no.nav.testnav.libs.servletcore.util.IdentUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonService {

    private final Random rand = new Random();
    private final PensjonTestdataFacadeConsumer pensjonTestdataFacadeConsumer;

    private static final String REGEX_RN = "[\r\n]";

    boolean opprettetPersonOgInntektIPopp(
            String ident,
            String miljoe,
            LocalDate fraDato
    ) {
        return opprettPersonIPopp(ident, miljoe) && opprettInntektIPopp(ident, miljoe, fraDato);
    }

    private boolean opprettPersonIPopp(
            String ident,
            String miljoe
    ) {
        var opprettPersonStatus = pensjonTestdataFacadeConsumer.opprettPerson(PensjonTestdataPerson.builder()
                .bostedsland("NOR")
                .fodselsDato(IdentUtil.getFoedselsdatoFraIdent(ident))
                .miljoer(Collections.singletonList(miljoe))
                .fnr(ident)
                .build());

        if (opprettPersonStatus.getStatus().isEmpty()) {
            return false;
        }

        for (var response : opprettPersonStatus.getStatus()) {
            if (response.getResponse().getHttpStatus().getStatus() != 200) {
                log.error(
                        "Kunne ikke opprette ident {} i popp i miljø {}. Feilmelding: {}",
                        ident.replaceAll(REGEX_RN, ""),
                        response.getMiljo().replaceAll(REGEX_RN, ""),
                        response.getResponse().getMessage().replaceAll(REGEX_RN, "")
                );
                return false;
            }
        }
        return true;
    }

    private boolean opprettInntektIPopp(
            String ident,
            String miljoe,
            LocalDate fraDato
    ) {
        var opprettInntektStatus = pensjonTestdataFacadeConsumer.opprettInntekt(PensjonTestdataInntekt.builder()
                .belop(rand.nextInt(650_000) + 450_000)
                .fnr(ident)
                .fomAar(fraDato.minusYears(4).getYear())
                .miljoer(Collections.singletonList(miljoe))
                .redusertMedGrunnbelop(true)
                .tomAar(fraDato.minusYears(1).getYear())
                .build());

        if (opprettInntektStatus.getStatus().isEmpty()) {
            return false;
        }

        for (var response : opprettInntektStatus.getStatus()) {
            if (response.getResponse().getHttpStatus().getStatus() != 200) {
                log.error(
                        "Kunne ikke opprette inntekt på ident {} i popp i miljø {}. Feilmelding: {}",
                        ident.replaceAll(REGEX_RN, ""),
                        response.getMiljo().replaceAll(REGEX_RN, ""),
                        response.getResponse().getMessage().replaceAll(REGEX_RN, "")
                );
                return false;
            }
        }
        return true;
    }
}
