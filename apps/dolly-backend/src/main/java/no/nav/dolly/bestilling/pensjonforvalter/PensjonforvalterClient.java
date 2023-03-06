package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
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
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.BooleanUtils;
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
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final PersonServiceConsumer personServiceConsumer;

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

        if (IdentTypeUtil.getIdentType(dollyPerson.getIdent()) == IdentType.NPID) {
            return Flux.empty();
        }

        var bestilteMiljoer = new AtomicReference<>(bestilling.getEnvironments().stream()
                .map(miljoe -> miljoe.equals("q4") ? "q1" : miljoe)
                .collect(Collectors.toSet()));

        var bestillingId = progress.getBestilling().getId();

        return Flux.from(pensjonforvalterConsumer.getMiljoer()
                        .flatMap(tilgjengeligeMiljoer -> {

                            if (!dollyPerson.isOrdre()) {
                                transactionHelperService.persister(progress, BestillingProgress::setPensjonforvalterStatus,
                                        prepInitStatus(tilgjengeligeMiljoer));
                            }
                            bestilteMiljoer.set(bestilteMiljoer.get().stream()
                                    .filter(tilgjengeligeMiljoer::contains)
                                    .collect(Collectors.toSet()));

                            return pensjonforvalterConsumer.getAccessToken()
                                    .flatMapMany(token -> getIdenterRelasjoner(dollyPerson.getIdent())
                                            .collectList()
                                            .map(this::getPersonData)
                                            .flatMap(Flux::collectList)
                                            .map(relasjoner -> Flux.concat(
                                                    opprettPersoner(dollyPerson.getIdent(), tilgjengeligeMiljoer,
                                                            relasjoner, token)
                                                            .map(response -> PENSJON_FORVALTER + decodeStatus(response, dollyPerson.getIdent())),

                                                    lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer.get(), token)
                                                            .map(response -> TP_FORHOLD + decodeStatus(response, dollyPerson.getIdent())),

                                                    lagreAlderspensjon(
                                                            bestilling.getPensjonforvalter(),
                                                            relasjoner,
                                                            dollyPerson.getIdent(),
                                                            bestilteMiljoer.get(),
                                                            token,
                                                            isOpprettEndre,
                                                            bestillingId)
                                                            .map(response -> PEN_ALDERSPENSJON + decodeStatus(response, dollyPerson.getIdent())),

                                                    lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer.get(), token)
                                                            .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, dollyPerson.getIdent())))))
                                    .flatMap(Flux::from)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining("$"));
                        }))
                .map(status -> futurePersist(dollyPerson, progress, status));
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
                        .map(person -> Stream.of(
                                        person.getPerson().getSivilstand().stream()
                                                .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                                .filter(Objects::nonNull),
                                        person.getPerson().getForelderBarnRelasjon().stream()
                                                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                .filter(Objects::nonNull),
                                        person.getPerson().getFullmakt().stream()
                                                .map(FullmaktDTO::getMotpartsPersonident))
                                .flatMap(Function.identity()))
                        .flatMap(Flux::fromStream)
                        .flatMap(relasjon -> personServiceConsumer.getPdlSyncReady(relasjon)
                                .filter(BooleanUtils::isTrue)
                                .map(exists -> relasjon)));
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
            poppInntektRequest.setFnr(dollyPerson.getIdent());

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
                            context.setProperty("ident", dollyPerson.getIdent());
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
            log.error("Feilet å konvertere transaksjonsId for pensjonForvalter", e);
        }
        return null;
    }
}