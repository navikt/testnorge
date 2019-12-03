package no.nav.dolly.domain.resultset.entity.bestilling;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import springfox.documentation.spring.web.json.Json;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsMalBestilling extends RsDollyBestilling {

    private Integer antallIdenter;
    private List<String> environments;

    private Json tpsf;
    private String opprettFraIdenter;

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList();
        }
        return environments;
    }
}
