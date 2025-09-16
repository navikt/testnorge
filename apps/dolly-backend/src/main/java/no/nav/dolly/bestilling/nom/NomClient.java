package no.nav.dolly.bestilling.nom;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Service
@RequiredArgsConstructor
public class NomClient implements ClientRegister {

    private final NomConsumer nomConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isFalse(bestilling.getHarNomIdent())) {
            return Mono.empty();
        }

        return personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(PdlPerson.Person::getNavn)
                .flatMap(Flux::fromIterable)
                .filter(navn -> isFalse(navn.getMetadata().getHistorisk()))
                .next()
                .map(navn -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    return mapperFacade.map(navn, NomRessursRequest.class, context);
                })
                .flatMap(nomConsumer::hentRessurs)
    }

    @Override
    public void release(List<String> identer) {

    }
}
