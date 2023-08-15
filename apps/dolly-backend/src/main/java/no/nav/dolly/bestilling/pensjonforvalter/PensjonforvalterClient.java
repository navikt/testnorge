package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
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
    private final PdlPersonConsumer pdlPersonConsumer;
    private final PdlDataConsumer pdlDataConsumer;
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

        log.info("Pensjon 1/ {}", dollyPerson.getIdent());
        if (IdentTypeUtil.getIdentType(dollyPerson.getIdent()) == IdentType.NPID) {
            return Flux.empty();
        }

        log.info("Pensjon 2/ {}", dollyPerson.getIdent());
        var bestilteMiljoer = new AtomicReference<>(bestilling.getEnvironments().stream()
                .map(miljoe -> miljoe.equals("q4") ? "q1" : miljoe)
                .collect(Collectors.toSet()));

        var bestillingId = progress.getBestilling().getId();

        return Flux.from(pensjonforvalterConsumer.getMiljoer())
                .flatMap(tilgjengeligeMiljoer -> {
                    log.info("Pensjon 3/ {}", dollyPerson.getIdent());
                    bestilteMiljoer.set(bestilteMiljoer.get().stream()
                            .filter(tilgjengeligeMiljoer::contains)
                            .collect(Collectors.toSet()));

                    return Flux.just(bestilling)
                            .doOnNext(bestilling1 -> log.info("Pensjon 4/ {}", dollyPerson.getIdent()))
                            .filter(bestilling1 -> isOppdateringRequired(bestilling1, progress))
                            .doOnNext(bestilling1 -> log.info("Pensjon 5/ {}", dollyPerson.getIdent()))
                            .doOnNext(bestilling1 -> {
                                if (!dollyPerson.isOrdre()) {
                                    transactionHelperService.persister(progress, BestillingProgress::setPensjonforvalterStatus,
                                            prepInitStatus(tilgjengeligeMiljoer));
                                }
                            })
                            .flatMap(bestilling1 -> getIdenterRelasjoner(dollyPerson.getIdent())
                                    .collectList()
                                    .map(this::getPersonData)
                                    .flatMapMany(Flux::collectList)
                                    .map(relasjoner -> Flux.concat(
                                            opprettPersoner(dollyPerson.getIdent(), tilgjengeligeMiljoer, relasjoner)
                                                    .map(response -> PENSJON_FORVALTER + decodeStatus(response, dollyPerson.getIdent())),

                                            lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer.get())
                                                    .map(response -> TP_FORHOLD + decodeStatus(response, dollyPerson.getIdent())),

                                            lagreAlderspensjon(
                                                    bestilling1.getPensjonforvalter(),
                                                    relasjoner,
                                                    dollyPerson.getIdent(),
                                                    bestilteMiljoer.get(),
                                                    isOpprettEndre,
                                                    bestillingId)
                                                    .map(response -> PEN_ALDERSPENSJON + decodeStatus(response, dollyPerson.getIdent())),

                                            lagreInntekt(bestilling1.getPensjonforvalter(), dollyPerson, bestilteMiljoer.get())
                                                    .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, dollyPerson.getIdent())))))
                            .flatMap(Flux::from)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining("$"));
                })
                .map(status -> futurePersist(dollyPerson, progress, status));
    }

    private boolean isOppdateringRequired(RsDollyUtvidetBestilling bestilling, BestillingProgress progress) {

        var status = transactionHelperService.getProgress(progress, BestillingProgress::getPensjonforvalterStatus);
        log.info("Status: {} , isBlank(status): {}, nonNull(pensjon): {}", status, isBlank(status), nonNull(bestilling.getPensjonforvalter()));
        return isBlank(status) || nonNull(bestilling.getPensjonforvalter());
    }

    private String prepInitStatus(Set<String> miljoer) {

        return PENSJON_FORVALTER +
                miljoer.stream()
                        .map(miljo -> String.format("%s:%s", miljo, ErrorStatusDecoder.getInfoVenter(SYSTEM)))
                        .collect(Collectors.joining(","));
    }

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress, String status) {

        return () -> {
            if (!dollyPerson.isOrdre()) {
                transactionHelperService.persister(progress, BestillingProgress::setPensjonforvalterStatus, status);
            }
            return progress;
        };
    }

    private Flux<String> getIdenterRelasjoner(String ident) {

        return Flux.concat(Flux.just(ident),
                        getPersonData(List.of(ident))
                                .flatMap(person -> Flux.fromStream(Stream.of(
                                                person.getPerson().getSivilstand().stream()
                                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getForelderBarnRelasjon().stream()
                                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getFullmakt().stream()
                                                        .map(FullmaktDTO::getMotpartsPersonident))
                                        .flatMap(Function.identity()))),
                        pdlDataConsumer.getPersoner(List.of(ident))
                                .flatMap(person -> Flux.fromIterable(person.getRelasjoner())
                                        .filter(relasjon -> relasjon.getRelasjonType() != RelasjonType.GAMMEL_IDENTITET)
                                        .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                        .map(PersonDTO::getIdent)))
                .distinct();
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

    private Flux<PensjonforvalterResponse> opprettPersoner(String hovedperson, Set<String> miljoer,
                                                           List<PdlPersonBolk.PersonBolk> relasjoner) {

        return Flux.fromIterable(relasjoner)
                .map(person -> mapperFacade.map(person, PensjonPersonRequest.class))
                .flatMap(request -> pensjonforvalterConsumer.opprettPerson(request, miljoer)
                        .filter(response -> hovedperson.equals(request.getFnr())));
    }

    private Flux<PensjonforvalterResponse> lagreAlderspensjon(PensjonData pensjonData, List<PdlPersonBolk.PersonBolk> relasjoner,
                                                              String ident, Set<String> miljoer,
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

                return pensjonforvalterConsumer.lagreAlderspensjon(alderspensjonRequest)
                        .map(response -> {
                            response.getStatus().forEach(status -> {
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
                                                        Set<String> miljoer) {

        if (nonNull(pensjonData) && nonNull(pensjonData.getInntekt())) {
            var poppInntektRequest = mapperFacade.map(pensjonData.getInntekt(), PensjonPoppInntektRequest.class);
            poppInntektRequest.setFnr(dollyPerson.getIdent());

            return pensjonforvalterConsumer.lagreInntekter(poppInntektRequest, miljoer);

        } else {
            return Flux.empty();
        }
    }

    private Mono<PensjonforvalterResponse> lagreTpForhold(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer) {

        return nonNull(pensjonData) && !pensjonData.getTp().isEmpty() ?
                Flux.fromIterable(pensjonData.getTp())
                        .map(tp -> {

                            var context = new MappingContext.Factory().getContext();
                            context.setProperty("ident", dollyPerson.getIdent());
                            context.setProperty("miljoer", miljoer);

                            var tpForholdRequest = mapperFacade.map(tp, PensjonTpForholdRequest.class, context);
                            return pensjonforvalterConsumer.lagreTpForhold(tpForholdRequest)
                                    .flatMap(forholdSvar -> {
                                                log.info("Lagret TP-forhold {}", forholdSvar);
                                                return Flux.fromIterable(tp.getYtelser())
                                                        .flatMap(ytelse -> {
                                                            context.setProperty("ordning", tp.getOrdning());
                                                            PensjonTpYtelseRequest pensjonTpYtelseRequest = mapperFacade.map(ytelse, PensjonTpYtelseRequest.class, context);
                                                            return pensjonforvalterConsumer.lagreTpYtelse(pensjonTpYtelseRequest);
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
            log.error("Feilet å konvertere transaksjonsId for pensjonForvalter", e);
        }
        return null;
    }
}