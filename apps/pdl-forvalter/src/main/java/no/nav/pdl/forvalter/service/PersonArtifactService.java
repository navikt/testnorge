package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final DoedsfallService doedsfallService;
    private final FalskIdentitetService falskIdentitetService;
    private final FoedestedService foedestedService;
    private final FoedselService foedselService;
    private final FoedselsdatoService foedselsdatoService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final FullmaktService fullmaktService;
    private final IdenttypeService identtypeService;
    private final InnflyttingService innflyttingService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final NavnService navnService;
    private final NavPersonIdentifikatorService navPersonIdentifikatorService;
    private final OppholdService oppholdService;
    private final OppholdsadresseService oppholdsadresseService;
    private final SikkerhetstiltakService sikkerhetstiltakService;
    private final SivilstandService sivilstandService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final UtflyttingService utflyttingService;
    private final VergemaalService vergemaalService;

    public Mono<DbPerson> buildPerson(DbPerson dbPerson, Boolean relaxed) {

        // Orders below matters to some degree, don´t rearrange without checking consequences

        return falskIdentitetService.convert(dbPerson)
                .flatMap(adressebeskyttelseService::convert)
                .flatMap(foedselService::convert)
                .flatMap(foedselsdatoService::convert)
                .flatMap(kjoennService::convert)
                .flatMap(person1 -> bostedAdresseService.convert(person1, relaxed))
                .flatMap(innflyttingService::convert)
                .flatMap(foedestedService::convert)
                .flatMap(statsborgerskapService::convert)
                .flatMap(navnService::convert)
                .flatMap(oppholdsadresseService::convert)
                .flatMap(telefonnummerService::convert)
                .flatMap(utflyttingService::convert)
                .flatMap(oppholdService::convert)
                .flatMap(tilrettelagtKommunikasjonService::convert)
                .flatMap(sivilstandService::convert)
                .flatMap(doedsfallService::convert)
                .flatMap(fullmaktService::convert)
                .flatMap(person1 -> kontaktAdresseService.convert(person1, relaxed))
                .flatMap(utenlandsidentifikasjonsnummerService::convert)
                .flatMap(vergemaalService::convert)
                .flatMap(kontaktinformasjonForDoedsboService::convert)
                .flatMap(forelderBarnRelasjonService::convert)
                .flatMap(foreldreansvarService::convert)
                .flatMap(doedfoedtBarnService::convert)
                .flatMap(deltBostedService::convert)
                .flatMap(sikkerhetstiltakService::convert)
                .flatMap(navPersonIdentifikatorService::convert)
                .flatMap(folkeregisterPersonstatusService::convert)
                .flatMap(identtypeService::convert)
                .flatMap(folkeregisterPersonstatusService::convert);
    }
}
