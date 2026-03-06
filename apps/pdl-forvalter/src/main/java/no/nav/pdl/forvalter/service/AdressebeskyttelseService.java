package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.pdl.forvalter.service.EnkelAdresseService.getStrengtFortroligKontaktadresse;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class AdressebeskyttelseService implements BiValidation<AdressebeskyttelseDTO, PersonDTO> {

    private static final String VALIDATION_UTLAND_MASTER_ERROR = "Adressebeskyttelse: Gradering STRENGT_FORTROLIG_UTLAND " +
            "kan kun settes hvis master er PDL";
    private static final String VALIDATION_INVALID_BESKYTTELSE = "Adressebeskyttelse: Gradering " +
            "FORTROLIG kan kun settes på personer med fødselsnummer";

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getAdressebeskyttelse())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .then();
    }

    @Override
    public Mono<Void> validate(AdressebeskyttelseDTO adressebeskyttelse, PersonDTO person) {

        if (FNR != IdenttypeUtility.getIdenttype(person.getIdent()) &&
                FORTROLIG == adressebeskyttelse.getGradering()) {
            throw new InvalidRequestException(VALIDATION_INVALID_BESKYTTELSE);
        }
        if (STRENGT_FORTROLIG_UTLAND == adressebeskyttelse.getGradering() &&
                Master.FREG == adressebeskyttelse.getMaster()) {
            throw new InvalidRequestException(VALIDATION_UTLAND_MASTER_ERROR);
        }
        return Mono.empty();
    }

    private Mono<AdressebeskyttelseDTO> handle(AdressebeskyttelseDTO adressebeskyttelse, PersonDTO person) {

        if (STRENGT_FORTROLIG_UTLAND == adressebeskyttelse.getGradering()) {
            adressebeskyttelse.setMaster(Master.PDL);
        }

        if (STRENGT_FORTROLIG == adressebeskyttelse.getGradering()) {
            person.setBostedsadresse(null);
            person.setOppholdsadresse(null);
            person.setKontaktadresse(null);
            person.getKontaktadresse().add(getStrengtFortroligKontaktadresse());
        }
        return Mono.empty();
    }
}
