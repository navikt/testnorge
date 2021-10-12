package no.nav.pdl.forvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbPerson {

    @Id
    private Long id;

    private LocalDateTime sistOppdatert;
    private String ident;

    private String fornavn;
    private String mellomnavn;
    private String etternavn;

    private PersonDTO person;

    private List<DbRelasjon> relasjoner;

    @JsonIgnore
    private List<DbAlias> alias;

    public List<DbRelasjon> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    public List<DbAlias> getAlias() {
        if (isNull(alias)) {
            alias = new ArrayList<>();
        }
        return alias;
    }
}