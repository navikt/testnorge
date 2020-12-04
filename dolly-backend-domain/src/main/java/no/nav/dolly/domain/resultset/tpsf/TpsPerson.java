package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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
    private List<String> verger;
    private List<String> fullmektige;
    private List<String> identhistorikk;

    public List<String> getPartnere() {
        if (isNull(partnere)) {
            partnere = new ArrayList<>();
        }
        return partnere;
    }

    public List<String> getBarn() {
        if (isNull(barn)) {
            barn = new ArrayList<>();
        }
        return barn;
    }

    public List<String> getVerger() {
        if (isNull(verger)) {
            verger = new ArrayList<>();
        }
        return verger;
    }

    public List<String> getFullmektige() {
        if (isNull(fullmektige)) {
            fullmektige = new ArrayList<>();
        }
        return fullmektige;
    }

    public List<String> getIdenthistorikk() {
        if (isNull(identhistorikk)) {
            identhistorikk = new ArrayList<>();
        }
        return identhistorikk;
    }

    public List<Person> getPersondetaljer() {
        if (isNull(persondetaljer)) {
            persondetaljer = new ArrayList<>();
        }
        return persondetaljer;
    }

    public Person getPerson(String ident) {
        for (Person person : getPersondetaljer()) {
            if (person.getIdent().equals(ident)) {
                return person;
            }
        }
        return null;
    }
}
