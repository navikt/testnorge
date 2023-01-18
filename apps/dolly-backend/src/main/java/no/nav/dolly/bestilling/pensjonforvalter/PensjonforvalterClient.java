package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarsel;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(7)
@RequiredArgsConstructor
public class PensjonforvalterClient implements ClientRegister {

    private static final String SYSTEM = "PESYS";
    private static final String PENSJON_FORVALTER = "PensjonForvalter#";
    private static final String POPP_INNTEKTSREGISTER = "PoppInntekt#";
    private static final String TP_FORHOLD = "TpForhold#";
    private static final String PEN_ALDERSPENSJON = "AP#";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final DollyPersonCache dollyPersonCache;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;

    public static PensjonforvalterResponse mergePensjonforvalterResponses(List<PensjonforvalterResponse> responser) {

        var status = new HashMap<String, PensjonforvalterResponse.Response>();
        responser.stream()
                .forEach(respons -> respons.getStatus().stream()
                        .forEach(detalj -> {
                            if (detalj.getResponse().isResponse2xx()) {
                                status.putIfAbsent(detalj.getMiljo(), detalj.getResponse());
                            } else {
                                status.put(detalj.getMiljo(), detalj.getResponse());
                            }
                        }));

        return PensjonforvalterResponse.builder()
                .status(status.entrySet().stream()
                        .map(detalj -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo(detalj.getKey())
                                .response(detalj.getValue())
                                .build())
                        .toList())
                .build();
    }

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var bestilteMiljoer = new HashSet<>(bestilling.getEnvironments()).stream()
                .map(miljoe -> miljoe.equals("q4") ? "q1" : miljoe)
                .collect(Collectors.toSet());
        var tilgjengeligeMiljoer = Optional.ofNullable(pensjonforvalterConsumer.getMiljoer())
                .orElse(Set.of("q1", "q2"));
        bestilteMiljoer.retainAll(tilgjengeligeMiljoer);
        var bestillingId = progress.getBestilling().getId();

        if (!dollyPerson.isOpprettetIPDL()) {
            progress.setPensjonforvalterStatus(PENSJON_FORVALTER +
                    tilgjengeligeMiljoer.stream()
                            .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarsel(SYSTEM))))
                            .collect(Collectors.joining(",")));

        } else if (nonNull(bestilling.getPensjonforvalter()) && bestilteMiljoer.isEmpty()) {
            progress.setPensjonforvalterStatus(new StringBuilder()
                    .append(PENSJON_FORVALTER)
                    .append("NA:Feil= Bestilling ble ikke sendt til Pensjonsforvalter (POPP/TP) da tilgjengelig(e) miljø(er) [")
                    .append(String.join(";", tilgjengeligeMiljoer))
                    .append("] ikke er valgt")
                    .toString());

        } else {
            dollyPersonCache.fetchIfEmpty(dollyPerson);

            progress.setPensjonforvalterStatus(
                    pensjonforvalterConsumer.getAccessToken()
                            .flatMapMany(token -> getIdenterRelasjoner(dollyPerson.getHovedperson())
                                    .flatMap(this::getPersonData)
                                    .collectList()
                                    .map(relasjoner -> Flux.concat(opprettPersoner(dollyPerson.getHovedperson(), tilgjengeligeMiljoer,
                                                    relasjoner, token)
                                                    .map(response -> PENSJON_FORVALTER + decodeStatus(response, dollyPerson.getHovedperson())),

                                            lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, token)
                                                    .map(response -> TP_FORHOLD + decodeStatus(response, dollyPerson.getHovedperson())),

                                            lagreAlderspensjon(
                                                    bestilling.getPensjonforvalter(),
                                                    relasjoner,
                                                    dollyPerson.getHovedperson(),
                                                    bestilteMiljoer,
                                                    token,
                                                    isOpprettEndre,
                                                    bestillingId)
                                                    .map(response -> PEN_ALDERSPENSJON + decodeStatus(response, dollyPerson.getHovedperson())),

                                            lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, token)
                                                    .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, dollyPerson.getHovedperson()))
                                    )))
                            .flatMap(Flux::from)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining("$"))
                            .block());
        }
        return Flux.empty();
    }

    private Flux<List<String>> getIdenterRelasjoner(String ident) {

        return getPersonData(List.of(ident))
                .map(person -> Stream.of(List.of(person.getIdent()),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList());
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    @Override
    public void release(List<String> identer) {

        // Pensjonforvalter / POPP støtter pt ikke sletting

        pensjonforvalterConsumer.sletteTpForhold(identer);
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getPensjonforvalter()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getPensjonforvalterStatus()));
    }

    private Flux<PensjonforvalterResponse> opprettPersoner(String hovedperson, Set<String> miljoer,
                                                           List<PdlPersonBolk.PersonBolk> relasjoner,
                                                           AccessToken token) {

        return Flux.fromIterable(relasjoner)
                .map(person -> mapperFacade.map(person, PensjonPersonRequest.class))
                .flatMap(request -> pensjonforvalterConsumer.opprettPerson(request, miljoer, token)
                        .filter(response -> hovedperson.equals(request.getFnr())));
    }

    private Flux<PensjonforvalterResponse> lagreAlderspensjon(PensjonData pensjonData, List<PdlPersonBolk.PersonBolk> relasjoner,
                                                              String ident, Set<String> miljoer, AccessToken token,
                                                              boolean isOpprettEndre, Long bestillingId) {

        if (nonNull(pensjonData) && nonNull(pensjonData.getAlderspensjon())) {

            var opprettIMiljoer = miljoer.stream()
                    .filter(miljoe -> isOpprettEndre ||
                            !transaksjonMappingService.existAlready(PEN_AP, ident, miljoe))
                    .toList();

            if (!opprettIMiljoer.isEmpty()) {
                var context = new MappingContext.Factory().getContext();

                context.setProperty("ident", ident);
                context.setProperty("miljoer", opprettIMiljoer);
                context.setProperty("relasjoner", relasjoner);
                var alderspensjonRequest = mapperFacade.map(pensjonData.getAlderspensjon(),
                        AlderspensjonRequest.class, context);

                return pensjonforvalterConsumer.lagreAlderspensjon(alderspensjonRequest, token)
                        .map(response -> {
                            response.getStatus().forEach(status -> {
                                log.info("Mottatt status for {} fra miljø {} fra Pensjon-Testdata-Facade: {}", ident, status.getMiljo(), status.getResponse());
                                if (status.getResponse().isResponse2xx()) {
                                    saveAPTransaksjonId(ident, status.getMiljo(), bestillingId, pensjonData.getAlderspensjon());
                                }
                            });
                            return response;
                        });
            }
        }
        return Flux.empty();
    }

    private void saveAPTransaksjonId(String ident, String miljoe, Long bestillingId, PensjonData.Alderspensjon alderspensjon) {
        log.info("lagrer transaksjon for {} i {} ", ident, miljoe);

        var jsonData = Map.of(
                "iverksettelsesdato", alderspensjon.getIverksettelsesdato().format(ISO_LOCAL_DATE),
                "uttaksgrad", alderspensjon.getUttaksgrad()
        );

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(jsonData))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(PEN_AP.name())
                        .build());
    }

    private Flux<PensjonforvalterResponse> lagreInntekt(PensjonData pensjonData, DollyPerson dollyPerson,
                                                        Set<String> miljoer, AccessToken token) {

        if (nonNull(pensjonData) && nonNull(pensjonData.getInntekt())) {
            var poppInntektRequest = mapperFacade.map(pensjonData.getInntekt(), PensjonPoppInntektRequest.class);
            poppInntektRequest.setFnr(dollyPerson.getHovedperson());

            return pensjonforvalterConsumer.lagreInntekter(poppInntektRequest, miljoer, token);

        } else {
            return Flux.empty();
        }
    }

    private Mono<PensjonforvalterResponse> lagreTpForhold(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, AccessToken token) {

        return nonNull(pensjonData) && !pensjonData.getTp().isEmpty() ?
                Flux.fromIterable(pensjonData.getTp())
                        .map(tp -> {

                            var context = new MappingContext.Factory().getContext();
                            context.setProperty("ident", dollyPerson.getHovedperson());
                            context.setProperty("miljoer", miljoer);

                            var tpForholdRequest = mapperFacade.map(tp, PensjonTpForholdRequest.class, context);
                            return pensjonforvalterConsumer.lagreTpForhold(tpForholdRequest, token)
                                    .flatMap(forholdSvar -> {
                                                log.info("Lagret TP-forhold {}", forholdSvar);
                                                return Flux.fromIterable(tp.getYtelser())
                                                        .flatMap(ytelse -> {
                                                            context.setProperty("ordning", tp.getOrdning());
                                                            PensjonTpYtelseRequest pensjonTpYtelseRequest = mapperFacade.map(ytelse, PensjonTpYtelseRequest.class, context);
                                                            return pensjonforvalterConsumer.lagreTpYtelse(pensjonTpYtelseRequest, token);
                                                        });
                                            }
                                    );
                        })
                        .flatMap(Flux::from)
                        .collectList()
                        .map(PensjonforvalterClient::mergePensjonforvalterResponses)
                :
                Mono.empty();

    }

    private String decodeStatus(PensjonforvalterResponse response, String ident) {

        log.info("Mottatt status på {} fra Pensjon-Testdata-Facade: {}", ident, response);

        if (response.getStatus().isEmpty()) {
            return "NA:EMPTY_RESPONSE";
        }

        return response.getStatus().stream()
                .map(entry -> String.format("%s:%s", entry.getMiljo(),
                        entry.getResponse().isResponse2xx() ? "OK" :
                                ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getErrorText(
                                        HttpStatus.valueOf(entry.getResponse().getHttpStatus().getStatus()),
                                        entry.getResponse().getMessage()))))
                .collect(Collectors.joining(","));
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for dokarkiv", e);
        }
        return null;
    }
}