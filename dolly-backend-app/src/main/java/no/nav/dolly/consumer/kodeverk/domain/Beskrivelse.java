package no.nav.dolly.consumer.kodeverk.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beskrivelse {

    private String term;
    private String tekst;
}