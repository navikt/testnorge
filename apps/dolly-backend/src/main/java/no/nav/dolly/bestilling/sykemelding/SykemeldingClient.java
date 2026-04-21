package no.nav.dolly.bestilling.sykemelding;


import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sykemelding.domain.dto.SykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.SykemeldingResponseDTO;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.util.DollyTextUtil.getGenereringStartet;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private final SykemeldingConsumer sykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final ApplicationConfig applicationConfig;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling,
                                                DollyPerson dollyPerson,
                                                BestillingProgress progress,
                                                boolean isOpprettEndre) {

        RsSykemelding sykemelding = bestilling.getSykemelding();

        if (isNull(sykemelding) || !sykemelding.hasNySykemelding()) {
            return Mono.just(progress);
        }

        var nySykemelding = sykemelding.getNySykemelding();
        return setProgress(progress, getGenereringStartet())
                .then(postNySykemelding(nySykemelding, dollyPerson.getIdent())
                        .map(this::getStatus)
                        .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                        .onErrorResume(error ->
                                Mono.just(encodeStatus(WebClientError.describe(error).getMessage()))))
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(ident -> sykemeldingConsumer.deleteTsmSykemeldinger(ident)
                .subscribe());
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(
                progress,
                BestillingProgress::getSykemeldingStatus,
                BestillingProgress::setSykemeldingStatus,
                status);
    }

    private String getStatus(SykemeldingResponseDTO status) {
        log.info("Sykemelding response for {} mottatt, {}", status.getIdent(), Json.pretty(status));
        return status.getStatus().is2xxSuccessful()
                ? "OK"
                : errorStatusDecoder.getErrorText(status.getStatus(), status.getAvvik());
    }

    private Mono<BestillingProgress> setProgress(BestillingProgress progress, String status) {
        return transactionHelperService.persister(
                progress,
                BestillingProgress::getSykemeldingStatus,
                BestillingProgress::setSykemeldingStatus,
                status);
    }

    private Mono<SykemeldingResponseDTO> postNySykemelding(RsSykemelding.RsNySykemelding rsNySykemelding,
                                                           String ident) {

        var aktivitet = rsNySykemelding.getAktivitet().stream()
                .map(a -> SykemeldingRequestDTO.Aktivitet.builder()
                        .grad(a.getGrad())
                        .fom(a.getFom())
                        .tom(a.getTom())
                        .build())
                .collect(Collectors.toList());

        SykemeldingRequestDTO request = new SykemeldingRequestDTO(ident, aktivitet);

        return sykemeldingConsumer.postTsmSykemelding(request);
    }
}
