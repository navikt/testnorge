package no.nav.identpool.navnepool;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.navnepool.domain.Navn;
import no.nav.identpool.navnepool.domain.ValidEtternavn;
import no.nav.identpool.navnepool.domain.ValidFornavn;

@Service
@RequiredArgsConstructor
public class NavnepoolService {

    private final SecureRandom secureRandom;

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
