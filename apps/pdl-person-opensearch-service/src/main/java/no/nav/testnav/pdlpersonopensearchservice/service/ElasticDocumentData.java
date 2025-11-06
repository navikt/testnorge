package no.nav.testnav.pdlpersonopensearchservice.service;

import lombok.Value;

import java.util.Map;

@Value
public class ElasticDocumentData {
    String index;
    String identifikator;
    Map<String, Object> dokumentAsMap;

    boolean isTombstone() {
        return dokumentAsMap == null;
    }
}
