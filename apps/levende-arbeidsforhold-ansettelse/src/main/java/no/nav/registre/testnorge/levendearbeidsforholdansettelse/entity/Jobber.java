package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@Entity
public class Jobber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Boolean aktiv;
    private String intervall;
    private int antpersoner;
    private int antbedrifter;

    // Getters and Setters
    public String hentID(){
        return id;
    }
}
