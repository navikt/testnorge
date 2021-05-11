package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlKjoenn;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.service.command.KjoennFraIdentCommand;

import java.util.List;

import static java.util.Objects.isNull;

public class KjoennCommand extends PdlArtifactService<PdlKjoenn> {

    private  final String ident;

    public KjoennCommand(List<PdlKjoenn> request,
                         String ident) {
        super(request);
        this.ident = ident;
    }

    @Override
    protected void validate(PdlKjoenn kjoenn) {

    }

    @Override
    protected void handle(PdlKjoenn kjoenn) {

        if (isNull(kjoenn.getKjoenn())) {
            kjoenn.setKjoenn(new KjoennFraIdentCommand(ident).call());
        }
    }

    @Override
    protected void enforceIntegrity(List<PdlKjoenn> type) {

    }
}
