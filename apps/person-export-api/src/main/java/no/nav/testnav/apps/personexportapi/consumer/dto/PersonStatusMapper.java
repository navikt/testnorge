package no.nav.testnav.apps.personexportapi.consumer.dto;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.personexportapi.domain.Person;

@UtilityClass
public class PersonStatusMapper {

    public static String getPersonstatus(Person person) {

        if (person.isFnr()) {
            switch (person.getPersonstatus()) {

            case "1":
                return "Bosatt";
            case "2":
                return "Utflyttet (ikke i bruk)";
            case "3":
                return "Utvandret";
            case "4":
                return "Forsvunnet";
            case "5":
                return "Død";
            case "6":
                return "Utgått fødselsnummer";
            case "7":
                return "Fødselsregistrert";
            case "8":
                return "Annullert tilgang";
            case "9":
                return "Uregistrert person";
            default:
                return null;
            }

        } else {
            switch (person.getPersonstatus()) {

            case "2":
                return "I bruk";
            case "5":
                return "Utgått, link til fødselsnummer";
            case "6":
                return "Utgått, dublett";
            case "7":
                return "Utgått, avgang";
            case "9":
                return "Utgått, korrigert til nytt D-nummer";
            default:
                return null;
            }
        }
    }
}