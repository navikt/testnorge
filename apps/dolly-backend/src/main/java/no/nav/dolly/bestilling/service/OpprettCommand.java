package no.nav.dolly.bestilling.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class OpprettCommand implements Callable<List<String>> {

    private final AvailCheckCommand.AvailStatus availStatus;
    private final RsDollyBestillingRequest bestKriterier;
    private final TpsfService tpsfService;
    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public List<String> call() {

        if (availStatus.isTpsf()) {
            TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                    mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setOpprettFraIdenter(new ArrayList<>(List.of(availStatus.getIdent())));
            tpsfBestilling.setAntall(tpsfBestilling.getOpprettFraIdenter().size());
            return tpsfService.opprettIdenterTpsf(tpsfBestilling);

        } else {
            var opprettPdlPerson = mapperFacade.map(bestKriterier.getPdldata(), BestillingRequestDTO.class);
            opprettPdlPerson.setOpprettFraIdent(availStatus.getIdent());
            return List.of(pdlDataConsumer.opprettPdl(opprettPdlPerson));
        }
    }
}
