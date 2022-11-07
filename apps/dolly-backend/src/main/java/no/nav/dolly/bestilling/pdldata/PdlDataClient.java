package no.nav.dolly.bestilling.pdldata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Order(2)
@Service
@RequiredArgsConstructor
public class PdlDataClient implements ClientRegister {

    private final PdlDataConsumer pdlDataConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        try {
            if (progress.isTpsf() && nonNull(bestilling.getPdldata()) && isOpprettEndre) {
                pdlDataConsumer.oppdaterPdl(dollyPerson.getHovedperson(),
                        PersonUpdateRequestDTO.builder()
                                .person(bestilling.getPdldata().getPerson())
                                .build());
            }

            if (!progress.isPdl()  && isBlank(progress.getPdlDataStatus())) {
                progress.setPdlDataStatus(pdlDataConsumer.sendOrdre(dollyPerson.getHovedperson(),
                        progress.isTpsf(),
                        isTrue(bestilling.getEkskluderEksternePersoner())));
            }

        } catch (RuntimeException e) {

            progress.setPdlDataStatus(errorStatusDecoder.decodeThrowable(e));
        }
    }

    @Override
    public void release(List<String> identer) {

        // Sletting gjøres nå i PersonService
    }

    public Object status() {
        return pdlDataConsumer.checkStatus();
    }
}
