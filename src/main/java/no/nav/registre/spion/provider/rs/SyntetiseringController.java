package no.nav.registre.spion.provider.rs;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.provider.rs.response.SyntetiserSpionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;
import no.nav.registre.spion.provider.rs.response.SyntetiserVedtakResponse;
import no.nav.registre.spion.service.SyntetiseringService;

import static no.nav.registre.spion.utils.SwaggerUtils.REQUEST_DESCRIPTION;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    private final VedtakPublisher vedtakPublisher;


    @Value("${tps.forvalter.avspillergruppe.id}")
    private String defaultAvspillergruppeId;

    @Value("${tps.forvalter.miljoe}")
    private String defaultMiljoe;


    @PostMapping(value = "/vedtak")
    @ApiOperation(value = "Generer syntetiske vedtak for et gitt antall personer og legger dem på Kafka kø til SPION.")
    @Transactional
    public SyntetiserSpionResponse genererVedtakForSPION(@ApiParam(value = REQUEST_DESCRIPTION) @RequestBody SyntetiserSpionRequest request) throws JsonProcessingException {

        List<SyntetiserVedtakResponse> syntetisertvedtaksliste = syntetiseringService.syntetiserVedtak(
                Objects.isNull(request.getAvspillergruppeId()) ?
                        Long.valueOf(defaultAvspillergruppeId) : request.getAvspillergruppeId(),
                Objects.isNull(request.getMiljoe()) ? defaultMiljoe : request.getMiljoe(),
                request.getNumPersons(),
                request.getStartDate(),
                request.getEndDate(),
                request.getNumPeriods());

        int vedtakSyntetisertForAntallPersoner = syntetisertvedtaksliste.size();

        log.info("Vedtak for {} person(er) ble syntetisert.", vedtakSyntetisertForAntallPersoner);

        int antallVellykketSendinger = vedtakPublisher.publish(syntetisertvedtaksliste);

        return new SyntetiserSpionResponse(vedtakSyntetisertForAntallPersoner, antallVellykketSendinger);
    }

}
