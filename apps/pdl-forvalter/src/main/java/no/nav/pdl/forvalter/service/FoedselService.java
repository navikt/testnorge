package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlFoedsel;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.pdl.forvalter.utils.TilfeldigKommuneService;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class FoedselService extends PdlArtifactService<PdlFoedsel> {

    private final TilfeldigKommuneService tilfeldigKommuneService;
    private final TilfeldigLandService tilfeldigLandService;

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
            foedsel.setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
        }
        foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());

        if (isNull(foedsel.getFoedeland())) {
            if (Identtype.FNR.equals(IdenttypeFraIdentUtility.getIdenttype(ident))) {
                foedsel.setFoedeland(NORGE);
            } else if (nonNull(innflytting)) {
                foedsel.setFoedeland(innflytting.getFraflyttingsland());
            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                foedsel.setFoedeland(bostedadresse.getUtenlandskAdresse().getLandkode());
            } else {
                foedsel.setFoedeland(tilfeldigLandService.getLand());
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
                foedsel.setFodekommune(tilfeldigKommuneService.getKommune());
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
