package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreAlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
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
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVenterTekst;
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
    private final TransactionHelperService transactionHelperService;
    private final PersonServiceConsumer personServiceConsumer;
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
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var penMiljoer = new AtomicReference<>(new HashSet<>(bestilling.getEnvironments()).stream()
                .map(miljoe -> miljoe.equals("q4") ? "q1" : miljoe)
                .collect(Collectors.toSet()));

        var bestillingId = progress.getBestilling().getId();

        pensjonforvalterConsumer.getMiljoer()
                        .map(miljoer -> {
                            penMiljoer.set(penMiljoer.get().stream()
                                    .filter(miljoer::contains)
                                    .collect(Collectors.toSet()));
                            progress.setPensjonforvalterStatus(PENSJON_FORVALTER +
                                    miljoer.stream()
                                            .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getInfoVenter(SYSTEM))))
                                            .collect(Collectors.joining(",")));
                            transactionHelperService.persister(progress);
                        });

        return Flux.from(personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                .flatMap(isPresent -> {
                    if (isPresent) {
                        return pensjonforvalterConsumer.getAccessToken()
                                .flatMapMany(token -> getIdenterFamilie(dollyPerson.getHovedperson())
                                        .map(this::getPersonData)
                                        .flatMap(Flux::from)
                                        .map(person -> Flux.concat(pensjonforvalterConsumer.opprettPerson(
                                                                mapperFacade.map(person, OpprettPersonRequest.class), penMiljoer.get(), token)
                                                        .filter(response -> dollyPerson.getHovedperson().equals(person.getIdent()))
                                                        .map(response -> PENSJON_FORVALTER + decodeStatus(response, person.getIdent())),
                                                (dollyPerson.getHovedperson().equals(person.getIdent()) ?
                                                        lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, penMiljoer.get(), token)
                                                                .map(response -> TP_FORHOLD + decodeStatus(response, person.getIdent())) :
                                                        Flux.just("")),
                                                (dollyPerson.getHovedperson().equals(person.getIdent()) ?
                                                        lagreAlderspensjon(
                                                                bestilling.getPensjonforvalter(),
                                                                dollyPerson.getHovedperson(),
                                                                penMiljoer.get(),
                                                                token,
                                                                isOpprettEndre,
                                                                bestillingId)
                                                                .map(response -> PEN_ALDERSPENSJON + decodeStatus(response, person.getIdent())) :
                                                        Flux.just("")),

                                                (dollyPerson.getHovedperson().equals(person.getIdent()) ?
                                                        lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, penMiljoer.get(), token)
                                                                .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, person.getIdent())) :
                                                        Flux.just(""))))
                                        .flatMap(Flux::from))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining("$"));
                    } else {
                        return tilgjengeligeMiljoer.get()
                                .flatMapMany(Flux::fromIterable)
                                .map(miljoe -> String.format("%s:%s", miljoe, encodeStatus(getVarselSlutt(SYSTEM))))
                                .collect(Collectors.joining(","));
                    }
                })
                .map(status -> futurePersist(progress, status)));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setPensjonforvalterStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    private Flux<List<String>> getIdenterFamilie(String ident) {

        return getPersonData(List.of(ident))
                .map(person -> Stream.of(List.of(person.getIdent()),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
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

        return bestilling.getProgresser().stream()
                .allMatch(entry -> isNotBlank(entry.getPensjonforvalterStatus()) &&
                        !entry.getPensjonforvalterStatus().contains(getVenterTekst()));
    }

    private Flux<PensjonforvalterResponse> lagreAlderspensjon(
            PensjonData pensjonData,
            String ident,
            Set<String> miljoer,
            AccessToken token,
            boolean isOpprettEndre,
            Long bestillingId) {

        if (nonNull(pensjonData) && nonNull(pensjonData.getAlderspensjon())) {
            List<String> opprettIMiljoer = null;

            if (!isOpprettEndre) {
                opprettIMiljoer = miljoer.stream()
                        .filter(miljo -> !transaksjonMappingService.existAlready(PEN_AP, ident, miljo))
                        .toList();
            } else {
                opprettIMiljoer = new ArrayList<>(miljoer);
            }

            if (!opprettIMiljoer.isEmpty()) {
                LagreAlderspensjonRequest lagreAlderspensjonRequest = mapperFacade.map(pensjonData.getAlderspensjon(), LagreAlderspensjonRequest.class);
                lagreAlderspensjonRequest.setPid(ident);
                lagreAlderspensjonRequest.setMiljoer(opprettIMiljoer);
                lagreAlderspensjonRequest.setStatsborgerskap("NOR");

                return pensjonforvalterConsumer.lagreAlderspensjon(lagreAlderspensjonRequest, token)
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
            var lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
            lagreInntektRequest.setFnr(dollyPerson.getHovedperson());

            return pensjonforvalterConsumer.lagreInntekter(lagreInntektRequest, miljoer, token);

        } else {
            return Flux.empty();
        }
    }

    private Mono<PensjonforvalterResponse> lagreTpForhold(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, AccessToken token) {

        return nonNull(pensjonData) && !pensjonData.getTp().isEmpty() ?
                Flux.fromIterable(pensjonData.getTp())
                        .map(tp -> {
                            var lagreTpForholdRequest = mapperFacade.map(tp, LagreTpForholdRequest.class);
                            lagreTpForholdRequest.setFnr(dollyPerson.getHovedperson());
                            lagreTpForholdRequest.setMiljoer(miljoer);
                            return pensjonforvalterConsumer.lagreTpForhold(lagreTpForholdRequest, token)
                                    .flatMap(forholdSvar ->
                                            Flux.fromIterable(tp.getYtelser())
                                                    .flatMap(ytelse -> {
                                                        LagreTpYtelseRequest lagreTpYtelseRequest = mapperFacade.map(ytelse, LagreTpYtelseRequest.class);
                                                        lagreTpYtelseRequest.setYtelseType(ytelse.getType());
                                                        lagreTpYtelseRequest.setOrdning(tp.getOrdning());
                                                        lagreTpYtelseRequest.setFnr(dollyPerson.getHovedperson());
                                                        lagreTpYtelseRequest.setMiljoer(miljoer);
                                                        return pensjonforvalterConsumer.lagreTpYtelse(lagreTpYtelseRequest, token);
                                                    })
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
                        entry.getResponse().getHttpStatus().getStatus() == 200 ? "OK" :
                                encodeStatus(errorStatusDecoder.getErrorText(
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
