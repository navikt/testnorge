package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import no.nav.testnav.libs.dto.aareg.v1.Person;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsSyntPerson {

    private String ident;
    private String identtype;

    public Person toPerson() {
        return Person.builder().offentligIdent(ident).build();
    }
}
