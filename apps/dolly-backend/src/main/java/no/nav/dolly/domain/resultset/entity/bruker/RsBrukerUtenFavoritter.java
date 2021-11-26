package no.nav.dolly.domain.resultset.entity.bruker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBrukerUtenFavoritter {

    private String brukerId;
    private String brukernavn;
    private Brukertype brukertype;
    private String epost;
    private String navIdent;
}