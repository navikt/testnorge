package no.nav.identpool.navnepoll;

import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.identpool.navnepoll.domain.Navn;
import no.nav.identpool.navnepoll.domain.ValidEtternavn;
import no.nav.identpool.navnepoll.domain.ValidFornavn;

@Service
public class NavnepollService {
    @Autowired
    private SecureRandom secureRandom;

    public Navn finnTilfeldigNavn() {
        return new Navn(
                ValidFornavn.getNavnList().get(secureRandom.nextInt(ValidFornavn.values().length)),
                ValidEtternavn.getNavnList().get(secureRandom.nextInt(ValidEtternavn.values().length))
        );
    }

    public Boolean isValid(Navn navn) {
        return ValidFornavn.getNavnList().contains(navn.getFornavn()) && ValidEtternavn.getNavnList().contains(navn.getEtternavn());
    }
}
