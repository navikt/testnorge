package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.SyntArbeidsforholdConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkService {
    private final SyntArbeidsforholdConsumer syntArbeidsforholdConsumer;

    public Mono<List<Arbeidsforhold>> genererStart(LocalDate startdato, String virksomhetsnummer, String opplysningspliktig, String ident) {
        var response = syntArbeidsforholdConsumer.genererStartArbeidsforhold(startdato);
        return response.map(items -> items.stream().map(item -> new Arbeidsforhold(
                item,
                UUID.randomUUID().toString(),
                virksomhetsnummer,
                opplysningspliktig,
                ident
        )).collect(Collectors.toList()));
    }

    public Mono<List<List<Arbeidsforhold>>> genererHistorikk(List<Arbeidsforhold> previous, LocalDate kaldermnd, int antall) {
        var responseList = syntArbeidsforholdConsumer.genererArbeidsforholdHistorikk(
                previous.stream()
                        .map(arb -> arb.toSynt(antall, kaldermnd))
                        .collect(Collectors.toList())
        );
        return responseList.map(list -> convert(previous, list));
    }


    private List<List<Arbeidsforhold>> convert(List<Arbeidsforhold> previous, List<List<ArbeidsforholdResponse>> responses){
        var arbeidsforholds = new ArrayList<List<Arbeidsforhold>>();

        for(int index = 0; index < responses.size(); index++) {

            var previousArbeidsforhold = previous.get(index);

            var newArbeidsforholds = responses.get(index).stream().map(value -> new Arbeidsforhold(
                    value,
                    previousArbeidsforhold.getId(),
                    previousArbeidsforhold.getVirksomhetsnummer(),
                    previousArbeidsforhold.getOpplysningspliktig(),
                    previousArbeidsforhold.getIdent()
            )).collect(Collectors.toList());

            arbeidsforholds.add(newArbeidsforholds);
        }
        return arbeidsforholds;
    }

}
