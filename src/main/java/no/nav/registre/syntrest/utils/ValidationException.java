package no.nav.registre.syntrest.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationException extends Exception {
    private final List<String> errors;
}
