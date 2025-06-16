package no.nav.dolly.domain.resultset.entity.bruker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBrukerAndClaims {

    private String brukerId;
    private String brukernavn;
    private Brukertype brukertype;
    private String epost;
    private String navIdent;
    private List<String> grupper;
    private List<String> favoritter;
}