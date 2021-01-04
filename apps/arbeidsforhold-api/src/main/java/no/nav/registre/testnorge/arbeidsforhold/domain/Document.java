package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
public class Document {
    String xml;
    Long id;
}
