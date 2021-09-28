package no.nav.registre.testnorge.arena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TestnorgeArenaApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(TestnorgeArenaApplicationStarter.class, args);
    }
}
