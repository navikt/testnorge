package no.nav.dolly.bestilling.arbeidssoekerregisteret;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ArbeidssoekerregisteretClient implements ClientRegister {

    private final ArbeidssoekerregisteretConsumer arbeidssoekerregisteretConsumer;
    private final MapperFacade mapperFacade;


    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var test = Flux.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getArbeidssokerregisteret()))
                .map(soknad -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    return mapperFacade.map(soknad, ArbeidssokerregisteretRequest.class, context);
                })
                .map(arbeidssoekerregisteretConsumer::postArbeidssokerregisteret)
                .flatMap(response -> response.map(arbeidssokerregisteretResponse -> {
                    if (arbeidssokerregisteretResponse.getStatus().is2xxSuccessful()) {
                        return "OK";
                    } else {
                        return "Feil";
                    }
                }));
    }

    @Override
    public void release(List<String> identer) {

    }
}
