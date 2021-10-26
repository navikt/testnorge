package no.nav.dolly.bestilling.aareg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingConsumer;
import no.nav.dolly.bestilling.aareg.amelding.OrganisasjonServiceConsumer;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AmeldingTransaksjon;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.aareg.util.AaregMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg.RsArbeidsgiver;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.AAREG;

@Slf4j
@Order(4)
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    private static final int NOT_FOUND = -1;

    private final AaregConsumer aaregConsumer;
    private final AmeldingConsumer ameldingConsumer;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        StringBuilder result = new StringBuilder();

        if (!bestilling.getAareg().isEmpty()) {
            bestilling.getEnvironments().forEach(env -> {
                if (nonNull(bestilling.getAareg().get(0).getAmelding()) && !bestilling.getAareg().get(0).getAmelding().isEmpty()) {
                    sendAmelding(bestilling, dollyPerson, progress, result, env);
                } else {
                    sendArbeidsforhold(bestilling, dollyPerson, isOpprettEndre, result, env);
                }
            });
        }
        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(aaregConsumer::slettArbeidsforholdFraAlleMiljoer);
    }

    private void sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, boolean isOpprettEndre, StringBuilder result, String env) {
        try {

            MappingContext context = new MappingContext.Factory().getContext();

            List<Arbeidsforhold> arbeidsforholdRequest =
                    nonNull(bestilling.getAareg().get(0)) ? mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class, context) : emptyList();
            List<ArbeidsforholdResponse> eksisterendeArbeidsforhold = aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), env);

            List<Arbeidsforhold> arbeidsforhold = AaregMergeUtil.merge(
                    arbeidsforholdRequest,
                    eksisterendeArbeidsforhold,
                    dollyPerson.getHovedperson(), isOpprettEndre);

            arbeidsforhold.forEach(arbforhold -> {
                AaregOpprettRequest aaregOpprettRequest = AaregOpprettRequest.builder()
                        .arbeidsforhold(arbforhold)
                        .environments(singletonList(env))
                        .build();
                appendResult(aaregConsumer.opprettArbeidsforhold(aaregOpprettRequest).getStatusPerMiljoe(), arbforhold.getArbeidsforholdID(), result);
            });

            if (arbeidsforhold.isEmpty()) {
                appendResult(singletonMap(env, "OK"), "0", result);
            }
        } catch (RuntimeException e) {
            log.error("Innsending til Aareg feilet: ", e);
            Map<String, String> status = new HashMap<>();
            status.put(env, errorStatusDecoder.decodeRuntimeException(e));
            appendResult(status, "1", result);
        }
    }

    private void sendAmelding(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, StringBuilder result, String env) {
        try {
            Map<String, AMeldingDTO> dtoMaanedMap = new HashMap<>();

            Set<String> orgnumre = bestilling.getAareg().get(0).getAmelding().stream()
                    .map(RsAmeldingRequest::getArbeidsforhold)
                    .flatMap(Collection::stream)
                    .map(RsArbeidsforholdAareg::getArbeidsgiver)
                    .map(RsArbeidsgiver::getOrgnummer)
                    .collect(Collectors.toSet());
            List<OrganisasjonDTO> organisasjoner = organisasjonServiceConsumer.getOrganisasjoner(orgnumre, env);
            bestilling.getAareg().get(0).getAmelding().forEach(amelding -> {


                Map<String, String> opplysningspliktig = new HashMap<>();
                orgnumre.forEach(orgnummer -> opplysningspliktig.put(orgnummer,
                        organisasjoner.stream()
                                .filter(Objects::nonNull)
                                .filter(org -> nonNull(org.getOrgnummer()) && org.getOrgnummer().equals(orgnummer))
                                .map(OrganisasjonDTO::getJuridiskEnhet)
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException(String.format("Juridisk enhet for organisasjon: %s ikke funnet i miljø: %s", orgnummer, env)))));
                MappingContext context = new MappingContext.Factory().getContext();

                context.setProperty("personIdent", dollyPerson.getHovedperson());
                context.setProperty("arbeidsforholdstype", bestilling.getAareg().get(0).getArbeidsforholdstype());
                context.setProperty("opplysningsPliktig", opplysningspliktig);

                dtoMaanedMap.put(amelding.getMaaned(), mapperFacade.map(amelding, AMeldingDTO.class, context));
            });

            Map<String, ResponseEntity<Void>> response = ameldingConsumer.putAmeldingList(dtoMaanedMap, env);
            response.forEach((maaned, resp) -> {
                if (resp.getStatusCode().is2xxSuccessful()) {
                    if (result.indexOf("OK") == NOT_FOUND) {
                        appendResult((singletonMap(env, "OK")), "1", result);
                        saveTransaksjonId(resp, maaned, dollyPerson.getHovedperson(), progress.getBestilling().getId(), env);
                    }
                } else {
                    if (result.indexOf(resp.getStatusCode().getReasonPhrase()) == NOT_FOUND) {
                        appendResult((singletonMap(env, resp.getStatusCode().getReasonPhrase())), "1", result);
                    }
                }
            });
        } catch (RuntimeException e) {
            log.error("Innsending til A-melding service feilet: ", e);
            Map<String, String> status = new HashMap<>();
            status.put(env, errorStatusDecoder.decodeRuntimeException(e));
            appendResult(status, "1", result);
        }
    }

    private void saveTransaksjonId(ResponseEntity<Void> response, String maaned, String ident, Long bestillingId, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(AmeldingTransaksjon.builder()
                                .id(nonNull(response.getHeaders().get("id")) && !response.getHeaders().get("id").isEmpty()
                                        ? response.getHeaders().get("id").get(0)
                                        : null)
                                .maaned(maaned)
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(AAREG.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for aareg", e);
        }
        return null;
    }

    private static StringBuilder appendResult(Map<String, String> result, String arbeidsforholdId, StringBuilder builder) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
            builder.append(',')
                    .append(entry.getKey())
                    .append(": arbforhold=")
                    .append(arbeidsforholdId)
                    .append('$')
                    .append(entry.getValue().replaceAll(",", "&").replaceAll(":", "="));
        }
        return builder;
    }
}