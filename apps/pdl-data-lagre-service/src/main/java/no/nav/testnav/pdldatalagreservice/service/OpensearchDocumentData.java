package no.nav.testnav.pdldatalagreservice.service;

import lombok.Value;

import java.util.Map;

@Value
public class OpensearchDocumentData {
    String index;
    String identifikator;
    Map<String, Object> dokumentAsMap;

    boolean isTombstone() {
        return dokumentAsMap == null;
    }
}
