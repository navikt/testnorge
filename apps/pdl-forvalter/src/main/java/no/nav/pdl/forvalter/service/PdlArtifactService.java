package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@RequiredArgsConstructor
public abstract class PdlArtifactService<T extends PdlDbVersjon> implements Callable<List<T>> {

    private final List<T> request;

    public List<T> call() {

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
