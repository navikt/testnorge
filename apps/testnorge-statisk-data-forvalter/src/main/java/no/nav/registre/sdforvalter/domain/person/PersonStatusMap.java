package no.nav.registre.sdforvalter.domain.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
public class PersonStatusMap {
    @JsonProperty
    Map<String, PersonStatus> map = new HashMap<>();

    public PersonStatusMap(List<PersonStatus> list){
        list.forEach(personStatus -> map.put(personStatus.getFnr(), personStatus));
    }
}
