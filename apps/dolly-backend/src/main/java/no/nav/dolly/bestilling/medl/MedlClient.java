package no.nav.dolly.bestilling.medl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.medl.dto.MedlPostResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedlClient implements ClientRegister {

    private static final String STATUS_AVVIST = "AVST";
    private static final String STATUSAARSAK_FEILREGISTRERT = "Feilregistrert";

    private final MedlConsumer medlConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getMedl())) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", dollyPerson.getIdent());

            var medlRequest = mapperFacade.map(
                    bestilling.getMedl(),
                    MedlData.class, context);

            return medlConsumer.createMedlemskapsperiode(medlRequest)
                    .map(this::getStatus)
                    .flatMap(status -> oppdaterStatus(progress,
                            formaterStatusResponse(status)
                    ));
        }

        return Mono.empty();
    }

    @Override
    public void release(List<String> identer) {

        medlConsumer.getMedlemskapsperioder(identer)
                .map(medlDataResponse -> {

                    var gjeldendeMedlemskapsperiode = mapperFacade.map(medlDataResponse, MedlData.class);
                    gjeldendeMedlemskapsperiode.setStatus(STATUS_AVVIST);
                    gjeldendeMedlemskapsperiode.setStatusaarsak(STATUSAARSAK_FEILREGISTRERT);

                    return gjeldendeMedlemskapsperiode;
                })
                .flatMap(medlConsumer::deleteMedlemskapsperioder)
                .collectList()
                .subscribe(response -> log.info("Sletting utført mot Medl"));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setMedlStatus, status);
    }

    private String getStatus(MedlPostResponse response) {

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getMelding());
    }

    private static String formaterStatusResponse(String status) {
        return Arrays.stream(status.split(";"))
                .filter(errorResponse -> errorResponse.contains("detail"))
                .findFirst().orElse(status)
                .replace("detail= ", "");
    }
}
