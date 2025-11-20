package no.nav.testnav.pdllagreservice.dto;

import lombok.Value;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

@Value
public class OpensearchDocumentData {
    String index;
    String identifikator;
    Map<String, Object> dokumentAsMap;

    public boolean isTombstone() {
        return isNull(dokumentAsMap);
    }
}
