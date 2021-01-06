package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Line {
    String orgnummer;
    String enhetstype;
    String value;
    String miljo;
    String uuid;
    boolean updatable;
}
