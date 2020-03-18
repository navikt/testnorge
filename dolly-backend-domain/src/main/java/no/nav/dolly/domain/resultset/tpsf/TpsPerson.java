package no.nav.dolly.domain.resultset.tpsf;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TpsPerson {

    private List<Person> persondetaljer;

    private String hovedperson;
    private List<String> partnere;
    private List<String> barn;
    private List<String> nyePartnereOgBarn;

    public List<String> getPartnere() {
        if (isNull(partnere)) {
            partnere = new ArrayList();
        }
        return partnere;
    }

    public List<String> getBarn() {
        if (isNull(barn)) {
            barn = new ArrayList();
        }
        return barn;
    }

    public List<Person> getPersondetaljer() {
        if (isNull(persondetaljer)) {
            persondetaljer = new ArrayList();
        }
        return persondetaljer;
    }

    public Person getPerson(String ident){
        for (Person person : getPersondetaljer()){
            if (person.getIdent().equals(ident)){
                return person;
            }
        }
        return null;
    }
}
