package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DollyPerson {

    private FullPersonDTO pdlfPerson;

    private List<Person> persondetaljer;

    private String hovedperson;
    private List<String> partnere;
    private List<String> barn;
    private List<String> foreldre;
    private List<String> nyePartnereOgBarn;
    private List<String> verger;
    private List<String> fullmektige;
    private List<String> identhistorikk;
    private Testident.Master master;

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

    public List<String> getForeldre() {
        if (isNull(foreldre)) {
            foreldre = new ArrayList<>();
        }
        return foreldre;
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

    public boolean isTpsfMaster() {
        return getMaster() == Testident.Master.TPSF;
    }

    public boolean isPdlMaster() {
        return getMaster() == Testident.Master.PDL;
    }

    public boolean isPdlfMaster() {
        return getMaster() == Testident.Master.PDLF;
    }
}
