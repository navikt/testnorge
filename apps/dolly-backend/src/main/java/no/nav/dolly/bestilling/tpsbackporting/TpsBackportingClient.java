package no.nav.dolly.bestilling.tpsbackporting;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.getNonPdlTpsCreateEnv;

@Service
@Order(1)
@RequiredArgsConstructor
public class TpsBackportingClient implements ClientRegister {

    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;
    private final TpsfService tpsfService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final DollyPersonCache dollyPersonCache;

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

        if (progress.isTpsf() && nonNull(bestilling.getPdldata()) && isOpprettEndre) {
            pdlDataConsumer.oppdaterPdl(dollyPerson.getHovedperson(),
                    PersonUpdateRequestDTO.builder()
                            .person(bestilling.getPdldata().getPerson())
                            .build());
        }

        if (isOpprettEndre && dollyPerson.isTpsfMaster() &&
                !getNonPdlTpsCreateEnv(bestilling.getEnvironments()).isEmpty() &&
                nonNull(bestilling.getPdldata()) && bestilling.getPdldata().isTpsdataPresent()) {

            var pdldata = pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson()));

            if (nonNull(pdldata) && !pdldata.isEmpty()) {
                var pdlPerson = pdldata.get(0).getPerson();
                var tpsfBestilling = TpsfBestilling.builder()
                        .harIngenAdresse(true)
                        .build();

                mapAtrifacter(pdlPerson, tpsfBestilling);
                dollyPersonCache.fetchIfEmpty(dollyPerson);

                try {
                    var response = tpsfService.endreLeggTilPaaPerson(dollyPerson.getHovedperson(), tpsfBestilling);
                    tpsfBestilling.setDoedsdato(null);
                    var familieResponse = Stream.of(dollyPerson.getPartnere(), dollyPerson.getBarn())
                            .flatMap(Collection::stream)
                            .map(ident -> tpsfService.endreLeggTilPaaPerson(ident, tpsfBestilling))
                            .toList();

                    tpsfService.sendIdenterTilTpsFraTPSF(Stream.of(List.of(response), familieResponse)
                                    .flatMap(Collection::stream)
                                    .map(RsOppdaterPersonResponse::getIdentTupler)
                                    .flatMap(Collection::stream)
                                    .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                                    .collect(Collectors.toSet())
                                    .stream().toList(),
                            getNonPdlTpsCreateEnv(bestilling.getEnvironments()));

                    // Force reload
                    dollyPerson.setPersondetaljer(null);

                } catch (RuntimeException e) {
                    progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));
                }
            }
        }
    }

    private void mapAtrifacter(PersonDTO pdlPerson, TpsfBestilling tpsfBestilling) {

        if (!pdlPerson.getBostedsadresse().isEmpty()) {
            mapperFacade.map(pdlPerson.getBostedsadresse().get(0), tpsfBestilling);
        }
        if (!pdlPerson.getKontaktadresse().isEmpty()) {
            mapperFacade.map(pdlPerson.getKontaktadresse().get(0), tpsfBestilling);
        }
        if (!pdlPerson.getOppholdsadresse().isEmpty()) {
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
        if (!pdlPerson.getStatsborgerskap().isEmpty() &&
                (!"NOR".equals(getStatborgerskap(pdlPerson).getLandkode()) ||
                        IdentType.FNR != IdentTypeUtil.getIdentType(pdlPerson.getIdent()))) {
            mapperFacade.map(getStatborgerskap(pdlPerson), tpsfBestilling);
        }
        if (!pdlPerson.getDoedsfall().isEmpty()) {
            tpsfBestilling.setDoedsdato(pdlPerson.getDoedsfall().get(0).getDoedsdato());
        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
