package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
public class JobbParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String param_navn;
    private String param_tekst;
    private String param_verdi;

    // Getters and Setters
    public String hentParamNavn(){
        return param_navn;
    }
}
