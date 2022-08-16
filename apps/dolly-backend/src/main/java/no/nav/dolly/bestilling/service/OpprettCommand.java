package no.nav.dolly.bestilling.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OpprettCommand implements Callable<String> {

    private final AvailCheckCommand.AvailStatus availStatus;
    private final RsDollyBestillingRequest bestKriterier;
    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public String call() {

        var opprettPdlPerson = mapperFacade.map(bestKriterier.getPdldata(), BestillingRequestDTO.class);
        opprettPdlPerson.setOpprettFraIdent(availStatus.getIdent());
        return pdlDataConsumer.opprettPdl(opprettPdlPerson);
    }
}
