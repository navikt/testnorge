package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlKjoenn;
import no.nav.pdl.forvalter.service.command.KjoennFraIdentCommand;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public class KjoennService extends PdlArtifactService<PdlKjoenn> {

    public List<PdlKjoenn> convert(List<PdlKjoenn> request,
                         String ident) {

        for (var type : request) {

            if (type.isNew()) {
                validate(type);

                handle(type, ident);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        enforceIntegrity(request);
        return request;
    }

    private void handle(PdlKjoenn kjoenn, String ident) {

        if (isNull(kjoenn.getKjoenn())) {
            kjoenn.setKjoenn(new KjoennFraIdentCommand(ident).call());
        }
    }

    @Override
    protected void validate(PdlKjoenn kjoenn) {

    }

    @Override
    protected void handle(PdlKjoenn type) {

    }

    @Override
    protected void enforceIntegrity(List<PdlKjoenn> type) {

    }
}
