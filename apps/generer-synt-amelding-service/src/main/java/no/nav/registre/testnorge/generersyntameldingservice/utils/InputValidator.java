package no.nav.registre.testnorge.generersyntameldingservice.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;

@Component
public class InputValidator {

    private InputValidator(){}

    private static final List<String> ARBEIDSFORHOLD_TYPER = Stream.of(ArbeidsforholdType.values())
            .map(ArbeidsforholdType::getBeskrivelse)
            .collect(Collectors.toList());

    public static void validateInput(String arbeidsforholdType){
        if (!ARBEIDSFORHOLD_TYPER.contains(arbeidsforholdType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Ikke en gyldig arbeidsforhold type. Gyldige arbeidsforhold typer: %s.", ARBEIDSFORHOLD_TYPER.toString()));
        }
    }
}
