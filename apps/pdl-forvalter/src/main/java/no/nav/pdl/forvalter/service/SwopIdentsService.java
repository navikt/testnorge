package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO.Sivilstand;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static no.nav.pdl.forvalter.service.EnkelAdresseService.getStrengtFortroligKontaktadresse;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.isNotNpidIdent;
import static no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master.FREG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master.PDL;

@Service
@RequiredArgsConstructor
public class SwopIdentsService {

    private final PersonRepository personRepository;
    private final AliasRepository aliasRepository;
    private final RelasjonRepository relasjonRepository;

    private static String opaqifyIdent(String ident) {

        return 'X' + ident.substring(1);
    }

    private void swopOpplysninger(DbPerson person1, DbPerson person2) {

        var person = person1.getPerson();
        person1.setPerson(person2.getPerson());
        person2.setPerson(person);

        person1.setIdent(person1.getPerson().getIdent());
        person2.setIdent(person2.getPerson().getIdent());

        var navn = person2.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO());
        person2.setFornavn(navn.getFornavn());
        person2.setMellomnavn(navn.getMellomnavn());
        person2.setEtternavn(navn.getEtternavn());

        person1.getPerson().getSivilstand().addAll(person2.getPerson().getSivilstand());
        if (person1.getPerson().getSivilstand().size() > 1) {
            person1.getPerson().setSivilstand(person1.getPerson().getSivilstand().stream()
                    .filter(sivilstand -> !sivilstand.isUgift())
                    .toList());
        }
        person1.getPerson().getForelderBarnRelasjon().addAll(person2.getPerson().getForelderBarnRelasjon());
        person1.getPerson().getVergemaal().addAll(person2.getPerson().getVergemaal());
        person1.getPerson().getSikkerhetstiltak().addAll(person2.getPerson().getSikkerhetstiltak());
        person1.getPerson().getTelefonnummer().addAll(person2.getPerson().getTelefonnummer());
        person1.getPerson().getTilrettelagtKommunikasjon().addAll(person2.getPerson().getTilrettelagtKommunikasjon());
        person1.getPerson().getUtenlandskIdentifikasjonsnummer().addAll(person2.getPerson().getUtenlandskIdentifikasjonsnummer());
        person1.getPerson().getFalskIdentitet().addAll(person2.getPerson().getFalskIdentitet());
        person1.getPerson().getForeldreansvar().addAll(person2.getPerson().getForeldreansvar());
        person1.getPerson().getInnflytting().addAll(person2.getPerson().getInnflytting());
        person1.getPerson().setAdressebeskyttelse(person2.getPerson().getAdressebeskyttelse());
        person1.getPerson().setNavPersonIdentifikator(person2.getPerson().getNavPersonIdentifikator());

        if (person1.getPerson().isStrengtFortrolig() || person2.getPerson().isStrengtFortrolig()) {
            person1.getPerson().setBostedsadresse(null);
            person2.getPerson().setBostedsadresse(null);
            person1.getPerson().setOppholdsadresse(null);
            person2.getPerson().setOppholdsadresse(null);
            person1.getPerson().setKontaktadresse(new ArrayList<>(List.of(getStrengtFortroligKontaktadresse())));
            person2.getPerson().setKontaktadresse(new ArrayList<>(List.of(getStrengtFortroligKontaktadresse())));
        }

        var foedsel = person2.getPerson().getFoedsel().stream().findFirst().orElse(new FoedselDTO());
        var foedested = person2.getPerson().getFoedested().stream().findFirst().orElse(new FoedestedDTO());
        person1.getPerson().getFoedsel()
                .forEach(foedsel1 -> {
                    foedsel1.setFoedeland(foedsel.getFoedeland());
                    foedsel1.setFoedekommune(foedsel.getFoedekommune());
                    foedsel1.setFoedested(foedsel.getFoedested());
                });
        person1.getPerson().getFoedested()
                .forEach(foedsel1 -> {
                    foedsel1.setFoedeland(foedested.getFoedeland());
                    foedsel1.setFoedekommune(foedested.getFoedekommune());
                    foedsel1.setFoedested(foedested.getFoedested());
                });

        person1.getPerson().setNyident(null);

        if (person1.getPerson().getSivilstand().isEmpty() && FoedselsdatoUtility.isMyndig(person1.getPerson())) {
            person1.getPerson().setSivilstand(new ArrayList<>(List.of(SivilstandDTO.builder()
                    .id(1)
                    .type(Sivilstand.UGIFT)
                    .master(isNotNpidIdent(person1.getIdent()) ? FREG : PDL)
                    .kilde("Dolly")
                    .bekreftelsesdato(isNotNpidIdent(person1.getIdent()) ? null : now())
                    .build())));
        }

        relasjonRepository.saveAll(person2.getRelasjoner().stream()
                .map(relasjon -> DbRelasjon.builder()
                        .person(person1)
                        .relatertPerson(relasjon.getRelatertPerson())
                        .relasjonType(relasjon.getRelasjonType())
                        .sistOppdatert(now())
                        .build())
                .toList());
    }

    public PersonDTO execute(String ident1, String ident2) {

        var personer = personRepository.findByIdentIn(List.of(ident1, ident2),
                PageRequest.of(0, 10));
        var person1 = personer.stream()
                .filter(person -> ident1.equals(person.getIdent()))
                .findFirst();
        var person2 = personer.stream()
                .filter(person -> ident2.equals(person.getIdent()))
                .findFirst();

        if (person1.isPresent() && person2.isPresent()) {

            person1.get().setIdent(opaqifyIdent(ident1));
            person2.get().setIdent(opaqifyIdent(ident2));
            personRepository.saveAll(List.of(person1.get(), person2.get()));

            var oppdatertePersoner = personRepository.findByIdIn(List.of(person1.get().getId(), person2.get().getId()));
            var oppdatertPerson1 = oppdatertePersoner.stream()
                    .filter(person -> person1.get().getId().equals(person.getId()))
                    .findFirst();
            var oppdatertPerson2 = oppdatertePersoner.stream()
                    .filter(person -> person2.get().getId().equals(person.getId()))
                    .findFirst();

            if (oppdatertPerson1.isPresent() && oppdatertPerson2.isPresent()) {
                swopOpplysninger(oppdatertPerson1.get(), oppdatertPerson2.get());

                personRepository.saveAll(List.of(person1.get(), person2.get()));

                aliasRepository.save(DbAlias.builder()
                        .tidligereIdent(ident1)
                        .person(oppdatertPerson1.get())
                        .sistOppdatert(now())
                        .build());

                relasjonRepository.deleteAll(person2.get().getRelasjoner());

                return person1.get().getPerson();
            }
        }

        return null;
    }
}