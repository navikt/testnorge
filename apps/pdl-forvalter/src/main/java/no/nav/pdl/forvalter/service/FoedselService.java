package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlFoedsel;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import no.nav.pdl.forvalter.service.command.DatoFraIdentCommand;
import no.nav.pdl.forvalter.service.command.IdenttypeFraIdentCommand;
import no.nav.pdl.forvalter.service.command.TilfeldigKommuneCommand;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class FoedselService extends PdlArtifactService<PdlFoedsel> {

    public List<PdlFoedsel> convert(List<PdlFoedsel> request,
                                       String ident,
                                       PdlBostedadresse bostedadresse,
                                       RsInnflytting innflytting) {

        for (var type : request) {

            if (type.isNew()) {
                validate(type);

                handle(type, ident, bostedadresse, innflytting);
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        enforceIntegrity(request);
        return request;
    }

    public void handle(PdlFoedsel foedsel, String ident, PdlBostedadresse bostedadresse, RsInnflytting innflytting) {

        if (isNull(foedsel.getFoedselsdato())) {
            foedsel.setFoedselsdato(new DatoFraIdentCommand(ident).call().atStartOfDay());
        }
        foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());

        if (isNull(foedsel.getFoedeland())) {
            if (Identtype.FNR.equals(new IdenttypeFraIdentCommand(ident).call())) {
                foedsel.setFoedeland(NORGE);
            } else if (nonNull(innflytting)) {
                foedsel.setFoedeland(innflytting.getFraflyttingsland());
            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                foedsel.setFoedeland(bostedadresse.getUtenlandskAdresse().getLandkode());
            } else {
                foedsel.setFoedeland(new TilfeldigLandCommand().call());
            }
        }

        if (NORGE.equals(foedsel.getFoedeland()) && isBlank(foedsel.getFodekommune())) {
            if (nonNull(bostedadresse)) {
                if (nonNull(bostedadresse.getVegadresse())) {
                    foedsel.setFodekommune(bostedadresse.getVegadresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
                    foedsel.setFodekommune(bostedadresse.getMatrikkeladresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getUkjentBosted())) {
                    foedsel.setFodekommune(bostedadresse.getUkjentBosted().getBostedskommune());
                }
            } else {
                foedsel.setFodekommune(new TilfeldigKommuneCommand().call());
            }
        }
    }

    @Override
    protected void validate(PdlFoedsel foedsel) {

    }

    @Override
    protected void handle(PdlFoedsel type) {

    }

    @Override
    protected void enforceIntegrity(List<PdlFoedsel> type) {

    }
}
