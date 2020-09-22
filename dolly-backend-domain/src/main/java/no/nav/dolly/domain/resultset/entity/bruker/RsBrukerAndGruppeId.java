package no.nav.dolly.domain.resultset.entity.bruker;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
            favoritter = new ArrayList();
        }
        return favoritter;
    }
}