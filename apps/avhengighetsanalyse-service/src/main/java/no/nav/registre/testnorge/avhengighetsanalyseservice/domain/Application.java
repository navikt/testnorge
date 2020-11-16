package no.nav.registre.testnorge.avhengighetsanalyseservice.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model.ApplicationModel;

@RequiredArgsConstructor
public class Application {
    private final String registeredName;

    public Application(no.nav.registre.testnorge.libs.avro.application.Application application) {
        registeredName = application.getRegisteredName();
    }

    public Application(ApplicationModel model) {
        registeredName = model.getRegisteredName();
    }

    public String getHost() {
        return "https://" + registeredName + ".dev.adeo.no";
    }

    public ApplicationModel toModel() {
        return ApplicationModel
                .builder()
                .registeredName(registeredName)
                .build();
    }

    public String getRegisteredName() {
        return registeredName;
    }
}
