package no.nav.registre.testnorge.avhengighetsanalyseservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.avhengighetsanalyseservice.adapter.ApplicationAdapter;
import no.nav.registre.testnorge.avhengighetsanalyseservice.adapter.DependenciesAdapter;
import no.nav.registre.testnorge.libs.avro.application.Application;


@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RegisterApplicationListener {
    private final ApplicationAdapter applicationAdapter;
    private final DependenciesAdapter dependencyAdapter;

    @KafkaListener(topics = "testnorge-registrer-avhengighet-v1")
    public void register(@Payload Application application) {
        applicationAdapter.save(new no.nav.registre.testnorge.avhengighetsanalyseservice.domain.Application(application));
        dependencyAdapter.registerDependenciesHistory();
    }
}