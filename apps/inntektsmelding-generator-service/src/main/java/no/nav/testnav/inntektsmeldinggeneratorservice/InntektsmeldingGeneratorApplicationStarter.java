package no.nav.testnav.inntektsmeldinggeneratorservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InntektsmeldingGeneratorApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(InntektsmeldingGeneratorApplicationStarter.class, args);
    }
}