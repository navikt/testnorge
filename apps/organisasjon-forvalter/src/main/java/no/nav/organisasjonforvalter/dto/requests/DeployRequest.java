package no.nav.organisasjonforvalter.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeployRequest {

    private List<String> orgnumre;
    private List<String> environments;

    public List<String> getOrgnumre() {
        if (isNull(orgnumre)) {
            orgnumre = new ArrayList<>();
        }
        return orgnumre;
    }

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }
}
