package no.nav.dolly.bestilling.sykemelding;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SykemeldingTransaksjon;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.sykemelding.RsDetaljertSykemelding;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsSyntSykemelding;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private final SykemeldingConsumer sykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final TpsfPersonCache tpsfPersonCache;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSykemelding())) {

            StringBuilder status = new StringBuilder();
            try {
                tpsfPersonCache.fetchIfEmpty(tpsPerson);
                SyntSykemeldingRequest syntSykemeldingRequest = mapperFacade.map(bestilling.getSykemelding().getSyntSykemelding(), SyntSykemeldingRequest.class);
                syntSykemeldingRequest.setIdent(tpsPerson.getHovedperson());

                DetaljertSykemeldingRequest detaljertSykemeldingRequest = mapperFacade.map(bestilling.getSykemelding().getDetaljertSykemelding(), DetaljertSykemeldingRequest.class);
                Person pasient = tpsPerson.getPerson(tpsPerson.getHovedperson());
                BoGateadresse pasientAdresse = (BoGateadresse) pasient.getBoadresse().get(0);

                // Denne skulle vi hente? detaljertSykemeldingRequest.getLege()

                detaljertSykemeldingRequest.setPasient(DetaljertSykemeldingRequest.Pasient.builder()
                        .fornavn(pasient.getFornavn())
                        .etternavn(pasient.getEtternavn())
                        .mellomnavn(isNull(pasient.getMellomnavn()) ? null : pasient.getMellomnavn())
                        .foedselsdato(pasient.getFoedselsdato())
                        .ident(pasient.getIdent())
                        .navKontor(pasient.getTknavn())
                        .telefon(isNull(pasient.getTelefonnummer_1()) ? null : pasient.getTelefonnummer_1())
                        .adresse(DetaljertSykemeldingRequest.Adresse.builder()
                                .by(pasient.getBoadresse().get(0).getPostnr())
                                .gate(pasientAdresse.getGateadresse())
                                .land(pasient.getStatsborgerskap().get(0).getStatsborgerskap())
                                .postnummer(pasient.getBoadresse().get(0).getPostnr())
                                .build())
                        .build());

                if (!transaksjonMappingService.existAlready(SYKEMELDING, tpsPerson.getHovedperson(), null) || isOpprettEndre) {

                    ResponseEntity<String> response = sykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest);
                    if (response.hasBody()) {
                        status.append("OK");
                        RsSyntSykemelding syntSykemelding = bestilling.getSykemelding().getSyntSykemelding();

                        saveTranskasjonId(syntSykemelding.getOrgnummer(), syntSykemelding.getArbeidsforholdId(), tpsPerson.getHovedperson());
                    }

                    ResponseEntity<String> responseDetaljert = sykemeldingConsumer.postDetaljertSykemelding(detaljertSykemeldingRequest);
                    if (responseDetaljert.hasBody()) {
                        status.append("OK");
                        RsDetaljertSykemelding detaljertSykemelding = bestilling.getSykemelding().getDetaljertSykemelding();

                        // saveTranskasjonId(detaljertSykemelding.get, tpsPerson.getHovedperson());
                    }

                }
            } catch (RuntimeException e) {

                status.append(errorStatusDecoder.decodeRuntimeException(e));
            }
            progress.setSykemeldingStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTranskasjonId(String orgnummer, String arbeidsforholdsId, String ident) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .transaksjonId(toJson(SykemeldingTransaksjon.builder()
                                .orgnummer(orgnummer)
                                .arbeidsforholdId(arbeidsforholdsId)))
                        .datoEndret(LocalDateTime.now())
                        .system(SYKEMELDING.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for sykemelding");
        }
        return null;
    }
}
