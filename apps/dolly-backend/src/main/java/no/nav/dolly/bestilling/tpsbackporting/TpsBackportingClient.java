package no.nav.dolly.bestilling.tpsbackporting;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@Order(8)
@RequiredArgsConstructor
public class TpsBackportingClient implements ClientRegister {

    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;
    private final TpsfService tpsfService;

    private static StatsborgerskapDTO getStatborgerskap(PersonDTO pdlPerson) {

        // Velg norsk statsborskap hvis dette finnes, TPS har kun et aktivt og NOR er dominerende
        return pdlPerson.getStatsborgerskap().stream()
                .anyMatch(statsborgerskap -> "NOR".equals(statsborgerskap.getLandkode())) ?
                pdlPerson.getStatsborgerskap().stream()
                        .filter(statsborgerskap -> "NOR".equals(statsborgerskap.getLandkode()))
                        .toList().get(0) :
                pdlPerson.getStatsborgerskap().get(0);
    }

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isOpprettEndre && dollyPerson.isTpsfMaster() &&
                nonNull(bestilling.getPdldata()) && bestilling.getPdldata().isTpsdataPresent()) {

            var pdldata = pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson()));

            if (nonNull(pdldata) && !pdldata.isEmpty()) {
                var pdlPerson = pdldata.get(0).getPerson();
                var tpsfBestilling = TpsfBestilling.builder()
                        .harIngenAdresse(true)
                        .build();

                if (!pdlPerson.getBostedsadresse().isEmpty()) {
                    mapperFacade.map(pdlPerson.getBostedsadresse().get(0), tpsfBestilling);
                }
                if (!pdlPerson.getKontaktadresse().isEmpty()) {
                    mapperFacade.map(pdlPerson.getKontaktadresse().get(0), tpsfBestilling);
                }
                if (!pdlPerson.getKontaktadresse().isEmpty()) {
                    mapperFacade.map(pdlPerson.getOppholdsadresse().get(0), tpsfBestilling);
                }
                if (!pdlPerson.getAdressebeskyttelse().isEmpty()) {
                    mapperFacade.map(pdlPerson.getAdressebeskyttelse().get(0), tpsfBestilling);
                }
                if (!pdlPerson.getInnflytting().isEmpty()) {
                    mapperFacade.map(pdlPerson.getInnflytting().get(0), tpsfBestilling);
                }
                if (!pdlPerson.getUtflytting().isEmpty()) {
                    mapperFacade.map(pdlPerson.getUtflytting().get(0), tpsfBestilling);
                }
                if (!pdlPerson.getStatsborgerskap().isEmpty()) {
                    mapperFacade.map(getStatborgerskap(pdlPerson), tpsfBestilling);
                }
                if (!pdlPerson.getDoedsfall().isEmpty()) {
                    tpsfBestilling.setDoedsdato(pdlPerson.getDoedsfall().get(0).getDoedsdato());
                }

                var tpsresponse = tpsfService.endreLeggTilPaaPerson(dollyPerson.getHovedperson(), tpsfBestilling);
                tpsfService.sendIdenterTilTpsFraTPSF(List.of(dollyPerson.getHovedperson()), bestilling.getEnvironments());
            }
        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
