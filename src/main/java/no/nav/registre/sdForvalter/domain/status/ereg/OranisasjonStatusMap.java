package no.nav.registre.sdForvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class OranisasjonStatusMap {

    @JsonProperty
    private String miljo;

    @JsonProperty
    private Map<String, OrganisasjonStatus> map = new HashMap<>();

    public void put(String orgnummer, OrganisasjonStatus status) {
        map.put(orgnummer, status);
    }
}
