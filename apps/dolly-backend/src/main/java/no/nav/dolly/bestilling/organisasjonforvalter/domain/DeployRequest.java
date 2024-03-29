package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeployRequest {

    private Set<String> orgnumre;
    private Set<String> environments;

    public Set<String> getOrgnumre() {
        return isNull(orgnumre) ? new HashSet<>() : orgnumre;
    }

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }
}