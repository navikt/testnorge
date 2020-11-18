package no.nav.dolly.dbmigrate.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import no.nav.dolly.domain.jpa.oracle.OraBruker;
import no.nav.dolly.domain.jpa.postgres.Bruker;

public interface MigrationService {

    void migrate();

    static String getBrukerId(Bruker bruker) {

        return nonNull(bruker.getBrukerId()) ? bruker.getBrukerId() : bruker.getNavIdent();
    }

    static String getBrukerId(OraBruker bruker) {

        if (isNull(bruker)) {
            return null;
        }
        return nonNull(bruker.getBrukerId()) ? bruker.getBrukerId() : bruker.getNavIdent();
    }
}
