package no.nav.registre.sdForvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
public class OrganisasjonStatusMap {

    @JsonProperty
    private String miljo;

    @JsonProperty
    private Map<String, OrganisasjonStatus> map = new HashMap<>();

    public OrganisasjonStatusMap(String miljo, List<OrganisasjonStatus> statuses) {
        this.miljo = miljo;
        statuses.forEach(status -> map.put(status.getOrgnummer(), status));
    }

    @JsonProperty
    public Integer getSize(){
        return map.size();
    }
}
