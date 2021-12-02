package no.nav.dolly.bestilling.tpsbackporting;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class TpsBackportingClient implements ClientRegister {

    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (dollyPerson.isTpsfMaster() && nonNull(bestilling.getPdldata()) && bestilling.getPdldata().isTpsdataPresent()) {

            var pdlPerson = pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson())).stream().findFirst().get();
            var tpsfBestilling = new TpsfBestilling();

            if (pdlPerson.getPerson().getBostedsadresse().isEmpty()) {
                tpsfBestilling.setHarIngenAdresse(true);
                tpsfBestilling.setBoadresse(mapperFacade.map(pdlPerson.getPerson().getBostedsadresse().stream().findFirst().get(), RsAdresse.class));
            }

        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
