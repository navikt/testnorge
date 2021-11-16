package no.nav.dolly.domain.resultset.entity.bruker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBruker {

    private String brukerId;
    private String brukernavn;
    private Brukertype brukertype;
    private String epost;
    private String navIdent;
    private List<RsTestgruppe> favoritter;

    public List<RsTestgruppe> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new ArrayList<>();
        }
        return favoritter;
    }
}