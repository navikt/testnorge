package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GjeldendeService {

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DoedsfallService doedsfallService;
    private final FoedselService foedselService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final InnflyttingService innflyttingService;
    private final NavnService navnService;
    private final OppholdsadresseService oppholdsadresseService;
    private final OppholdService oppholdService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtflyttingService utflyttingService;
    private final FullmaktService fullmaktService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final VergemaalService vergemaalService;
    private final FalskIdentitetService falskIdentitetService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final IdenttypeService identtypeService;
    private final SivilstandService sivilstandService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final SikkerhetstiltakService sikkerhetstiltakService;

    public void update(PersonDTO mergedPerson) {

        
    }
}
