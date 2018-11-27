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

    private final SecureRandom secureRandom;

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
        return !(navn.getFornavn() == null && navn.getEtternavn() == null)
                && (navn.getFornavn() == null || validFornavn.contains(navn.getFornavn()))
                && (navn.getEtternavn() == null || validEtternavn.contains(navn.getEtternavn()));
    }
}
