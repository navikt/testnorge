package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

public abstract class PdlArtifactService<T extends PdlDbVersjon> {

    public List<T> resolve(List<T> request) {

        for (var type : request) {

            if (isNull(type.getId()) || type.getId().equals(0)) {
                validate(type);

                handle(type);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return request;
    }

    protected abstract void validate(T type);

    public abstract void handle(T type);
}
