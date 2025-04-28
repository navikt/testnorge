package no.nav.dolly.bestilling.etterlatte;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakRequestDTO;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakResponseDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.FoedselsdatoUtility;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtterlatteClient implements ClientRegister {

    private final TransactionHelperService transactionHelperService;
    private final EtterlatteConsumer etterlatteConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getEtterlatteYtelser().isEmpty()) {

            return Flux.from(getPersoner(dollyPerson.getIdent())
                    .flatMapMany(vedtakRequestDTO -> Flux.fromIterable(bestilling.getEtterlatteYtelser())
                            .flatMap(etterlattYtelse -> {
                                var context = MappingContextUtils.getMappingContext();
                                context.setProperty("etterlattYtelse", etterlattYtelse);
                                var nyVedtakRequest = mapperFacade.map(vedtakRequestDTO, VedtakRequestDTO.class, context);
                                return etterlatteConsumer.opprettVedtak(nyVedtakRequest)
                                        .map(VedtakResponseDTO::getStatus)
                                        .map(HttpStatus::valueOf);
                            })
                    )
                    .collectList()
                    .map(statusList -> statusList.stream()
                            .filter(status -> !status.is2xxSuccessful())
                            .findFirst().orElse(HttpStatus.OK))
                    .map(status -> status.is2xxSuccessful() ? "OK" :
                            "Feil= %d %s".formatted(status.value(), status.getReasonPhrase()))
                    .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        // Etterlatte tilbyr pt ikke sletting
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setEtterlatteStatus, status);
            return progress;
        };
    }

    private Mono<VedtakRequestDTO> getPersoner(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .flatMap(personBolk -> personServiceConsumer.getPdlPersoner(
                        Stream.of(Stream.of(ident),
                                        personBolk.getPerson().getForelderBarnRelasjon().stream()
                                                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                .filter(Objects::nonNull),
                                        personBolk.getPerson().getSivilstand().stream()
                                                .filter(sivilstand -> sivilstand.isGift() || sivilstand.isGjenlevende())
                                                .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                                .filter(Objects::nonNull)
                                )
                                .flatMap(Function.identity())
                                .distinct()
                                .toList()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .map(personBolk -> Map.of(personBolk.getIdent(), personBolk))
                .reduce(new HashMap<String, PdlPersonBolk.PersonBolk>(), (map, element) -> {
                    map.putAll(element);
                    return map;
                })
                .flatMap(personer -> {
                    if (isTrue(FoedselsdatoUtility.isMyndig(personer.get(ident)))) {
                        return Mono.just(VedtakRequestDTO.builder()
                                .barn(personer.get(ident).getPerson().getForelderBarnRelasjon().stream()
                                        .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .distinct()
                                        .toList())
                                .avdoed(getForelderFraForelder(personer, ident, bolk -> !bolk.getPerson().getDoedsfall().isEmpty()))
                                .gjenlevende(getForelderFraForelder(personer, ident, bolk -> bolk.getPerson().getDoedsfall().isEmpty()))
                                .build());
                    } else {
                        return Mono.just(VedtakRequestDTO.builder()
                                .barn(List.of(personer.get(ident).getIdent()))
                                .gjenlevende(getForlderfraBarn(personer, ident,
                                        bolk -> bolk.getPerson().getDoedsfall().isEmpty()))
                                .avdoed(getForlderfraBarn(personer, ident, bolk -> !bolk.getPerson().getDoedsfall().isEmpty()))
                                .build());
                    }
                });
    }

    private static String getForlderfraBarn(HashMap<String, PdlPersonBolk.PersonBolk> personer, String ident, Gjenlevende gjenlevende) {
        return personer.get(ident).getPerson().getForelderBarnRelasjon().stream()
                .filter(PdlPerson.ForelderBarnRelasjon::isForelder)
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .map(personer::get)
                .filter(gjenlevende::apply)
                .map(PdlPersonBolk.PersonBolk::getIdent)
                .findFirst().orElse(null);
    }

    private static String getForelderFraForelder(HashMap<String, PdlPersonBolk.PersonBolk> personer, String ident, Gjenlevende gjenlevende) {

        return personer.get(ident).getPerson().getForelderBarnRelasjon().stream()
                .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .map(personer::get)
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(PdlPerson.Person::getForelderBarnRelasjon)
                .flatMap(Collection::stream)
                .filter(PdlPerson.ForelderBarnRelasjon::isForelder)
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .map(personer::get)
                .filter(gjenlevende::apply)
                .map(PdlPersonBolk.PersonBolk::getIdent)
                .findFirst().orElse(null);
    }
}