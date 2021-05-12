package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlFoedsel;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.service.command.DatoFraIdentCommand;
import no.nav.pdl.forvalter.service.command.IdenttypeFraIdentCommand;
import no.nav.pdl.forvalter.service.command.TilfeldigKommuneCommand;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class FoedselCommand extends PdlArtifactService<PdlFoedsel> {

    private final String ident;
    private final PdlBostedadresse bostedadresse;
    private final RsInnflytting innflytting;

    public FoedselCommand(List<PdlFoedsel> request,
                          String ident,
                          PdlBostedadresse bostedadresse,
                          RsInnflytting innflytting) {
        super(request);
        this.ident = ident;
        this.bostedadresse = bostedadresse;
        this.innflytting = innflytting;
    }

    @Override
    protected void validate(PdlFoedsel foedsel) {

    }

    @Override
    public void handle(PdlFoedsel foedsel) {

        if (isNull(foedsel.getFoedselsdato())) {
            foedsel.setFoedselsdato(new DatoFraIdentCommand(ident).call().atStartOfDay());
        }
        foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());

        if (isNull(foedsel.getFoedeland())) {
            if (Identtype.FNR.equals(new IdenttypeFraIdentCommand(ident).call())) {
                foedsel.setFoedeland(NORGE);
            } else if (nonNull(innflytting)) {
                foedsel.setFoedeland(innflytting.getFraflyttingsland());
            } else {
                foedsel.setFoedeland(new TilfeldigLandCommand().call());
            }
        }

        if (NORGE.equals(foedsel.getFoedeland()) && isBlank(foedsel.getFodekommune())) {
            if (nonNull(bostedadresse.getVegadresse())) {
                foedsel.setFodekommune(bostedadresse.getVegadresse().getKommunenummer());
            } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
                foedsel.setFodekommune(bostedadresse.getMatrikkeladresse().getKommunenummer());
            } else if (nonNull(bostedadresse.getUkjentBosted())) {
                foedsel.setFodekommune(bostedadresse.getUkjentBosted().getBostedskommune());
            } else {
                foedsel.setFodekommune(new TilfeldigKommuneCommand().call());
            }
        }
    }

    @Override
    protected void enforceIntegrity(List<PdlFoedsel> type) {

    }
}
