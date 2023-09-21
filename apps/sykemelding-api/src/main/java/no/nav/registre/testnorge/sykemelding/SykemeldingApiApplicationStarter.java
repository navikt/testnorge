package no.nav.registre.testnorge.sykemelding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "no.nav.registre.testnorge.sykemelding")
public class SykemeldingApiApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(SykemeldingApiApplicationStarter.class, args);
    }
}
