package no.nav.dolly.bestilling.aareg.amelding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmeldingService {

    private static final String STATUS_ELEMENT = "%s:Amelding$%s";

    private final AmeldingConsumer ameldingConsumer;
    private final MapperFacade mapperFacade;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;

    public Mono<String> sendAmelding(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                     Set<String> miljoer) {

        var orgnumre = bestilling.getAareg().getFirst().getAmelding().stream()
                .map(RsAmeldingRequest::getArbeidsforhold)
                .flatMap(Collection::stream)
                .map(RsArbeidsforholdAareg::getArbeidsgiver)
                .map(RsArbeidsforholdAareg.RsArbeidsgiver::getOrgnummer)
                .collect(Collectors.toSet());

        return Flux.fromIterable(miljoer)
                .map(miljoe -> organisasjonServiceConsumer.getOrganisasjoner(orgnumre, miljoe)
                        .filter(OrganisasjonDTO::isFunnet)
                        .collect(Collectors.toMap(OrganisasjonDTO::getOrgnummer, OrganisasjonDTO::getJuridiskEnhet))
                        .flatMapMany(organisasjon ->
                                prepareAmeldinger(bestilling.getAareg().getFirst(), dollyPerson.getIdent(),
                                        organisasjon, miljoe))
                        .collect(Collectors.joining(",")))
                .flatMap(Flux::from)
                .collect(Collectors.joining(","));
    }

    private Flux<String> prepareAmeldinger(RsAareg aareg, String ident, Map<String, String> organisasjon,
                                           String miljoe) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personIdent", ident);
        context.setProperty("arbeidsforholdstype", aareg.getArbeidsforholdstype());
        context.setProperty("opplysningspliktig", organisasjon);

        return Flux.fromIterable(aareg.getAmelding())
                .map(aamelding -> mapperFacade.map(aamelding, AMeldingDTO.class, context))
                .sort(Comparator.comparing(AMeldingDTO::getKalendermaaned))
                .collectList()
                .flatMapMany(ameldinger -> ameldingConsumer.sendAmeldinger(ameldinger, miljoe)
                        .distinct()
                        .map(status -> STATUS_ELEMENT.formatted(miljoe, status)));
    }
}
