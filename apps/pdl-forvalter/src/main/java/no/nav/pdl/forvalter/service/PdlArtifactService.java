package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.util.List;
import java.util.concurrent.Callable;

import static org.apache.logging.log4j.util.Strings.isBlank;

@RequiredArgsConstructor
public abstract class PdlArtifactService<T extends PdlDbVersjon> implements Callable<List<T>> {

    private final List<T> request;

    public List<T> call() {

        for (var type : request) {

            if (type.isNew()) {
                validate(type);

                handle(type);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        enforceIntegrity(request);
        return request;
    }

    protected abstract void validate(T type);

    protected abstract void handle(T type);

    protected abstract void enforceIntegrity(List<T> type);

    protected boolean isInitial() {
        return request.size() == 1;
    }
}
