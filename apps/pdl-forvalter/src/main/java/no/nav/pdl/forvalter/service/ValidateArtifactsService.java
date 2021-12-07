package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class ValidateArtifactsService {

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final DoedsfallService doedsfallService;
    private final FalskIdentitetService falskIdentitetService;
    private final FoedselService foedselService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final FullmaktService fullmaktService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final IdenttypeService identtypeService;
    private final InnflyttingService innflyttingService;
    private final NavnService navnService;
    private final OppholdsadresseService oppholdsadresseService;
    private final OppholdService oppholdService;
    private final SivilstandService sivilstandService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final UtflyttingService utflyttingService;
    private final VergemaalService vergemaalService;
    private final SikkerhetstiltakService sikkerhetstiltakService;

    public void validate(PersonDTO person) {

        Stream.of(
                validate(kjoennService, person.getKjoenn(), person),
                validate(innflyttingService, person.getInnflytting()),
                validate(statsborgerskapService, person.getStatsborgerskap()),
                validate(bostedAdresseService, person.getBostedsadresse(), person),
                validate(foedselService, person.getFoedsel(), person),
                validate(navnService, person.getNavn()),
                validate(oppholdsadresseService, person.getOppholdsadresse(), person),
                validate(adressebeskyttelseService, person.getAdressebeskyttelse(), person),
                validate(telefonnummerService, person.getTelefonnummer()),
                validate(utflyttingService, person.getUtflytting()),
                validate(oppholdService, person.getOpphold()),
                validate(tilrettelagtKommunikasjonService, person.getTilrettelagtKommunikasjon()),
                validate(doedsfallService, person.getDoedsfall()),
                validate(folkeregisterPersonstatusService, person.getFolkeregisterPersonstatus(), person),
                validate(fullmaktService, person.getFullmakt(), person),
                validate(kontaktAdresseService, person.getKontaktadresse(), person),
                validate(utenlandsidentifikasjonsnummerService, person.getUtenlandskIdentifikasjonsnummer()),
                validate(vergemaalService, person.getVergemaal()),
                validate(falskIdentitetService, person.getFalskIdentitet()),
                validate(kontaktinformasjonForDoedsboService, person.getKontaktinformasjonForDoedsbo()),
                validate(sivilstandService, person.getSivilstand()),
                validate(forelderBarnRelasjonService, person.getForelderBarnRelasjon()),
                validate(foreldreansvarService, person.getForeldreansvar(), person),
                validate(deltBostedService, person.getDeltBosted(), person),
                validate(sikkerhetstiltakService, person.getSikkerhetstiltak()),
                validate(doedfoedtBarnService, person.getDoedfoedtBarn()),
                validate(identtypeService, person.getNyident())
        )
                .reduce(Flux.empty(), Flux::concat)
                .collectList()
                .block();
    }

    private <T extends DbVersjonDTO> Flux<Void> validate(Validation<T> validation, List<T> artifact) {

        artifact.stream()
                .filter(type -> isTrue(type.getIsNew()))
                .forEach(validation::validate);

        return Flux.empty();
    }

    private <T extends DbVersjonDTO, R> Flux<Void> validate(BiValidation<T, R> validation, List<T> artifact, R person) {

        artifact.stream()
                .filter(type -> isTrue(type.getIsNew()))
                .forEach(type -> validation.validate(type, person));

        return Flux.empty();
    }
}
