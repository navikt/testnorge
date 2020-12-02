package no.nav.registre.tpsidentservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TpsStatus {
    List<StatusPaaIdenter> statusPaaIdenter;

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    static class StatusPaaIdenter {
        String ident;
        Set<String> env;
    }

    @JsonIgnore
    public String getIdent() {
        return statusPaaIdenter.get(0).ident;
    }

    @JsonIgnore
    public Set<String> getMiljoer() {
        return statusPaaIdenter.get(0).env;
    }
}
