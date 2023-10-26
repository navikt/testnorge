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
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSivilstandWrapper;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_UT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonforvalterClient implements ClientRegister {

    private static final String IDENT = "ident";
    private static final String MILJOER = "miljoer";
    private static final String SYSTEM = "PESYS";
    private static final String PENSJON_FORVALTER = "PensjonForvalter#";
    private static final String SAMBOER_REGISTER = "Samboer#";
    private static final String POPP_INNTEKTSREGISTER = "PoppInntekt#";
    private static final String TP_FORHOLD = "TpForhold#";
    private static final String PEN_ALDERSPENSJON = "AP#";
    private static final String PEN_UFORETRYGD = "Ufoer#";
    private static final String PERIODE = "/periode/";
    private static final String TPS_NOT_READY = "Oppretting ikke utført da TPS mangler persondata.";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final PersonServiceConsumer personServiceConsumer;
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

        if (IdentTypeUtil.getIdentType(dollyPerson.getIdent()) == IdentType.NPID) {
            return Flux.empty();
        }

        var bestilteMiljoer = new AtomicReference<>(bestilling.getEnvironments().stream()
                .map(miljoe -> miljoe.equals("q4") ? "q1" : miljoe)
                .collect(Collectors.toSet()));

        var bestillingId = bestilling.getId();

        return Flux.from(pensjonforvalterConsumer.getMiljoer())
                .flatMap(tilgjengeligeMiljoer -> {
                    bestilteMiljoer.set(bestilteMiljoer.get().stream()
                            .filter(tilgjengeligeMiljoer::contains)
                            .collect(Collectors.toSet()));

                    return Flux.just(bestilling)
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
                                    .doOnNext(persondata -> {
                                        if (persondata.isEmpty()) {
                                            log.warn("Persondata for {} gir tom response fra PDL", dollyPerson.getIdent());
                                        }
                                    })
                                    .map(persondata -> Flux.concat(
                                            opprettPersoner(dollyPerson.getIdent(), tilgjengeligeMiljoer, persondata)
                                                    .map(response -> PENSJON_FORVALTER + decodeStatus(response, dollyPerson.getIdent())),

                                            lagreSamboer(dollyPerson.getIdent(), tilgjengeligeMiljoer)
                                                    .map(response -> SAMBOER_REGISTER + decodeStatus(response, dollyPerson.getIdent())),

                                            Flux.just(bestilling1)
                                                    .filter(bestilling2 -> nonNull(bestilling2.getPensjonforvalter()))
                                                    .map(RsDollyUtvidetBestilling::getPensjonforvalter)
                                                    .flatMap(pensjon -> Flux.merge(
                                                            lagreInntekt(pensjon, dollyPerson.getIdent(), bestilteMiljoer.get(),
                                                                    progress.getIsTpsSyncEnv())
                                                                    .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, dollyPerson.getIdent())),

                                                            lagreTpForhold(pensjon, dollyPerson.getIdent(), bestilteMiljoer.get())
                                                                    .map(response -> TP_FORHOLD + decodeStatus(response, dollyPerson.getIdent())),

                                                            lagreAlderspensjon(
                                                                    pensjon,
                                                                    persondata,
                                                                    dollyPerson.getIdent(),
                                                                    bestilteMiljoer.get(),
                                                                    isOpprettEndre,
                                                                    bestillingId,
                                                                    progress.getIsTpsSyncEnv())
                                                                    .map(response -> PEN_ALDERSPENSJON + decodeStatus(response, dollyPerson.getIdent())),

                                                            lagreUforetrygd(
                                                                    pensjon,
                                                                    persondata,
                                                                    dollyPerson.getIdent(),
                                                                    bestilteMiljoer.get(),
                                                                    isOpprettEndre,
                                                                    bestillingId,
                                                                    progress.getIsTpsSyncEnv())
                                                                    .map(response -> PEN_UFORETRYGD + decodeStatus(response, dollyPerson.getIdent()))))))

                                    .flatMap(Flux::from)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining("$")));
                })
                .map(status -> futurePersist(dollyPerson, progress, status));
    }

    private Flux<PensjonforvalterResponse> lagreSamboer(String ident, Set<String> tilgjengeligeMiljoer) {

        return Flux.concat(annulerAlleSamboere(ident, tilgjengeligeMiljoer),
                pdlDataConsumer.getPersoner(List.of(ident))
                        .map(hovedperson -> {
                            var context = new MappingContext.Factory().getContext();
                            context.setProperty(IDENT, ident);
                            return (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                                    .sivilstander(hovedperson.getPerson().getSivilstand())
                                    .build(), List.class, context);
                        })
                        .flatMap(Flux::fromIterable)
                        .flatMap(request -> Flux.fromIterable(tilgjengeligeMiljoer)
                                .flatMap(miljoe -> pensjonforvalterConsumer.lagreSamboer(request, miljoe))
                                .filter(response -> request.getPidBruker().equals(ident))));
    }

    private Flux<PensjonforvalterResponse> annulerAlleSamboere(String ident, Set<String> tilgjengeligeMiljoer) {

        return Flux.fromIterable(tilgjengeligeMiljoer)
                .flatMap(miljoe -> pensjonforvalterConsumer.hentSamboer(ident, miljoe)
                        .flatMap(response -> Flux.merge(Flux.just(response), Flux.fromIterable(response.getSamboerforhold())
                                        .map(PensjonSamboerResponse.Samboerforhold::getPidSamboer)
                                        .flatMap(identSamboer -> pensjonforvalterConsumer.hentSamboer(identSamboer, miljoe)))
                                .flatMap(samboerResponse -> Flux.fromIterable(samboerResponse.getSamboerforhold())
                                        .flatMap(samboer -> pensjonforvalterConsumer.annullerSamboer(samboer.getPidBruker(),
                                                        getPeriodeId(samboer.get_links().getAnnuller().getHref()), miljoe)
                                                .filter(response1 -> samboer.getPidBruker().equals(ident) &&
                                                        response1.getStatus().stream()
                                                                .noneMatch(status -> status.getResponse().getHttpStatus().getStatus() == 200))))));
    }

    private static String getPeriodeId(String lenke) {
        return lenke.substring(lenke.indexOf(PERIODE) + PERIODE.length())
                .replace("/annuller", "");
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

        return personServiceConsumer.getPdlPersoner(identer)
                .doOnNext(bolk -> {
                    if (isNull(bolk.getData()) || bolk.getData().getHentPersonBolk().stream()
                            .anyMatch(personBolk -> isNull(personBolk.getPerson()))) {
                        log.warn("PDL-data mangler for {}, bolkPersoner: {}, ", String.join(", ", identer), bolk);
                    }
                })
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
                                                           List<PdlPersonBolk.PersonBolk> personer) {

        return Flux.fromIterable(personer)
                .map(person -> mapperFacade.map(person, PensjonPersonRequest.class))
                .flatMap(request -> pensjonforvalterConsumer.opprettPerson(request, miljoer)
                        .filter(response -> hovedperson.equals(request.getFnr())));
    }

    private Flux<PensjonforvalterResponse> lagreAlderspensjon(PensjonData pensjonData, List<PdlPersonBolk.PersonBolk> relasjoner,
                                                              String ident, Set<String> miljoer,
                                                              boolean isOpprettEndre, Long bestillingId,
                                                              List<String> isTpsSyncEnv) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasAlderspensjon)
                .map(PensjonData::getAlderspensjon)
                .flatMap(alderspensjon -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> {

                            if (isOpprettEndre || !transaksjonMappingService.existAlready(PEN_AP, ident, miljoe, bestillingId)) {

                                if (isTpsSyncEnv.contains(miljoe)) {

                                    var context = new MappingContext.Factory().getContext();
                                    context.setProperty(IDENT, ident);
                                    context.setProperty(MILJOER, List.of(miljoe));
                                    context.setProperty("relasjoner", relasjoner);
                                    return Flux.just(mapperFacade.map(alderspensjon, AlderspensjonRequest.class, context))
                                            .flatMap(alderspensjonRequest -> pensjonforvalterConsumer.lagreAlderspensjon(alderspensjonRequest)
                                                    .map(response -> {
                                                        response.getStatus().forEach(status -> {
                                                            if (status.getResponse().isResponse2xx()) {
                                                                saveAPTransaksjonId(ident, status.getMiljo(), bestillingId,
                                                                        PEN_AP, alderspensjonRequest);
                                                            }
                                                        });
                                                        return response;
                                                    }));

                                } else {
                                    return getStatus(miljoe, 503, TPS_NOT_READY);
                                }

                            } else {
                                return getStatus(miljoe, 200, "OK");
                            }
                        }));
    }

    private Flux<PensjonforvalterResponse> lagreUforetrygd(PensjonData pensjondata,
                                                           List<PdlPersonBolk.PersonBolk> persondata,
                                                           String ident, Set<String> miljoer, boolean isOpprettEndre,
                                                           Long bestillingId, List<String> isTpsSyncEnv) {

        return Flux.just(pensjondata)
                .filter(PensjonData::hasUforetrygd)
                .map(PensjonData::getUforetrygd)
                .flatMap(uforetrygd -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> {

                            if (isOpprettEndre || !transaksjonMappingService.existAlready(PEN_UT, ident, miljoe, bestillingId)) {

                                if (isTpsSyncEnv.contains(miljoe)) {

                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty(IDENT, ident);
                                    context.setProperty(MILJOER, List.of(miljoe));
                                    context.setProperty("persondata", persondata);
                                    return Flux.just(mapperFacade.map(uforetrygd, PensjonUforetrygdRequest.class, context))
                                            .flatMap(request -> pensjonforvalterConsumer.lagreUforetrygd(request)
                                                    .map(response -> {
                                                        response.getStatus().stream()
                                                                .filter(status -> status.getResponse().isResponse2xx())
                                                                .forEach(status ->
                                                                        saveAPTransaksjonId(ident, status.getMiljo(), bestillingId,
                                                                                PEN_UT, request));
                                                        return response;
                                                    }));

                                } else {
                                    return getStatus(miljoe, 503, TPS_NOT_READY);
                                }

                            } else {
                                return getStatus(miljoe, 200, "OK");
                            }
                        }));
    }

    private static Flux<PensjonforvalterResponse> getStatus(String miljoe, Integer status, String reasonPhrase) {

        return Flux.just(PensjonforvalterResponse.builder()
                .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                        .miljo(miljoe)
                        .response(PensjonforvalterResponse.Response.builder()
                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                        .status(status)
                                        .reasonPhrase(reasonPhrase)
                                        .build())
                                .message(reasonPhrase)
                                .build())
                        .build()))
                .build());
    }

    private void saveAPTransaksjonId(String ident, String miljoe, Long bestillingId, SystemTyper type, Object vedtak) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(vedtak))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(type.name())
                        .build());
    }

    private Flux<PensjonforvalterResponse> lagreInntekt(PensjonData pensjonData, String ident,
                                                        Set<String> miljoer, List<String> isTpsSyncEnv) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasInntekt)
                .map(PensjonData::getInntekt)
                .flatMap(inntekt -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> {

                            if (isTpsSyncEnv.stream().anyMatch(sync -> sync.equals(miljoe))) {

                                var request = mapperFacade.map(inntekt, PensjonPoppInntektRequest.class);
                                request.setFnr(ident);
                                request.setMiljoer(List.of(miljoe));
                                return pensjonforvalterConsumer.lagreInntekter(request);

                            } else {
                                return getStatus(miljoe, 503, TPS_NOT_READY);
                            }
                        }));
    }

    private Mono<PensjonforvalterResponse> lagreTpForhold(PensjonData pensjonData, String ident, Set<String> miljoer) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasTp)
                .map(PensjonData::getTp)
                .flatMap(Flux::fromIterable)
                .map(tp -> {

                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOER, miljoer);

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
                .filter(resultat -> !resultat.isEmpty())
                .map(PensjonforvalterClient::mergePensjonforvalterResponses);
    }

    private String decodeStatus(PensjonforvalterResponse response, String ident) {

        log.info("Mottatt status på {} fra Pensjon-Testdata-Facade: {}", ident, response);

        return response.getStatus().stream()
                .map(entry -> String.format("%s:%s", entry.getMiljo(),
                        entry.getResponse().isResponse2xx() ? "OK" :
                                getError(entry)))
                .collect(Collectors.joining(","));
    }

    private String getError(PensjonforvalterResponse.ResponseEnvironment entry) {

        return ErrorStatusDecoder.encodeStatus(
                entry.getResponse().getMessage().contains("{") ?
                        "Feil: " + entry.getResponse().getMessage().split("\\{")[1].split("}")[0]
                                .replace("message\":", "") :
                        errorStatusDecoder.getErrorText(
                                HttpStatus.valueOf(entry.getResponse().getHttpStatus().getStatus()),
                                entry.getResponse().getMessage())
        );
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