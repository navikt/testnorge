package no.nav.registre.testnav.geografiskekodeverkservice.domain;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class ValidationErrorResponse {
    List<Violation> violations = new ArrayList<>();
}
