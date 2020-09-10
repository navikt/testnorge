package no.nav.dolly.consumer.kodeverk.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Beskrivelse {

    private String term;
    private String tekst;
}