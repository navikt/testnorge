package no.nav.dolly.domain.resultset.entity.bruker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBrukerAndGruppeId {

    private String brukerId;
    private String brukernavn;
    private String epost;
    private String navIdent;
    private List<String> favoritter;

    public List<String> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new ArrayList<>();
        }
        return favoritter;
    }
}