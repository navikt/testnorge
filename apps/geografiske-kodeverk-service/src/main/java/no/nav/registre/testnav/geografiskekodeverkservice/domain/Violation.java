package no.nav.registre.testnav.geografiskekodeverkservice.domain;

import lombok.Value;

@Value
public class Violation {
    String fieldName;
    String message;
}
