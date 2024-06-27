package no.nav.registre.testnorge.levendearbeidsforhold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import no.nav.registre.testnorge.levendearbeidsforhold.listener.DoedsfallListener;


@SpringBootApplication
public class LevendeArbeidsforholdServiceApplicationStarter {
    
    public static void main(String[] args) {
        SpringApplication.run(LevendeArbeidsforholdServiceApplicationStarter.class, args);
    }
}