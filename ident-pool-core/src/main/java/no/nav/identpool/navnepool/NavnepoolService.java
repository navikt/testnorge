package no.nav.identpool.navnepool;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.navnepool.domain.Navn;

@Service
@RequiredArgsConstructor
public class NavnepoolService {

    private final List<String> validFornavn;

    private final List<String> validEtternavn;

    private SecureRandom secureRandom = new SecureRandom();

    public List<Navn> hentTilfeldigeNavn(Integer antall) {
        List<Navn> navneliste = new ArrayList<>(antall);
        for (int i = 0; i < antall; i++) {
            navneliste.add(hentTilfeldigNavn());
        }
        return navneliste;
    }

    public Navn hentTilfeldigNavn() {
        return new Navn(
                validFornavn.get(secureRandom.nextInt(validFornavn.size())),
                validEtternavn.get(secureRandom.nextInt(validEtternavn.size()))
        );
    }

    public Boolean isValid(Navn navn) {
        return areBothNotNull(navn) && isFornavnOrEtternavnValid(navn);
    }

    /**
     * Forklaring av NOSONAR: Sonar foreslår Set i stedet for List.
     * Sonar tar ikke hensyn til at listene skal brukes med get i tjenesten hentTilfeldigNavn: validEtternavn.get(randomInt).
     * http://freg-sonar.adeo.no/coding_rules#rule_key=fb-contrib%3ADLC_DUBIOUS_LIST_COLLECTION
     */
    private boolean isFornavnOrEtternavnValid(Navn navn) {
        return (navn.getFornavn() == null || validFornavn.contains(navn.getFornavn())) //NOSONAR
                && (navn.getEtternavn() == null || validEtternavn.contains(navn.getEtternavn()));//NOSONAR
    }

    private boolean areBothNotNull(Navn navn) {
        return !(navn.getFornavn() == null && navn.getEtternavn() == null);
    }
}
