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
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
                    .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        // Etterlatte tilbyr pt ikke sletting
    }

    private ClientFuture futurePersist(BestillingProgress progress, HttpStatus httpStatus) {

        var status = httpStatus.is2xxSuccessful() ? "OK" :
                "Feil= %d %s".formatted(httpStatus.value(),httpStatus.getReasonPhrase());

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
                .map(personBolk -> VedtakRequestDTO.builder()
                        .barn(personBolk.getPerson().getForelderBarnRelasjon().stream()
                                .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                .toList())
                        .avdoed(!personBolk.getPerson().getDoedsfall().isEmpty() ? personBolk.getIdent() :
                                getRelatertVedSivilstand(personBolk.getPerson()))
                        .gjenlevende(personBolk.getPerson().getDoedsfall().isEmpty() ? personBolk.getIdent() :
                                getRelatertVedSivilstand(personBolk.getPerson()))
                        .build())
                .reduce(new VedtakRequestDTO(), (a, b) -> b);
    }

    private static String getRelatertVedSivilstand(PdlPerson.Person person) {

        return person.getSivilstand().stream()
                .filter(sivilstand -> sivilstand.isGift() || sivilstand.isGjenlevende())
                .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                .findAny().orElse(null);
    }
}