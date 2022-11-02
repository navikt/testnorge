package no.nav.dolly.bestilling.aareg.amelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.AmeldingTransaksjon;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.dolly.bestilling.aareg.util.AaaregUtility.appendResult;
import static no.nav.dolly.domain.resultset.SystemTyper.AAREG;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmeldingService {

    private final AmeldingConsumer ameldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final TransaksjonMappingService transaksjonMappingService;

    public String sendAmelding(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                               Long bestillingId, List<String> miljoer) {

        var result = new StringBuilder();

        miljoer.forEach(miljoe -> sendAmelding(bestilling, dollyPerson, bestillingId, result, miljoe));
        return result.length() > 1 ? result.substring(1) : null;
    }

    public void sendAmelding(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, Long bestillingId,
                              StringBuilder result, String env) {
        try {
            Map<String, AMeldingDTO> dtoMaanedMap = new HashMap<>();

            var orgnumre = bestilling.getAareg().get(0).getAmelding().stream()
                    .map(RsAmeldingRequest::getArbeidsforhold)
                    .flatMap(Collection::stream)
                    .map(RsArbeidsforholdAareg::getArbeidsgiver)
                    .map(RsArbeidsforholdAareg.RsArbeidsgiver::getOrgnummer)
                    .collect(Collectors.toSet());
            var organisasjoner = organisasjonServiceConsumer.getOrganisasjoner(orgnumre, env);
            bestilling.getAareg().get(0).getAmelding().forEach(amelding -> {

                var opplysningspliktig = new HashMap<>();
                orgnumre.forEach(orgnummer -> opplysningspliktig.put(orgnummer,
                        organisasjoner.stream()
                                .filter(org -> orgnummer.equals(org.getOrgnummer()))
                                .map(OrganisasjonDTO::getJuridiskEnhet)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException(String.format("Juridisk enhet for organisasjon: %s ikke funnet i miljø: %s", orgnummer, env)))));
                MappingContext context = new MappingContext.Factory().getContext();

                context.setProperty("personIdent", dollyPerson.getHovedperson());
                context.setProperty("arbeidsforholdstype", bestilling.getAareg().get(0).getArbeidsforholdstype());
                context.setProperty("opplysningsPliktig", opplysningspliktig);

                dtoMaanedMap.put(amelding.getMaaned(), mapperFacade.map(amelding, AMeldingDTO.class, context));
            });

            Optional
                    .ofNullable(sendAmeldinger(dtoMaanedMap.values().stream().toList(), env))
                    .orElse(Map.of())
                    .forEach((key, value) -> {
                        if (value.getStatusCode().is2xxSuccessful()) {
                            saveTransaksjonId(value, key, dollyPerson.getHovedperson(), bestillingId, env);
                        }
                        appendResult(
                                Map.entry(key,
                                        value.getStatusCode().is2xxSuccessful()
                                                ? "OK"
                                                : value.getStatusCode().getReasonPhrase()),
                                "1",
                                result);
                    });
        } catch (RuntimeException e) {
            log.error("Innsending til A-melding service feilet: ", e);
            appendResult(Map.entry(env, errorStatusDecoder.decodeRuntimeException(e)), "1", result);
        }
    }

    private Map<String, ResponseEntity<Void>> sendAmeldinger(List<AMeldingDTO> ameldingList, String miljoe) {
        return ameldingConsumer.createOrder(ameldingList, miljoe).blockLast();
    }

    private void saveTransaksjonId(
            ResponseEntity<Void> response,
            String maaned,
            String ident,
            Long bestillingId,
            String miljoe
    ) {
        transaksjonMappingService.save(
                TransaksjonMapping
                        .builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(
                                AmeldingTransaksjon
                                        .builder()
                                        .id(response.getHeaders().getFirst("id"))
                                        .maaned(maaned)
                                        .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(AAREG.name())
                        .build()
        );
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for aareg", e);
        }
        return null;
    }

}
