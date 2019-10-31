package no.nav.registre.aareg.domain;

import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;

public final class CommonKeys {

    public static final String MDC_CONSUMER_ID_KEY = "consumerId";
    public static final String MDC_CALL_ID_KEY = "callId";
    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String HEADER_NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";
    public static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";

    public static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_LIST_STRING = new ParameterizedTypeReference<>() {
    };

    public static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE_LIST_LONG = new ParameterizedTypeReference<>() {
    };

    public static final ParameterizedTypeReference<List<Map>> RESPONSE_TYPE_LIST_MAP = new ParameterizedTypeReference<>() {
    };

    public static final ParameterizedTypeReference<List<RsAaregOpprettRequest>> RESPONSE_TYPE_LIST_AAREG_REQUEST = new ParameterizedTypeReference<>() {
    };

    private CommonKeys() {
    }
}
