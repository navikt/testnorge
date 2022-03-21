package no.nav.dolly.bestilling.tpsbackporting;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@Order(1)
@RequiredArgsConstructor
public class TpsBackportingClient implements ClientRegister {

    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;
    private final TpsfService tpsfService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final DollyPersonCache dollyPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        try {
            if (progress.isTpsf() && nonNull(bestilling.getPdldata()) && isOpprettEndre) {
                pdlDataConsumer.oppdaterPdl(dollyPerson.getHovedperson(),
                        PersonUpdateRequestDTO.builder()
                                .person(bestilling.getPdldata().getPerson())
                                .build());
            }
        } catch (
                WebClientResponseException e) {

            progress.setPdlDataStatus(errorStatusDecoder.decodeRuntimeException(e));
            return;
        }

        if (isOpprettEndre && dollyPerson.isTpsfMaster() &&
                nonNull(bestilling.getPdldata()) && bestilling.getPdldata().isTpsdataPresent()) {

            var pdldata = pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson()));

            if (nonNull(pdldata) && !pdldata.isEmpty()) {
                var pdlPerson = pdldata.get(0).getPerson();
                var tpsfBestilling = TpsfBestilling.builder()
                        .harIngenAdresse(true)
                        .build();

                mapAtrifacter(bestilling.getPdldata(), pdlPerson, tpsfBestilling);
                dollyPersonCache.fetchIfEmpty(dollyPerson);

                try {
                    var response = tpsfService.endreLeggTilPaaPerson(dollyPerson.getHovedperson(), tpsfBestilling);
                    tpsfBestilling.setDoedsdato(null);
                    var familieResponse = Stream.of(dollyPerson.getPartnere(), dollyPerson.getBarn())
                            .flatMap(Collection::stream)
                            .map(ident -> tpsfService.endreLeggTilPaaPerson(ident, tpsfBestilling))
                            .toList();

                    // Force reload
                    dollyPerson.setPersondetaljer(null);

                } catch (RuntimeException e) {
                    progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));
                }
            }
        }
    }

    private void mapAtrifacter(PdlPersondata bestilling, PersonDTO pdlPerson, TpsfBestilling tpsfBestilling) {

        if (!bestilling.getPerson().getBostedsadresse().isEmpty()) {
            mapperFacade.map(pdlPerson.getBostedsadresse().get(0), tpsfBestilling);
        }
        if (!bestilling.getPerson().getKontaktadresse().isEmpty()) {
            mapperFacade.map(pdlPerson.getKontaktadresse().get(0), tpsfBestilling);
        }
        if (!bestilling.getPerson().getOppholdsadresse().isEmpty()) {
            mapperFacade.map(pdlPerson.getOppholdsadresse().get(0), tpsfBestilling);
        }
        if (!bestilling.getPerson().getInnflytting().isEmpty()) {
            mapperFacade.map(pdlPerson.getInnflytting().get(0), tpsfBestilling);
        }
        if (!bestilling.getPerson().getUtflytting().isEmpty()) {
            mapperFacade.map(pdlPerson.getUtflytting().get(0), tpsfBestilling);
        }
        if (!bestilling.getPerson().getDoedsfall().isEmpty()) {
            tpsfBestilling.setDoedsdato(pdlPerson.getDoedsfall().get(0).getDoedsdato());
        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
